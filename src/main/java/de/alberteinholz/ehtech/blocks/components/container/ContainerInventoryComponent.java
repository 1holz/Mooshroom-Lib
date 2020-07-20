package de.alberteinholz.ehtech.blocks.components.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent.Slot.Type;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import de.alberteinholz.ehtech.blocks.recipes.Input.ItemIngredient;
import de.alberteinholz.ehtech.util.Helper;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.item.InventoryComponent;
import io.github.cottonmc.component.serializer.StackSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class ContainerInventoryComponent implements InventoryComponent {
    protected final InventoryWrapper inventoryWrapper = new InventoryWrapper(this);
    public HashMap<String, Slot> stacks = new LinkedHashMap<String, Slot>();
    protected final List<Runnable> listeners = new ArrayList<>();
    protected ContainerDataProviderComponent data;

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    @Override
    public Inventory asInventory() {
        return inventoryWrapper;
    }

    @Override
    public SidedInventory asLocalInventory(WorldAccess world, BlockPos pos) {
        return inventoryWrapper;
    }

	public int size() {
		return stacks.size();
    }

    //XXX: Remove on UC update
    @Deprecated
    @Override
	public int getSize() {
		return stacks.size();
    }

    public Map<String, Slot> getSlots(Type type) {
        Map<String, Slot> map = new HashMap<String, Slot>();
        stacks.forEach((id, slot) -> {
            if (slot.type == type) map.put(id, slot);
        });
        return map;
    }

    public Slot getSlot(String id) {
        assert checkSlot(id);
        return stacks.get(id);
    }

    public Type getType(String id) {
        return getSlot(id).type;
    }

    public ItemStack getStack(String id) {
        return getSlot(id).stack;
    }

	public void setStack(String id, ItemStack stack) {
		getSlot(id).stack = stack;
		onChanged();
    }

    public boolean isSlotAvailable(String slot, Direction side) {
        return checkSlot(slot);
    }
    
    public boolean checkSlot(String slot) {
        if (stacks.containsKey(slot)) return true;
        else return false;
    }

    public void setDataProvider(ContainerDataProviderComponent data) {
        this.data = data;
    }

    //for using InventoryComponents wrap them in an InventoryWrapper
    //check ((MachineDataProviderComponent) data).allowsConfig(ConfigType.ITEM, configBehavior, dir) first
    public static int move(Inventory from, Inventory to, int maxTransfer, Direction dir, ActionType action) {
        int transfer = 0;
        InventoryComponent fromComponent = from instanceof InventoryWrapper && ((InventoryWrapper) from).component != null ? ((InventoryWrapper) from).component : null;
        ContainerInventoryComponent fromContainerComponent = fromComponent instanceof ContainerInventoryComponent ? (ContainerInventoryComponent) fromComponent : null;
        InventoryComponent toComponent = to instanceof InventoryWrapper && ((InventoryWrapper) to).component != null ? ((InventoryWrapper) to).component : null;
        ContainerInventoryComponent toContainerComponent = toComponent instanceof ContainerInventoryComponent ? (ContainerInventoryComponent) toComponent : null;
        for (Object idFrom : fromContainerComponent != null ? fromContainerComponent.getSlots(Type.OUTPUT).keySet().toArray() : Helper.countingArray(from.size())) {
            if (fromContainerComponent != null ? fromContainerComponent.canExtract((String) idFrom, dir) : fromComponent != null ? fromComponent.canExtract((int) idFrom) : true) {
                ItemStack extractionTest = fromContainerComponent != null ? fromContainerComponent.removeStack((String) idFrom, maxTransfer, ActionType.TEST) : fromComponent != null ? fromComponent.removeStack((int) idFrom, ActionType.TEST) : from.getStack((int) idFrom).copy();
                if (extractionTest.isEmpty()) continue;
                if (extractionTest.getCount() > maxTransfer - transfer) extractionTest.setCount(maxTransfer - transfer);
                for (Object idTo : toContainerComponent != null ? toContainerComponent.getSlots(Type.INPUT).keySet().toArray() : Helper.countingArray(to.size())) {
                    int insertionCount = extractionTest.getCount() - (toContainerComponent != null ? toContainerComponent.insertStack((String) idTo, extractionTest, action).getCount() : toComponent != null ? toComponent.insertStack((int) idTo, extractionTest, action).getCount() : 0);
                    if (!(to instanceof InventoryWrapper)) {
                        ItemStack gotten = to.getStack((int) idTo);
                        insertionCount = gotten.getCount() + extractionTest.getCount() > gotten.getMaxCount() || gotten.getCount() + extractionTest.getCount() > to.getMaxCountPerStack() ? Math.min(gotten.getMaxCount(), to.getMaxCountPerStack()) - gotten.getCount() : extractionTest.getCount();
                        if (action.shouldPerform()) gotten.increment(insertionCount);
                    }
                    if (insertionCount <= 0) continue;
                    int extractionCount = fromContainerComponent != null ? fromContainerComponent.removeStack((String) idFrom, insertionCount, action).getCount() : from.removeStack((int) idFrom, insertionCount).getCount();
                    transfer += extractionCount;
                    if (insertionCount != extractionCount) TechMod.LOGGER.smallBug(new IllegalStateException("Item moving wasn't performed correctly. This could lead to item deletion.")); 
                    if (transfer >= maxTransfer) break;
                }
            }
            if (transfer >= maxTransfer) break;
        }
        return transfer;
    }

    public boolean canInsert(String slot, Direction dir) {
        if (!getType(slot).insert) return false;
        else if (data instanceof MachineDataProviderComponent) return ((MachineDataProviderComponent) data).allowsConfig(ConfigType.ITEM, ConfigBehavior.FOREIGN_INPUT, dir);
        else return true;
    }

    public boolean canExtract(String slot, Direction dir) {
        if (!getType(slot).extract) return false;
        else if (data instanceof MachineDataProviderComponent) return ((MachineDataProviderComponent) data).allowsConfig(ConfigType.ITEM, ConfigBehavior.FOREIGN_OUTPUT, dir);
        else return true;
    }

    public ItemStack insertStack(String id, ItemStack stack, ActionType action) {
		ItemStack target = getStack(id);
		int maxSize = Math.min(target.getMaxCount(), getMaxStackSize(id));
		if (!target.isEmpty() && !target.isItemEqualIgnoreDamage(stack) || target.getCount() >= maxSize) return stack;
		int sizeLeft = maxSize - target.getCount();
		if (sizeLeft >= stack.getCount()) {
			if (action.shouldPerform()) {
				if (target.isEmpty()) setStack(id, stack);
				else target.increment(stack.getCount());
				onChanged();
			}
			return ItemStack.EMPTY;
		} else {
			if (action.shouldPerform()) {
				if (target.isEmpty()) {
					ItemStack newStack = stack.copy();
					newStack.setCount(maxSize);
					setStack(id, newStack);
				} else target.setCount(maxSize);
				onChanged();
			}
			stack.decrement(sizeLeft);
			return stack;
		}
    }
    
	@Override
	public ItemStack insertStack(ItemStack stack, ActionType action) {
        for (String id : stacks.keySet()) {
            stack = insertStack(id, stack, action);
			if (stack.isEmpty()) return stack;
        }
		return stack;
    }
    
    public ItemStack removeStack(String id, ActionType action) {
        if (action.shouldPerform()) {
            setStack(id, ItemStack.EMPTY);
            onChanged();
        }
        return getStack(id);
    }

    public ItemStack removeStack(String id, int amount, ActionType action) {
		ItemStack stack = getStack(id);
		if (action.shouldPerform()) onChanged();
		else stack = stack.copy();
        return stack.split(amount);
    }

    public int getMaxStackSize(String id) {
        return 64;
    }

    public boolean isAcceptableStack(String id, ItemStack stack) {
        return true;
    }

    @Override
    public int amountOf(Set<Item> items) {
        int amount = 0;
        for (Slot slot : stacks.values()) {
            if (items.contains(slot.stack.getItem())) amount += slot.stack.getCount();
        }
		return amount;
    }
    
    @Override
    public boolean contains(Set<Item> items) {
		for (Slot slot : stacks.values()) {
			if (items.contains(slot.stack.getItem()) && slot.stack.getCount() > 0) return true;
		}
		return false;
    }
    
    public boolean containsInput(ItemIngredient ingredient) {
        int amount = 0;
        for (Slot slot : stacks.values()) {
            if (ingredient.ingredient != null && slot.type == Type.INPUT && ingredient.ingredient.contains(slot.stack.getItem()) && (ingredient.tag == null || NbtHelper.matches(ingredient.tag, slot.stack.getTag(), true))) amount += slot.stack.getCount();
            if (amount >= ingredient.amount) return true;
        }
        return false;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        clear();
        for (String slotName : tag.getKeys()) {
            stacks.get(slotName).stack = StackSerializer.fromTag(tag.getCompound(slotName));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        for (Map.Entry<String, ContainerInventoryComponent.Slot> slot : stacks.entrySet()) {
            if (!slot.getValue().stack.isEmpty()) tag.put(slot.getKey(), StackSerializer.toTag(stacks.get(slot.getKey()).stack, new CompoundTag()));
        }
		return tag;
    }

    //should only be used if really needed
    public int getNumber(String slot) {
        stacks.containsKey(slot);
        int i = 0;
        for (Iterator<Entry<String, Slot>> iterator = stacks.entrySet().iterator(); iterator.hasNext();) {
            if (iterator.next().getKey() == slot) break;
            i++;
        }
        return i;
    }

    @Deprecated
    public String getId(int slot) {
        return (String) stacks.keySet().toArray()[slot];
    }

    @Deprecated
    private DefaultedList<ItemStack> asList() {
        Slot[] slots = new Slot[stacks.size()];
        stacks.values().toArray(slots);
        DefaultedList<ItemStack> list = DefaultedList.ofSize(slots.length, ItemStack.EMPTY);
        for (int i = 0; i < slots.length; i++) {
            list.set(i, slots[i].stack);
        }
        return list;
    }

    @Deprecated
	@Override
	public List<ItemStack> getStacks() {
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack stack : asList()) {
			list.add(stack.copy());
		}
		return list;
	}

    @Deprecated
	@Override
	public DefaultedList<ItemStack> getMutableStacks() {
		return asList();
	}

    @Deprecated
	@Override
	public ItemStack getStack(int slot) {
		return getStack(getId(slot));
	}

    @Deprecated
	@Override
	public boolean canInsert(int slot) {
        return getType(getId(slot)).insert;
	}

    @Deprecated
	@Override
	public boolean canExtract(int slot) {
		return getType(getId(slot)).extract;
	}

    //XXX: Remove on UC update
    @Deprecated
	@Override
	public ItemStack takeStack(int slot, int amount, ActionType action) {
		return removeStack(getId(slot), amount, action);
	}

    @Deprecated
	@Override
	public ItemStack removeStack(int slot, ActionType action) {
        return removeStack(getId(slot), action);
	}

    @Deprecated
	@Override
	public void setStack(int slot, ItemStack stack) {
		setStack(getId(slot), stack);
	}

    @Deprecated
	@Override
	public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
        return insertStack(getId(slot), stack, action);
	}

    @Deprecated
    @Override
    public int getMaxStackSize(int slot) {
        return getMaxStackSize(getId(slot));
    }

    @Deprecated
    @Override
    public boolean isAcceptableStack(int slot, ItemStack stack) {
        return isAcceptableStack(getId(slot), stack);
    }

    public static class Slot {
        public Type type;
        public ItemStack stack = ItemStack.EMPTY;

        public Slot(Type type) {
            this.type = type;
        }

        public enum Type {
            INPUT (true, false),
            OUTPUT (false, true),
            STORAGE (true, true),
            OTHER (false, false);

            public boolean insert;
            public boolean extract;

            private Type(boolean insert, boolean extract) {
                this.insert = insert;
                this.extract = extract;
            }
        }
    }
}