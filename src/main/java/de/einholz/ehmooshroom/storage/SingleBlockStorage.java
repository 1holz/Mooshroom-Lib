package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.BlockVariant;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class SingleBlockStorage extends SingleVariantStorage<BlockVariant> implements NbtSerializable {
    private final BlockEntity dirtyMarker;

    public SingleBlockStorage(BlockEntity dirtyMarker) {
        this.dirtyMarker = dirtyMarker;
    }

    @Override
    protected BlockVariant getBlankVariant() {
        return BlockVariant.blank();
    }

    @Override
    protected long getCapacity(BlockVariant variant) {
        return isResourceBlank() ? 1 : 0;
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        dirtyMarker.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("Block", getResource().toNbt());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        variant = BlockVariant.fromNbt(nbt.getCompound("Block"));
    }

    @Override
    public long insert(BlockVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        if (!supportsInsertion())
            return 0;
        return super.insert(insertedVariant, maxAmount, transaction);
    }

    @Override
    public long extract(BlockVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        if (!supportsExtraction())
            return 0;
        return super.extract(extractedVariant, maxAmount, transaction);
    }

    public BlockEntity getDirtyMarker() {
        return dirtyMarker;
    }
}
