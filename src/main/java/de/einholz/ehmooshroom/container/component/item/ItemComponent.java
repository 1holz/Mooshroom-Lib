package de.einholz.ehmooshroom.container.component.item;

import java.util.List;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent.SideConfigType;
import de.einholz.ehmooshroom.container.component.util.TransportingComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

//slot numbers for machine internals:
//0: power input
//1: power output
//2: upgrades
//3: network
public interface ItemComponent extends TransportingComponent<ItemComponent> {
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
    
    List<ItemStack> getStacks();
    int getMaxStackSize();

    default ItemStack getStack(int slot) {
        return getStacks().get(slot);
    }

    default void setStack(int slot, ItemStack stack) {
        getStacks().set(slot, stack);
    }

    default int size() {
        return getStacks().size();
    }

    @Override
    default Number transport(ItemComponent from, ItemComponent to) {
        int oriTrans = Math.min(from.getMaxTransfer().intValue(), to.getMaxTransfer().intValue());
        int transfer = oriTrans;
        for (ItemStack fStack : from.getStacks()) {
            if (fStack.isEmpty()) continue;
            for (int j = 0; j < to.size(); j++) {
                ItemStack tStack = to.getStack(j);
                if (!tStack.isEmpty() || !ItemStack.areEqual(fStack, tStack)) continue;
                int sTransfer = Math.min(tStack.getMaxCount() - tStack.getCount(), Math.min(fStack.getCount(), transfer));
                to.setStack(j, new ItemStack(fStack.getItem(), to.getStack(j).getCount() + sTransfer));;
                fStack.decrement(sTransfer);
                transfer -= sTransfer;
                if (transfer <= 0) return oriTrans;
            }
        }
        return oriTrans - transfer;
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
        for (ItemStack stack : getStacks()) list.add(stack.writeNbt(new NbtCompound()));
        nbt.put("Inventory", list);
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("Inventory", NbtType.COMPOUND);
        for (int i = 0; i < list.size(); i++) setStack(i, ItemStack.fromNbt(list.getCompound(i)));
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
