package de.einholz.ehmooshroom.container.component.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent.SideConfigType;
import de.einholz.ehmooshroom.container.component.util.TransportingComponent;
import de.einholz.ehmooshroom.util.Helper;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

//slot numbers for machine internals:
//0: power input
//1: power output
//2: upgrades
//3: network
public interface ItemComponent extends TransportingComponent<ItemComponent> {
    public static final Identifier ITEM_ID = MooshroomLib.HELPER.makeId("item");
    public static final ComponentKey<ItemComponent> ITEM = ComponentRegistry.getOrCreate(ITEM_ID, ItemComponent.class);
    //TODO: use cache!!!
    //null for ignoring
    public static final BlockApiLookup<ItemComponent, SideConfigType> ITEM_LOOKUP = BlockApiLookup.get(ITEM_ID, ItemComponent.class, SideConfigType.class);
    /*
    public static final Identifier ITEM_INTERNAL_ID = MooshroomLib.HELPER.makeId("item_internal");
    public static final Identifier ITEM_INPUT_ID = MooshroomLib.HELPER.makeId("item_input");
    public static final Identifier ITEM_OUTPUT_ID = MooshroomLib.HELPER.makeId("item_output");
    public static final Identifier ITEM_STORAGE_ID = MooshroomLib.HELPER.makeId("item_storage");
    public static final ComponentKey<ItemComponent> ITEM_INTERNAL = ComponentRegistry.getOrCreate(ITEM_INTERNAL_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_INPUT = ComponentRegistry.getOrCreate(ITEM_INPUT_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_OUTPUT = ComponentRegistry.getOrCreate(ITEM_OUTPUT_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_STORAGE = ComponentRegistry.getOrCreate(ITEM_STORAGE_ID, ItemComponent.class);
    //TODO: use cache!!!
    //null for ignoring
    public static final BlockApiLookup<ItemComponent, SideConfigType> ITEM_INTERNAL_LOOKUP = BlockApiLookup.get(ITEM_INTERNAL_ID, ItemComponent.class, SideConfigType.class);
    public static final BlockApiLookup<ItemComponent, SideConfigType> ITEM_INPUT_LOOKUP = BlockApiLookup.get(ITEM_INPUT_ID, ItemComponent.class, SideConfigType.class);
    public static final BlockApiLookup<ItemComponent, SideConfigType> ITEM_OUTPUT_LOOKUP = BlockApiLookup.get(ITEM_OUTPUT_ID, ItemComponent.class, SideConfigType.class);
    public static final BlockApiLookup<ItemComponent, SideConfigType> ITEM_STORAGE_LOOKUP = BlockApiLookup.get(ITEM_STORAGE_ID, ItemComponent.class, SideConfigType.class);
    */
    
    @Override
    default Identifier getId() {
        return ITEM_ID;
    }

    List<Slot> getSlots();

    default void addSlots(short t, String tag, Identifier... ids) {
        Slot[] a = new Slot[ids.length];
        for (int i = 0; i < a.length; i++) a[i] = new Slot().withType(t).addTag(tag).withId(ids[i]);
        addSlots(a);
    }

    default void addSlots(short t, String[] tags, Identifier... ids) {
        Slot[] a = new Slot[ids.length];
        for (int i = 0; i < a.length; i++) a[i] = new Slot().withType(t).addTags(tags).withId(ids[i]);
        addSlots(a);
    }

    default void addSlots(Slot pattern, int amount) {
        Slot[] a = new Slot[amount];
        Arrays.fill(a, pattern);
        addSlots(a);
    }

    default void addSlots(Slot... slots) {
        Collections.addAll(getSlots(), slots);
    }

