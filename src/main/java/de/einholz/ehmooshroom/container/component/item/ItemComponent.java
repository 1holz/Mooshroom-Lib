package de.einholz.ehmooshroom.container.component.item;

import java.util.Random;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.item.ItemComponent.ItemSpecification;
import de.einholz.ehmooshroom.container.component.util.TransportingComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public interface ItemComponent extends TransportingComponent<ItemComponent, ItemSpecification> {
    public static final Identifier ITEM_INTERNAL_ID = MooshroomLib.HELPER.makeId("item_internal");
    public static final Identifier ITEM_INPUT_ID = MooshroomLib.HELPER.makeId("item_input");
    public static final Identifier ITEM_OUTPUT_ID = MooshroomLib.HELPER.makeId("item_output");
    public static final Identifier ITEM_STORAGE_ID = MooshroomLib.HELPER.makeId("item_storage");
    public static final ComponentKey<ItemComponent> ITEM_INTERNAL = ComponentRegistry.getOrCreate(ITEM_INTERNAL_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_INPUT = ComponentRegistry.getOrCreate(ITEM_INPUT_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_OUTPUT = ComponentRegistry.getOrCreate(ITEM_OUTPUT_ID, ItemComponent.class);
    public static final ComponentKey<ItemComponent> ITEM_STORAGE = ComponentRegistry.getOrCreate(ITEM_STORAGE_ID, ItemComponent.class);
    
    public static class ItemSpecification {
        private Tag<Item> tag;
        private NbtCompound nbt;

        public ItemSpecification(Tag<Item> tag, NbtCompound nbt) {
            this.tag = tag;
            this.nbt = nbt;
        }

        public boolean matches(ItemStack stack) {
            return (tag == null || tag.contains(stack.getItem())) && NbtHelper.matches(nbt, stack.getTag(), true);
        }

        public int size() {
            return tag.values().size();
        }

        public Item getSingle() {
            return tag.getRandom(new Random());
        }

        public int getMinMaxStack() {
            int i = Integer.MAX_VALUE;
            for (Item item : tag.values()) if (item.getMaxCount() < i) i = item.getMaxCount();
            return i;
        }
    }
}
