package de.einholz.ehmooshroom.block.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ToLongFunction;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.SidedStorageMgr;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.SideConfigType;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.StorageEntry;
import de.einholz.ehmooshroom.storage.providers.FluidStorageProv;
import de.einholz.ehmooshroom.storage.providers.ItemStorageProv;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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

public class ContainerBE extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, ItemStorageProv, FluidStorageProv {
    protected final ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    private SidedStorageMgr storageMgr = new SidedStorageMgr();
    private Map<Transferable<?>, Long> transfer = new HashMap<>();
    private Map<Transferable<?>, Long> maxTransfer = new HashMap<>();
    private boolean dirty = false;
    
    public ContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state, ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state);
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public SidedStorageMgr getStorageMgr() {
        return storageMgr;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {
        if (!(be instanceof ContainerBE containerBE)) return;
        containerBE.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        resetDitry();
        transfer();
        if (isDirty()) markDirty();
    }

    @SuppressWarnings("null")
    public void transfer() {
        resetTransfer();
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Direction targetDir = dir.getOpposite();
            // self output / pull
            for (StorageEntry<?> entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, true, dir))) {
                if (!entry.trans.isTransferable()) continue;
                Storage<?> targetStorage = entry.trans.lookup.find(world, targetPos, targetDir);
                if (targetStorage == null) continue;
                if (transfer(entry.trans, targetStorage, entry.storage, getTransfer(), reduceTransfer())) setDirty();;
            }
            // self input / push
            for (StorageEntry<?> entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, false, dir))) {
                if (!entry.trans.isTransferable()) continue;
                Storage<?> targetStorage = entry.trans.lookup.find(world, targetPos, targetDir);
                if (targetStorage == null) continue;
                if (transfer(entry.trans, entry.storage, targetStorage, getTransfer(), reduceTransfer())) setDirty();;
            }
//          @SuppressWarnings("unchecked")
//          TransportingComponent<Component> comp = (TransportingComponent<Component>) entry.getValue();
//          BlockComponentHook hook = BlockComponentHook.INSTANCE;
//          if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_INPUT, dir)) {
//              if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.pull(hook.getInvComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//              if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.pull(hook.getTankComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//              if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.pull(hook.getCapComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//          }
//          if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_OUTPUT, dir)) {
//              if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//              if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.push(hook.getTankComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//              if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.push(hook.getCapComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//          }
        }
        // TODO: only for early development replace with proper creative battery
        //if (getMachineInvComp().getStack(getMachineInvComp().getIntFromId(MooshroomLib.HELPER.makeId("power_input"))).getItem().equals(Items.BEDROCK) && getMachineCapacitorComp().getCurrentEnergy() < getMachineCapacitorComp().getMaxEnergy()) getMachineCapacitorComp().generateEnergy(world, pos, getMachineCapacitorComp().getPreferredType().getMaximumTransferSize());
    }

    @SuppressWarnings("unchecked")
    public static <T> Boolean transfer(final Transferable<T> transferable, final Storage<?> fromRaw, final Storage<?> toRaw, final ToLongFunction<Transferable<?>> transGetter, final BiConsumer<Transferable<?>, Long> transReducer) {
        final Storage<T> from;
        final Storage<T> to;
        try {
            from = (Storage<T>) fromRaw;
            to = (Storage<T>) toRaw;
        } catch (ClassCastException e) {
            MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("Types of storages do not match. Probably due to wrong BlockApiLookup."));
            return false;
        }
        if (!from.supportsExtraction() || !to.supportsInsertion()) return false;
        boolean dirty = false;
        try (Transaction trans = Transaction.openOuter()) {
            Iterator<StorageView<T>> it = from.iterator(trans);
            while (it.hasNext()) {
                StorageView<T> view = it.next();
                if (view.isResourceBlank()) continue;
                T resource = view.getResource();
                long remainingTransfer = transGetter.applyAsLong(transferable);
                if (remainingTransfer == 0) continue;
                try (Transaction inTrans = trans.openNested()) {
                    long extracted = view.extract(resource, remainingTransfer, inTrans);
                    if (extracted == 0) continue;
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

    public void setMaxTransfer(Map<Transferable<?>, Long> maxTransfer) {
        this.maxTransfer = maxTransfer;
        resetTransfer();
    }

    public ToLongFunction<Transferable<?>> getTransfer() {
        return (trans) -> transfer.getOrDefault(trans, 0L);
    }

    public BiConsumer<Transferable<?>, Long> reduceTransfer() {
        return (trans, reduction) -> {
            if (transfer.containsKey(trans)) transfer.put(trans, getTransfer().applyAsLong(trans) - reduction);
        };
    }

    public void resetTransfer() {
        transfer = maxTransfer;
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
    public Storage<ItemVariant> getItemStorage(/*@Nullable*/ Direction dir) {
        return getStorageMgr().getCombinedStorage(TransferablesReg.ITEMS, dir == null ? null : SideConfigType.getFromParams(true, false, dir), SideConfigType.getFromParams(true, true, dir));
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(/*@Nullable*/ Direction dir) {
        return getStorageMgr().getCombinedStorage(TransferablesReg.FLUIDS, dir == null ? null : SideConfigType.getFromParams(true, false, dir), SideConfigType.getFromParams(true, true, dir));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt = super.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound nbt) {
        readNbt(nbt);
    }

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

    @Deprecated // TODO del if unused
    public static BlockApiProvider<Storage<ItemVariant>, Direction> getItemStorageProv(BlockEntityType<? extends BlockEntity> type) {
        return (world, pos, state, null_be, dir) -> ((ItemStorageProv) type.get(world, pos)).getItemStorage(dir);
    }

    @Deprecated // TODO del if unused
    public static BlockApiProvider<Storage<FluidVariant>, Direction> getFluidStorageProv(BlockEntityType<? extends BlockEntity> type) {
        return (world, pos, state, null_be, dir) -> ((FluidStorageProv) type.get(world, pos)).getFluidStorage(dir);
    }
}
