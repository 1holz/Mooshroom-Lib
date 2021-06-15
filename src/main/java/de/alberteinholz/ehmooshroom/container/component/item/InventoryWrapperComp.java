package de.alberteinholz.ehmooshroom.container.component.item;

import java.util.stream.IntStream;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

//Own InventoryWrapper because CU's doesn't work for me
//TODO: move to mooshroomlib
public class InventoryWrapperComp implements SidedInventory {
    public final InventoryComponent component;

    public InventoryWrapperComp(InventoryComponent component) {
        this.component = component;
    }

    public AdvancedInventoryComponent getAdvancedInvComp() throws UnsupportedOperationException {
        if (component instanceof AdvancedInventoryComponent) return (AdvancedInventoryComponent) component;
        UnsupportedOperationException e = new UnsupportedOperationException("Component is not a AdvancedInventoryComponent");
        MooshroomLib.LOGGER.bigBug(e);
        throw e;
    }

    @Override
    public int size() {
        return component.size();
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
        return component.removeStack(slot, amount, ActionType.PERFORM);
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
        if (component instanceof AdvancedInventoryComponent) return IntStream.range(0, ((AdvancedInventoryComponent) component).size()).filter(slot -> ((AdvancedInventoryComponent) component).isSlotAvailable(slot, side)).toArray();
        else return IntStream.range(0, component.size()).filter(slot -> component.canInsert(slot) || component.canExtract(slot)).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (component instanceof AdvancedInventoryComponent) return ((AdvancedInventoryComponent) component).canInsert(slot, dir);
        else return component.canInsert(slot);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (component instanceof AdvancedInventoryComponent) return ((AdvancedInventoryComponent) component).canExtract(slot,dir);
        else return component.canExtract(slot);
    }
}