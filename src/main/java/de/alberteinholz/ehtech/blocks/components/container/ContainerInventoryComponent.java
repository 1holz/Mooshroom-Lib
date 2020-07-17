package de.alberteinholz.ehtech.blocks.components.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent.Slot.Type;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import de.alberteinholz.ehtech.blocks.recipes.Input;
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
    public int maxTransfer = 1;

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

    public int pull(Inventory target, ActionType action, Direction dir) {
        int transfer = 0;
        if (!(data instanceof MachineDataProviderComponent && ((MachineDataProviderComponent) data).allowsConfig(ConfigType.ITEM, ConfigBehavior.SELF_INPUT, dir))) {
            TechMod.LOGGER.smallBug();
            return transfer;
        }
        InventoryComponent targetComponent = target instanceof InventoryWrapper && ((InventoryWrapper) target).component != null ? ((InventoryWrapper) target).component : null;
        ContainerInventoryComponent targetContainerComponent = target instanceof InventoryWrapper && ((InventoryWrapper) target).component instanceof ContainerInventoryComponent ? ((InventoryWrapper) target).getContainerInventoryComponent() : null;
        int[] intSlotIds = new int[target.size()];
        Arrays.setAll(intSlotIds, i -> i);
        Set<?> iterable = targetContainerComponent != null ? targetContainerComponent.getSlots(Type.OUTPUT).keySet() : new HashSet<>(Arrays.asList(intSlotIds));
        for (Object id : iterable) {
            if (targetContainerComponent != null ? targetContainerComponent.canExtract((String) id, dir) : targetComponent != null ? targetComponent.canExtract((int) id) : true) {
                ItemStack extracted = targetContainerComponent != null ? targetContainerComponent.removeStack((String) id, maxTransfer, ActionType.TEST) : targetComponent != null ? targetComponent.removeStack((int) id, ActionType.TEST) : target.getStack((int) id);
                if (extracted.getCount() > maxTransfer - transfer) extracted.setCount(maxTransfer - transfer);
                for (String ownId : getSlots(Type.INPUT).keySet()) {
                    int insertedCount = insertStack(ownId, extracted, action).getCount();
                    int extractedCount = targetContainerComponent != null ? targetContainerComponent.removeStack((String) id, insertedCount, action).getCount() : target.removeStack((int) id, insertedCount).getCount();
                    transfer += extractedCount;
                    if (insertedCount != extractedCount) TechMod.LOGGER.smallBug(new Exception("Item pulling wasn't performed correctly. This could lead to item deletion.")); 
                    if (transfer >= maxTransfer) break;
                }
            }
            if (transfer >= maxTransfer) break;
        }
        return transfer;
    }

    public int push(Inventory target, ActionType action, Direction dir) {
        int transfer = 0;
        if (!(data instanceof MachineDataProviderComponent && ((MachineDataProviderComponent) data).allowsConfig(ConfigType.ITEM, ConfigBehavior.SELF_OUTPUT, dir))) {
            TechMod.LOGGER.smallBug();
            return transfer;
        }
        InventoryComponent targetComponent = target instanceof InventoryWrapper && ((InventoryWrapper) target).component != null ? ((InventoryWrapper) target).component : null;
        ContainerInventoryComponent targetContainerComponent = target instanceof InventoryWrapper && ((InventoryWrapper) target).component instanceof ContainerInventoryComponent ? ((InventoryWrapper) target).getContainerInventoryComponent() : null;
        int[] intSlotIds = new int[target.size()];
        Arrays.setAll(intSlotIds, i -> i);
        Set<?> iterable = targetContainerComponent != null ? targetContainerComponent.getSlots(Type.INPUT).keySet() : new HashSet<>(Arrays.asList(intSlotIds));
        for (String id : getSlots(Type.OUTPUT).keySet()) {
            ItemStack extracted = removeStack(id, maxTransfer, ActionType.TEST);
            if (extracted.getCount() > maxTransfer - transfer) extracted.setCount(maxTransfer - transfer);
            for (Object foreigenId : iterable) {
                int insertedCount = targetContainerComponent != null ? targetContainerComponent.insertStack((String) foreigenId, extracted, action).getCount() : targetComponent != null ? targetComponent.insertStack((int) foreigenId, extracted, action).getCount() : 0;
                if (!(target instanceof InventoryWrapper)) {
                    ItemStack gotten = target.getStack((int) foreigenId);
                    insertedCount = gotten.getCount() + extracted.getCount() > gotten.getMaxCount() || gotten.getCount() + extracted.getCount() > target.getMaxCountPerStack() ? Math.min(gotten.getMaxCount(), target.getMaxCountPerStack()) - gotten.getCount() : extracted.getCount();
                    if (action.shouldPerform()) gotten.increment(insertedCount);
                }
                int extractedCount = removeStack(id, insertedCount, action).getCount();
                transfer += extractedCount;
                if (insertedCount != extractedCount) TechMod.LOGGER.smallBug(new Exception("Item pushing wasn't performed correctly. This could lead to item deletion.")); 
                if (transfer >= maxTransfer) break;
            }
            if (transfer >= maxTransfer) break;
        };
        return transfer;
    }

    //TODO: merge push and pull into this
    public static int move(Inventory from, Inventory to, ActionType action, Direction dir) {
        int transfer = 0;
        //add code here
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

    //FIXME: maybe deletes items
    public ItemStack insertStack(String id, ItemStack stack, ActionType action) {
		ItemStack target = getStack(id);
		if (!target.isEmpty() && !target.isItemEqualIgnoreDamage(stack)) return stack;
		int count = target.getCount();
		int maxSize = Math.min(target.getItem().getMaxCount(), getMaxStackSize(id));
		if (count == maxSize) return stack;
		int sizeLeft = maxSize - count;
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
		if (!action.shouldPerform()) stack = stack.copy();
		else onChanged();
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
    
    public boolean containsInput(Input.ItemIngredient ingredient) {
        int amount = 0;
        for (Slot slot : stacks.values()) {
            if (ingredient.ingredient != null && slot.type == Slot.Type.INPUT && ingredient.ingredient.contains(slot.stack.getItem())) {
                if (ingredient.tag != null) if (NbtHelper.matches(ingredient.tag, slot.stack.getTag(), true)) amount += slot.stack.getCount();
                else amount += slot.stack.getCount();
            }
        }
        if (amount >= ingredient.amount) return true;
        else return false;
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
        for (Iterator<Map.Entry<String, Slot>> iterator = stacks.entrySet().iterator(); iterator.hasNext();) {
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