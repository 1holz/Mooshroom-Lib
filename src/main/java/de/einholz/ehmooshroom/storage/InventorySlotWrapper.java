package de.einholz.ehmooshroom.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;

@Deprecated // TODO remove if unused
// XXX mixin fabrics InventorySlotWrapper?
public class InventorySlotWrapper extends SingleStackStorage {
    private final OldAdvItemStorage storage;
    private final int slot;
    private ItemStack lastReleasedSnapshot = null;

    public InventorySlotWrapper(OldAdvItemStorage storage, int slot) {
        this.storage = storage;
        this.slot = slot;
    }

    @Override
    public ItemStack getStack() {
        return storage.inventory.getStack(slot);
    }

    @Override
    public void setStack(ItemStack stack) {
        storage.inventory.setStack(slot, stack);
    }

    @Override
    public boolean canInsert(ItemVariant itemVariant) {
        return storage.inventory.isValid(slot, itemVariant.toStack());
    }

    @Override
    public int getCapacity(ItemVariant variant) {
        return Math.min(storage.inventory.getMaxCountPerStack(), variant.getItem().getMaxCount());
    }

    @Override
    public void updateSnapshots(TransactionContext transaction) {
        storage.markDirtyParticipant.updateSnapshots(transaction);
        super.updateSnapshots(transaction);
    }

    @Override
    public void releaseSnapshot(ItemStack snapshot) {
        lastReleasedSnapshot = snapshot;
    }

    @Override
    public void onFinalCommit() {
        ItemStack original = lastReleasedSnapshot;
        ItemStack currentStack = getStack();
        if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
            original.setCount(currentStack.getCount());
            original.setNbt(currentStack.hasNbt() ? currentStack.getNbt().copy() : null);
            setStack(original);
        } else original.setCount(0);
    }
}
