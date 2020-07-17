package de.alberteinholz.ehtech.blocks.components.container;

import java.util.ArrayList;
import java.util.List;

import de.alberteinholz.ehtech.TechMod;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

//Own InventoryWrapper because CU's doesn't work for me
public class InventoryWrapper implements SidedInventory {
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

    protected String getId(int slot) {
        if (!(component instanceof ContainerInventoryComponent)) {
            TechMod.LOGGER.smallBug(new Exception("Tried to get String-id from non ContainerInventoryComponent"));
            return "";
        }
        return (String) ((ContainerInventoryComponent) component).stacks.keySet().toArray()[slot];
    }

    @Override
    public int size() {
        return component.getSize();
    }

    @Override
    public boolean isEmpty() {
		for (int i = 0; i < size(); i++) {
			if (!getStack(i).isEmpty()) return false;
		}
		return true;
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

    @Deprecated
    @Override
    public int[] getAvailableSlots(Direction side) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < component.getStacks().size(); i++) {
            if (!(component instanceof ContainerInventoryComponent)) list.add(i);
            else if (((ContainerInventoryComponent) component).isSlotAvailable(getId(i), side)) list.add(i);
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (component instanceof ContainerInventoryComponent) return ((ContainerInventoryComponent) component).canInsert(getId(slot), dir);
        else return component.canInsert(slot);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (component instanceof ContainerInventoryComponent) return ((ContainerInventoryComponent) component).canExtract(getId(slot),dir);
        else return component.canExtract(slot);
    }
}