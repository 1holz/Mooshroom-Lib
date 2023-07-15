package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public final class PosAsInv implements Inventory {
    public final BlockPos POS;

    public PosAsInv(final BlockPos POS) {
        this.POS = POS;
    }

    private void error() {
        MooshroomLib.LOGGER.smallBug(new UnsupportedOperationException("This inventory represents a BlockPos."));
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public void clear() {
        error();
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        error();
        return false;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public ItemStack getStack(int slot) {
        error();
        return ItemStack.EMPTY;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public boolean isEmpty() {
        error();
        return true;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public void markDirty() {
        error();
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public ItemStack removeStack(int slot) {
        error();
        return ItemStack.EMPTY;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public ItemStack removeStack(int slot, int amount) {
        error();
        return ItemStack.EMPTY;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public void setStack(int slot, ItemStack stack) {
        error();
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public int size() {
        error();
        return 0;
    }
}
