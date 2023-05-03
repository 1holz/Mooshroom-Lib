package de.einholz.ehmooshroom.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegEntry<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    public final Block BLOCK;
    public final BlockEntityType<B> BLOCK_ENTITY_TYPE;
    public final Item ITEM;
    public final ScreenHandlerType<G> GUI;
    public final Factory<G, S> SCREEN;
    public final RecipeType<R> RECIPE_TYPE;
    public final RecipeSerializer<R> RECIPE_SERIALIZER;

    public RegEntry(final Identifier id, final Block BLOCK, final BlockEntityType<B> BLOCK_ENTITY_TYPE, final Item ITEM, final ScreenHandlerType<G> GUI, final Factory<G, S> SCREEN, final RecipeType<R> RECIPE_TYPE, final RecipeSerializer<R> RECIPE_SERIALIZER) {
        this.BLOCK = BLOCK == null ? null : Registry.register(Registry.BLOCK, id, BLOCK);
        this.BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE == null ? null : Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BLOCK_ENTITY_TYPE);
        this.ITEM = ITEM == null ? null : Registry.register(Registry.ITEM, id, ITEM);
        this.GUI = GUI == null ? null : Registry.register(Registry.SCREEN_HANDLER, id, GUI);
        this.SCREEN = SCREEN == null || EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType()) ? null : SCREEN;
        this.RECIPE_TYPE = RECIPE_TYPE == null ? null : Registry.register(Registry.RECIPE_TYPE, id, RECIPE_TYPE);
        this.RECIPE_SERIALIZER = RECIPE_SERIALIZER == null ? null : Registry.register(Registry.RECIPE_SERIALIZER, id, RECIPE_SERIALIZER);
    }
}
