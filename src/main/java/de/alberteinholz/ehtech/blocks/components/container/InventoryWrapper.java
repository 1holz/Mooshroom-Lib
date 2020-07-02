package de.alberteinholz.ehtech.blocks.components.container;

import java.util.ArrayList;
import java.util.List;

import io.github.cottonmc.component.api.ActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

//Own InventoryWrapper because CU's doesn't work for me
public class InventoryWrapper implements SidedInventory {
    public final ContainerInventoryComponent component;
    public final BlockPos pos;

    public InventoryWrapper(ContainerInventoryComponent component) {
        this(component, null);
    }

    public InventoryWrapper(BlockPos pos) {
        this(null, pos);
    }

    public InventoryWrapper(ContainerInventoryComponent component, BlockPos pos) {
        this.component = component;
        this.pos = pos;
    }

    @Override
    public int size() {
        return component.size();
    }

    @Override
    public boolean isEmpty() {
		for (int i = 0; i < size(); i++) {
			if (!getStack(i).isEmpty()) {
				return false;
			}
		}
		return true;
    }

    protected String getId(int slot) {
        return (String) component.stacks.keySet().toArray()[slot];
    }

    @Override
    public ItemStack getStack(int slot) {
        return component.getStack(getId(slot));
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return component.removeStack(getId(slot), amount, ActionType.PERFORM);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return component.removeStack(getId(slot), ActionType.PERFORM);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        component.setStack(getId(slot), stack);
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
            if (component.isSlotAvailable(getId(i), side)) {
                list.add(i);
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return component.canInsert(getId(slot), dir);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return component.canExtract(getId(slot),dir);
    }
}