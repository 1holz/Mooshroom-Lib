package de.einholz.ehmooshroom.block.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import de.einholz.ehmooshroom.storage.SidedStorageMgr;
import de.einholz.ehmooshroom.storage.StorageProv;
import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.storage.storages.AdvCombinedStorage;
import de.einholz.ehmooshroom.util.NbtSerializable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ContainerBE extends BlockEntity
        implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, StorageProv, NbtSerializable {
    protected final ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    private SidedStorageMgr storageMgr = new SidedStorageMgr(this);
    private Map<Transferable<?, ? extends TransferVariant<?>>, Long> transfer;
    private final Map<Transferable<?, ? extends TransferVariant<?>>, Long> maxTransfer = new HashMap<>();
    private boolean dirty = false;

    public ContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state,
            ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state);
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public SidedStorageMgr getStorageMgr() {
        return storageMgr;
    }

    // XXX make ticker static in general?
    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (be instanceof ContainerBE containerBE)
            containerBE.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        resetDitry();
        transfer();
        if (isDirty())
            markDirty();
    }

    @SuppressWarnings("null")
    public void transfer() {
        if (world.isClient)
            return;
        resetTransfer();
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Direction targetDir = dir.getOpposite();
            // self output / push
            for (var entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, true, dir))) {
                if (!entry.trans.isTransferable())
                    continue;
                Storage<?> targetStorage = entry.trans.getLookup().find(world, targetPos, targetDir);
                if (targetStorage == null)
                    continue;
                if (transfer(entry.trans, entry.storage, targetStorage, getTransfer(), reduceTransfer()))
                    setDirty();
            }
            // self input / pull
            for (var entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, false, dir))) {
                if (!entry.trans.isTransferable())
                    continue;
                Storage<?> targetStorage = entry.trans.getLookup().find(world, targetPos, targetDir);
                if (targetStorage == null)
                    continue;
                if (transfer(entry.trans, targetStorage, entry.storage, getTransfer(), reduceTransfer()))
                    setDirty();
            }
        }
        // TODO is there a way to only sync if the gui is opened?
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), Block.NOTIFY_LISTENERS);
    }

    // TODO merge with ProcessingBE consume(…) and generate(…) (atleast partially)
    @SuppressWarnings("unchecked")
    public static <T> Boolean transfer(final Transferable<T, ? extends TransferVariant<T>> transferable,
            final Storage<?> fromRaw, final Storage<?> toRaw,
            final ToLongFunction<Transferable<?, ? extends TransferVariant<?>>> transGetter,
            final BiConsumer<Transferable<?, ? extends TransferVariant<?>>, Long> transReducer) {
        // FIXME fix bug with StorageEntries appearing double in fromRaw
        final Storage<T> from;
        final Storage<T> to;
        try {
            from = (Storage<T>) fromRaw;
            to = (Storage<T>) toRaw;
        } catch (ClassCastException e) {
            MooshroomLib.LOGGER.smallBug(new IllegalArgumentException(
                    "Types of storages do not match. Probably due to wrong BlockApiLookup.", e));
            return false;
        }
        if (!from.supportsExtraction() || !to.supportsInsertion())
            return false;
        boolean dirty = false;
        try (Transaction trans = Transaction.openOuter()) {
            Iterator<StorageView<T>> it = from.iterator(trans);
            while (it.hasNext()) {
                StorageView<T> view = it.next();
                if (view.isResourceBlank())
                    continue;
                T resource = view.getResource();
                long remainingTransfer = transGetter.applyAsLong(transferable);
                if (remainingTransfer == 0)
                    continue;
                try (Transaction inTrans = trans.openNested()) {
                    long extracted = view.extract(resource, remainingTransfer, inTrans);
                    if (extracted == 0)
                        continue;
                    long inserted = to.insert(resource, extracted, inTrans);
                    if (inserted != extracted && to.insert(resource, inserted, inTrans) != inserted) {
                        MooshroomLib.LOGGER.smallBug(new IllegalStateException("Transfer could not be completed."));
                        inTrans.abort();
                        continue;
                    }
                    transReducer.accept(transferable, inserted);
                    dirty = true;
                    inTrans.commit();
                }
            }
            trans.commit();
        }
        return dirty;
    }

    public ContainerBE putMaxTransfer(Transferable<?, ? extends TransferVariant<?>> trans, long l) {
        maxTransfer.put(trans, l);
        return this;
    }

    public ContainerBE removeMaxTransfer(Transferable<?, ? extends TransferVariant<?>> trans) {
        maxTransfer.remove(trans);
        return this;
    }

    public long getMaxTransfer(Transferable<?, ? extends TransferVariant<?>> trans) {
        return maxTransfer.getOrDefault(trans, 0L);
    }

    public ToLongFunction<Transferable<?, ? extends TransferVariant<?>>> getTransfer() {
        return (trans) -> transfer.getOrDefault(trans, 0L);
    }

    public BiConsumer<Transferable<?, ? extends TransferVariant<?>>, Long> reduceTransfer() {
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

    @Override
    public <T, V extends TransferVariant<T>> AdvCombinedStorage<T, V, Storage<V>> getStorage(Transferable<T, V> trans,
            @Nullable Direction dir) {
        SideConfigAccessor acc = SideConfigAccessor.getFromDir(dir);
        return getStorageMgr().getCombinedStorage(trans, acc, SideConfigType.getFromParams(true, false, acc),
                SideConfigType.getFromParams(true, true, acc));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        getStorageMgr().writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        getStorageMgr().readNbt(nbt);
    }

    // TODO check what is necessary
    @Override
    public void fromClientTag(NbtCompound nbt) {
        readNbt(nbt);
    }

    // TODO check what is necessary
    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        return writeNbt(nbt);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return clientHandlerFactory.create(syncId, inv, buf);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
