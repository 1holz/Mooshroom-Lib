package de.einholz.ehmooshroom.registry.deprecated;

import de.einholz.ehmooshroom.item.Tool;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Deprecated
public interface ItemReg extends Reg {
    public static Item registerRaw(Identifier id, Item item) {
        return Registry.register(Registry.ITEM, id, item);
    }

    public static Item registerItem(Identifier id, Settings settings) {
        return Registry.register(Registry.ITEM, id, new Item(settings));
    }

    public static BlockItem registerBlockItem(Identifier id, Block block, Settings settings) {
        return Registry.register(Registry.ITEM, id, new BlockItem(block, settings));
    }

    public static Tool registerTool(Identifier id, Settings settings) {
        return Registry.register(Registry.ITEM, id, new Tool(settings));
    }

    public static <T extends ItemConvertible> T makeFuel(T item, int cookTime) {
        FuelRegistry.INSTANCE.add(item, cookTime);
        return item;
    }
}