    /**
     * <p> Types:
     * <p> 0 = Internal
     * <p> 1 = Input
     * <p> 2 = Output
     * <p> 3 = Storage
     * <p> for filtering only:
     * <p> 4 = inputable
     * <p> 5 = outputable
     * <p> 6 = all
     */
    default List<Slot> getSlots(short type) {
        List<Slot> list = new ArrayList<>();
        if (type > -1 && type < 4) {
            return fromExactSlotType(getSlots(), type, list);
        } else if (type > 3 && type < 6) {
            if (type == 4) fromExactSlotType(getSlots(), (short) 1, list);
            else fromExactSlotType(getSlots(), (short) 2, list);
            return fromExactSlotType(getSlots(), (short) 3, list);
        }
        if (type != 6) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("Illegal slot type " + type + ". Defaulting to all."));
        return getSlots();
    }

    static List<Slot> fromExactSlotType(List<Slot> slots, short type, List<Slot> list) {
        for (Slot slot : slots) if (slot.getType() == type) list.add(slot);
        return list;
    }

    default Slot getSlot(SlotFilter filter) {
        return filter.getSlot(getSlots());
    }

    default List<ItemStack> getStacks(short type) {
        List<ItemStack> list = new ArrayList<>();
        for (Slot slot : getSlots(type)) list.add(slot.getStack());
        return list;
    }

    default ItemStack getStack(SlotFilter filter) {
        return getSlot(filter).getStack();
    }

    default int getMaxStackSize(SlotFilter filter) {
        return getSlot(filter).getMax();
    }

    default void setStack(SlotFilter filter, ItemStack stack) {
        getSlot(filter).setStack(stack);
    }

    default int size() {
        return getSlots().size();
    }

    @Override
    default Number transport(ItemComponent from, ItemComponent to) {
        int cap = Math.min(from.getMaxTransfer().intValue(), to.getMaxTransfer().intValue());
        int remaining = cap;
        for (Slot fSlot : from.getSlots((short) 5)) {
            ItemStack fStack = fSlot.getStack();
            if (fStack.isEmpty()) continue;
            for (int j = 0; j < to.size(); j++) {
                Slot tSlot = to.getSlot(new SlotFilter(j));
                ItemStack tStack = tSlot.getStack();
                if (!tStack.isEmpty() || !ItemStack.areEqual(fStack, tStack)) continue;
                int sTransfer = Helper.min(tStack.getMaxCount() - tStack.getCount(), fStack.getCount(), remaining, tSlot.getMax() - tStack.getCount());
                tSlot.setStack(new ItemStack(fStack.getItem(), tStack.getCount() + sTransfer));;
                fStack.decrement(sTransfer);
                remaining -= sTransfer;
                if (remaining <= 0) return cap;
            }
        }
        return cap - remaining;
    }

    /*
    default Number getContent(ItemSpecification type) {
        int content = 0;
        for (ItemStack stack : getStacks()) if (type.matches(stack)) content += stack.getCount();
        return content;
    }

    default Number getSpace(ItemSpecification type) {
        int space = 0;
        for (ItemStack stack : getStacks()) if (stack.isEmpty() || type.matches(stack)) space += Math.min(getMaxStackSize(), type.getMaxStackSize());
        return space;
    }

    @Override
    default Number change(Number amount, Action action) {
        int i = amount.intValue();
        for (ItemStack stack : getStacks()) {
            if (i == 0) break;
            int change = i > 0 ? Math.min(i, Math.min(stack.getMaxCount(), getMaxStackSize()) - stack.getCount()) : Math.max(i, stack.getCount() * -1);
            if (action.perfrom()) stack.increment(change);
            i -= change;
        }
        return amount.intValue() - i;
    }
    */

    @Override
    default void writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (Slot s : getSlots()) {
            NbtCompound nc = new NbtCompound();
            if (s.getType() != 3) nc.putShort("Type", s.getType());
            if (s.getStack() != ItemStack.EMPTY) nc.put("Item", s.getStack().writeNbt(new NbtCompound()));
            if (s.getMax() != 64) nc.putInt("Max", s.getMax());
            if (!s.getTags().isEmpty()) {
                NbtList tags = new NbtList();
                for (String tag : s.getTags()) tags.add(NbtString.of(tag));
                nc.put("Tags", tags);
            }
            if (s.getId() != null) nc.putString("Id", s.getId().toString());
            //empty ones are also added to preserve the order
            list.add(nc);
        }
        if (!list.isEmpty()) nbt.put("Inventory", list);
        if (getMaxTransfer().intValue() != 4) nbt.putInt("MaxTransfer", getMaxTransfer().intValue());
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        if (nbt.contains("Inventory", NbtType.LIST)) {
            NbtList list = nbt.getList("Inventory", NbtType.COMPOUND);
            Slot[] slots = new Slot[list.size()];
            for (int i = 0; i < slots.length; i++) {
                slots[i] = new Slot();
                NbtCompound nc = list.getCompound(i);
                if (nc.contains("Type", NbtType.NUMBER)) slots[i].withType(nc.getShort("Type"));
                if (nc.contains("Item", NbtType.COMPOUND)) slots[i].setStack(ItemStack.fromNbt(nc.getCompound("Item")));
                if (nc.contains("Max", NbtType.NUMBER)) slots[i].withMax(nc.getInt("Max"));
                if (nc.contains("Tags", NbtType.LIST)) slots[i].addTags((String[]) nc.getList("Tags", NbtType.STRING).toArray());
                if (nc.contains("Id", NbtType.STRING)) slots[i].withId(new Identifier(nc.getString("Id")));
            }
            addSlots(slots);
        }
        if (nbt.contains("MaxTransfer", NbtType.INT)) setMaxTransfer(nbt.getInt("MaxTransfer"));
        //NbtList list = nbt.getList("Inventory", NbtType.COMPOUND);
        //for (int i = 0; i < list.size(); i++) setStack(i, ItemStack.fromNbt(list.getCompound(i)));
    }

    /**
     * <p> Types:
     * <p> 0 = Internal
     * <p> 1 = Input
     * <p> 2 = Output
     * <p> 3 = Storage
     */
    public static class Slot {
        private short type = 3;
        private ItemStack stack = ItemStack.EMPTY;
        private int max = 64;
        private List<String> tags = new ArrayList<>();
        private Identifier id;

        public Slot withType(short t) {
            type = t == 0 || t == 1 || t == 2 ? t : 3;
            if (type == 3 && t != 3) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("Illegal slot type " + t + ". Defaulting to storage."));
            return this;
        }

        public Slot withMax(int max) {
            this.max = max;
            return this;
        }

        public Slot addTags(String... tags) {
            Collections.addAll(this.tags, tags);
            return this;
        }

        public Slot addTag(String tag) {
            tags.add(tag);
            return this;
        }

        public Slot withId(Identifier id) {
            this.id = id;
            return this;
        }

        public short getType() {
            return type;
        }

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }

        public int getMax() {
            return max;
        }

        public List<String> getTags() {
            return tags;
        }

        public void removeTag(String tag) {
            tags.remove(tag);
        }

        public Identifier getId() {
            return id;
        }
    }

    public static class SlotFilter {
        private int i;
        private Identifier id;

        public SlotFilter(int i) {
            this.i = i;
        }

        public SlotFilter(Identifier id) {
            this.id = id;
        }

        public Slot getSlot(List<Slot> slots) {
            if (id == null) return slots.get(i);
            else for (Slot slot : slots) if (id.equals(slot.getId())) return slot;
            return null;
        }
    }

    //TODO delete
    /*
    public static class ItemSpecification {
        private Item item;
        private NbtCompound nbt;

        public ItemSpecification(Item item, NbtCompound nbt) {
            this.item = item;
            this.nbt = nbt;
        }

        public boolean matches(ItemStack stack) {
            return (item == null || item.equals(stack.getItem())) && NbtHelper.matches(nbt, stack.getTag(), true);
        }

        @Deprecated
        public Item getItem() {
            return item;
        }

        public int getMaxStackSize() {
            return item.getMaxCount();
        }
    }
    */
}
