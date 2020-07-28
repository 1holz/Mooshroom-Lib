package de.alberteinholz.ehmooshroom.registry;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistryEntry {
    private Identifier id;
    //supplied:
    public Item item;
    public ItemGroup itemGroup;

    //use null id only for templates
    protected ItemRegistryEntry(Identifier id) {
        this.id = id;
    }

    public ItemRegistryEntry withItemBuild(ItemFactory<? extends Item> factory, Settings settings) {
        return withItem(factory.create(settings));
    }

    public ItemRegistryEntry withItem(Item item) {
        this.item = item;
        if (id != null && item != null) Registry.register(Registry.ITEM, id, item);
        return this;
    }

    public ItemRegistryEntry withItemGroupBuild() {
        if (item == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add an Item before ItemGroupBuild for " + id.toString()));
            return this;
        }
        return withItemGroup(FabricItemGroupBuilder.create(id).icon(() -> new ItemStack(item)).build());
    }

    public ItemRegistryEntry withItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    @FunctionalInterface
    private static interface ItemFactory<I extends Item> {
        I create(Settings itemSettings);
    }
}