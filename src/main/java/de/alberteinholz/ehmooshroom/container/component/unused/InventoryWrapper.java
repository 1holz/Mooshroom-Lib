/*
package de.alberteinholz.ehmooshroom.container.component.unused;

import java.util.stream.IntStream;

import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

//Own InventoryWrapper because CU's doesn't work for me
@Deprecated
public class InventoryWrapper implements SidedInventory {
    //TODO: split the 2 purposes
    public final InventoryComponent component;
    public final BlockPos pos;

    public InventoryWrapper(InventoryComponent component) {
        this.component = component;
        this.pos = null;
    }

    public InventoryWrapper(BlockPos pos) {
        this.component = null;
        this.pos = pos;
    }

    public ContainerInventoryComponent getContainerInventoryComponent() throws UnsupportedOperationException {
        if (component instanceof ContainerInventoryComponent) return (ContainerInventoryComponent) component;
        else {
            UnsupportedOperationException exception = new UnsupportedOperationException("Component is not a ContainerInventoryComponent");
            TechMod.LOGGER.bigBug(exception);
            throw exception;
        }
    }

    @Override
    public int size() {
        return component.getSize();
    }

    @Override
    public boolean isEmpty() {
        return component.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return component.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return component.takeStack(slot, amount, ActionType.PERFORM);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return component.removeStack(slot, ActionType.PERFORM);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        component.setStack(slot, stack);
    }

    @Override
    public void markDirty() {
        component.onChanged();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        component.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (component instanceof ContainerInventoryComponent) return IntStream.range(0, ((ContainerInventoryComponent) component).size()).filter(slot -> ((ContainerInventoryComponent) component).isSlotAvailable(slot, side)).toArray();
        //XXX: Update on UC update
        else return IntStream.range(0, component.getSize()).filter(slot -> component.canInsert(slot) || component.canExtract(slot)).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (component instanceof ContainerInventoryComponent) return ((ContainerInventoryComponent) component).canInsert(slot, dir);
        else return component.canInsert(slot);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (component instanceof ContainerInventoryComponent) return ((ContainerInventoryComponent) component).canExtract(slot,dir);
        else return component.canExtract(slot);
    }
}
*/