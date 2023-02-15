package de.einholz.ehmooshroom.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegEntry {
    public final Block BLOCK;
    public final BlockEntityType<?> BLOCK_ENTITY_TYPE;
    public final Item ITEM;
    public final ScreenHandlerType<?> SCREEN;

    public RegEntry(final Identifier id, final Block BLOCK, final BlockEntityType<?> BLOCK_ENTITY_TYPE, final Item ITEM, final ScreenHandlerType<?> SCREEN) {
        this.BLOCK = BLOCK == null ? null : Registry.register(Registry.BLOCK, id, BLOCK);
        this.BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE == null ? null : Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BLOCK_ENTITY_TYPE);
        this.ITEM = ITEM != null ? null : Registry.register(Registry.ITEM, id, ITEM);
        if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType())) {
            this.SCREEN = SCREEN != null ? null : Registry.register(Registry.SCREEN_HANDLER, id, SCREEN);
        } else this.SCREEN = null;
    }
}
