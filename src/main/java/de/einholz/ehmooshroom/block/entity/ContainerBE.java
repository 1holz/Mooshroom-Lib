/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.block.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.BlockApiLookups;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import de.einholz.ehmooshroom.storage.SidedStorageMgr;
import de.einholz.ehmooshroom.storage.StorageProv;
import de.einholz.ehmooshroom.storage.storages.AdvCombinedStorage;
import de.einholz.ehmooshroom.storage.storages.BarStorage;
import de.einholz.ehmooshroom.util.NbtSerializable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType.ExtendedFactory;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ContainerBE extends BlockEntity
        implements ExtendedScreenHandlerFactory, StorageProv, NbtSerializable {
    protected final ExtendedFactory<? extends ScreenHandler> clientHandlerFactory;
    private SidedStorageMgr storageMgr = new SidedStorageMgr(this);
    private Map<Identifier, Long> transfer;
    private final Map<Identifier, Long> maxTransfer = new HashMap<>();
    private boolean dirty = false;
    private ScreenHandler screenHandler;

    public ContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state,
            ExtendedFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state);
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public SidedStorageMgr getStorageMgr() {
        return storageMgr;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (be instanceof ContainerBE containerBE)
            containerBE.tick(world, pos, state);
    }

    protected void tick(World world, BlockPos pos, BlockState state) {
        resetDitry();
        transfer();
        if (isDirty())
            markDirty();
        updateBalances();
    }

    protected void transfer() {
        if (world.isClient)
            return;
        resetTransfer();
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Direction targetDir = dir.getOpposite();
            // self output / push
            for (var entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, true, dir))) {
                // if (!entry.getTransferable().isTransferable())
                // continue;
                Storage<?> targetStorage = BlockApiLookups.getOrMake(entry.getTransferId()).find(world, targetPos,
                        targetDir);
                if (targetStorage == null)
                    continue;
                if (transfer(entry.getTransferId(), entry.getStorage(), targetStorage, getTransfer(),
                        reduceTransfer()))
                    setDirty();
            }
            // self input / pull
            for (var entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, false, dir))) {
                // if (!entry.getTransferable().isTransferable())
                // continue;
                Storage<?> targetStorage = BlockApiLookups.getOrMake(entry.getTransferId()).find(world, targetPos,
                        targetDir);
                if (targetStorage == null)
                    continue;
                if (transfer(entry.getTransferId(), targetStorage, entry.getStorage(), getTransfer(),
                        reduceTransfer()))
                    setDirty();
            }
        }
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
        if (screenHandler == null) {// } || screenHandler instanceof ContainerGui gui && !gui.isOpen()) {
            screenHandler = null;
            return;
        }
        // these seem to be unnecessary now
        // screenHandler.enableSyncing();
        // screenHandler.sendContentUpdates();
        // screenHandler.updateToClient();
    }

    // TODO merge with ProcessingBE consume(…) and generate(…) (atleast partially)
    @SuppressWarnings("unchecked")
    public static <T> Boolean transfer(Identifier typeId, Storage<?> fromRaw, Storage<?> toRaw,
            ToLongFunction<Identifier> transGetter, BiConsumer<Identifier, Long> transReducer) {
        Storage<T> from;
        Storage<T> to;
        try {
            from = (Storage<T>) fromRaw;
            to = (Storage<T>) toRaw;
        } catch (ClassCastException e) {
            MooshroomLib.LOGGER.errorBug("Types of storages do not match. Probably due to wrong BlockApiLookup", e);
            return false;
        }
        if (!from.supportsExtraction() || !to.supportsInsertion())
            return false;
        boolean dirty = false;
        try (Transaction trans = Transaction.openOuter()) {
            Iterator<? extends StorageView<T>> it = from.iterator(trans);
            while (it.hasNext()) {
                StorageView<T> view = it.next();
                if (view.isResourceBlank())
                    continue;
                T resource = view.getResource();
                long remainingTransfer = transGetter.applyAsLong(typeId);
                if (remainingTransfer == 0)
                    continue;
                try (Transaction inTrans = trans.openNested()) {
                    long extracted = view.extract(resource, remainingTransfer, inTrans);
                    if (extracted == 0)
                        continue;
                    long inserted = to.insert(resource, extracted, inTrans);
                    if (inserted != extracted && to.insert(resource, inserted, inTrans) != inserted) {
                        MooshroomLib.LOGGER.warnBug("Transfer could not be completed.");
                        inTrans.abort();
                        continue;
                    }
                    transReducer.accept(typeId, inserted);
                    dirty = true;
                    inTrans.commit();
                }
            }
            trans.commit();
        }
        return dirty;
    }

    public ContainerBE putMaxTransfer(Identifier trans, long l) {
        maxTransfer.put(trans, l);
        return this;
    }

    public ContainerBE removeMaxTransfer(Identifier trans) {
        maxTransfer.remove(trans);
        return this;
    }

    public long getMaxTransfer(Identifier trans) {
        return maxTransfer.getOrDefault(trans, 0L);
    }

    public ToLongFunction<Identifier> getTransfer() {
        return (trans) -> transfer.getOrDefault(trans, 0L);
    }

    public BiConsumer<Identifier, Long> reduceTransfer() {
        return (trans, reduction) -> {
            if (transfer.containsKey(trans))
                transfer.put(trans, getTransfer().applyAsLong(trans) - reduction);
        };
    }

    public void resetTransfer() {
        transfer = new HashMap<>(maxTransfer);
    }

    protected boolean isDirty() {
        return dirty;
    }

    protected void setDirty() {
        dirty = true;
    }

    protected void resetDitry() {
        dirty = false;
    }

    protected void updateBalances() {
        for (Identifier id : getStorageMgr().getIds())
            if (getStorageMgr().getEntry(id).getStorage() instanceof BarStorage storage)
                storage.updateBal();
    }

    @Override
    public AdvCombinedStorage<Object, TransferVariant<Object>, Storage<TransferVariant<Object>>> getStorage(
            Identifier id, @Nullable Direction dir) {
        SideConfigAccessor acc = SideConfigAccessor.getFromDir(dir);
        return getStorageMgr().getCombinedStorage(id, acc, SideConfigType.getFromParams(true, false, acc),
                SideConfigType.getFromParams(true, true, acc));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        getStorageMgr().writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        getStorageMgr().readNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        screenHandler = clientHandlerFactory.create(syncId, inv, buf);
        return screenHandler;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
