package de.einholz.ehmooshroom.container.component.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.einholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class CombinedInventoryComponent extends CombinedComponent<InventoryComponent> implements InventoryComponent {
    protected final List<Runnable> listeners = new ArrayList<>();
    protected int tempSlot = 0;

    @Override
    public CombinedInventoryComponent of(Map<Identifier, InventoryComponent> childComps) {
        Iterator<InventoryComponent> iter = childComps.values().iterator();
        while (iter.hasNext()) if (!(iter.next() instanceof InventoryComponent)) iter.remove();
        return (CombinedInventoryComponent) super.of(childComps);
    }

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    @Override
    public boolean canExtract(int slot) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? false : comp.canExtract(tempSlot);
    }

    @Override
    public boolean canInsert(int slot) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? false : comp.canInsert(tempSlot);
    }

    @Override
    public DefaultedList<ItemStack> getMutableStacks() {
		DefaultedList<ItemStack> ret = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        int i = 0;
        for (InventoryComponent comp : getComps().values()) for (ItemStack stack : comp.getMutableStacks()) {
            ret.set(i, stack);
            i++;
        }
        return ret;
    }

    @Override
    public int size() {
        int slots = 0;
        for (InventoryComponent comp : getComps().values()) slots += comp.size();
        return slots;
    }

    @Override
    public ItemStack getStack(int slot) {
        InventoryComponent comp = getCompFromSlot(slot);
        return comp == null ? ItemStack.EMPTY : comp.getStack(tempSlot);
    }

    @Override
    public List<ItemStack> getStacks() {
		List<ItemStack> ret = new ArrayList<>();
        for (InventoryComponent comp : getComps().values()) ret.addAll(comp.getStacks());
        return ret;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, ActionType action) {
        for (InventoryComponent comp : getComps().values()) stack = comp.insertStack(stack, action);
        return stack;
    }

    @Override
    public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? stack : comp.insertStack(tempSlot, stack, action);
    }

    //XXX: why not also make search for a stack possible?
    @Override
    public ItemStack removeStack(int slot, ActionType action) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? ItemStack.EMPTY : comp.removeStack(tempSlot, action);
    }

    @Override
    public ItemStack removeStack(int slot, int amount, ActionType action) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? ItemStack.EMPTY : comp.removeStack(tempSlot, amount, action);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        InventoryComponent comp = getCompFromSlot(slot);
        if (comp != null) comp.setStack(tempSlot, stack);
    }

	@Override
	public void clear() {
		for (InventoryComponent comp : getComps().values()) comp.clear();
	}

	@Override
	public int getMaxStackSize(int slot) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? 0 : comp.getMaxStackSize(tempSlot);
	}

	@Override
	public boolean isAcceptableStack(int slot, ItemStack stack) {
        InventoryComponent comp = getCompFromSlot(slot);
        return (comp == null) ? false : comp.isAcceptableStack(tempSlot, stack);
	}

	@Override
	public int amountOf(Set<Item> items) {
		int amount = 0;
        for (InventoryComponent comp : getComps().values()) amount += comp.amountOf(items);
		return amount;
	}

	@Override
	public Inventory asInventory() {
		return new InventoryWrapperComp(this);
	}

	@Override
	public SidedInventory asLocalInventory(WorldAccess world, BlockPos pos) {
		return (SidedInventory) asInventory();
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
        return CombinedComponent.toTag(tag, "CombinedInventoryComponent", getComps());
	}
    
    @Override
	public void fromTag(CompoundTag tag) {
        CombinedComponent.fromTag(tag, "CombinedInventoryComponent", getComps());
	}

    protected InventoryComponent getCompFromSlot(int slot) {
        for (InventoryComponent comp : getComps().values()) {
            if (comp.size() <= slot) slot -= comp.size();
            else {
                tempSlot = slot;
                return comp;
            }
        }
        return null;
    }
}
