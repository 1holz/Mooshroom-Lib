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
    public int getInvSize() {
        return component.getSize();
    }

    @Override
    public boolean isInvEmpty() {
		for (int i = 0; i < getInvSize(); i++) {
			if (!getInvStack(i).isEmpty()) {
				return false;
			}
		}
		return true;
    }

    protected String getId(int slot) {
        return (String) component.stacks.keySet().toArray()[slot];
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return component.getItemStack(getId(slot));
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        return component.takeStack(getId(slot), amount, ActionType.PERFORM);
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        return component.removeStack(getId(slot), ActionType.PERFORM);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        component.setStack(getId(slot), stack);
    }

    @Override
    public void markDirty() {
        component.onChanged();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        component.clear();

    }

    @Deprecated
    @Override
    public int[] getInvAvailableSlots(Direction side) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < component.getStacks().size(); i++) {
            if (component.isSlotAvailable(getId(i), side)) {
                list.add(i);
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir) {
        return component.canInsertStack(getId(slot), dir);
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) {
        return component.canExtractStack(getId(slot),dir);
    }
}