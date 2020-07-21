package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {
    public static final HashMap<Identifier, Entry<?, ?, ?, ?>> BLOCKS = new HashMap<>();

    public static <BE extends BlockEntity, SH extends ScreenHandler, R extends Recipe<?>, S extends Screen & ScreenHandlerProvider<SH>> void addBlock(Identifier id, Block block, Settings itemSettings, Supplier<BE> blockEntitySupplier, ExtendedClientHandlerFactory<SH> clientHandlerFactory, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer, Factory<SH, S> screenFactory) {
        BLOCKS.put(id, new Entry<>(id, block, itemSettings, blockEntitySupplier, clientHandlerFactory, recipeType, recipeSerializer, screenFactory));
    }

    public static void registerMain() {
        BLOCKS.forEach((id, entry) -> {
            entry.registerMain();
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        BLOCKS.forEach((id, entry) -> {
            entry.registerClient();
        });
    }

    public static class Entry<BE extends BlockEntity, SH extends ScreenHandler, R extends Recipe<?>, S extends Screen & ScreenHandlerProvider<SH>> {
        public final Identifier id;
        //supplied:
        public final Block block;
        public final Settings itemSettings;
        public final Supplier<BE> blockEntitySupplier;
        public final ExtendedClientHandlerFactory<SH> clientHandlerFactory;
        public final RecipeType<R> recipeType;
        public final RecipeSerializer<R> recipeSerializer;
        public final Factory<SH, S> screenFactory;
        //created:
        public BlockEntityType<BE> blockEntityType;
        public ScreenHandlerType<SH> screenHandlerType;

        public Entry(Identifier id, Block block, Settings itemSettings, Supplier<BE> blockEntitySupplier, ExtendedClientHandlerFactory<SH> clientHandlerFactory, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer, Factory<SH, S> screenFactory) {
            this.id = id;
            this.block = block;
            this.itemSettings = itemSettings;
            this.blockEntitySupplier = blockEntitySupplier;
            this.clientHandlerFactory = clientHandlerFactory;
            this.recipeType = recipeType;
            this.recipeSerializer = recipeSerializer;
            this.screenFactory = screenFactory;
        }

        public void registerMain() {
            if (block != null) Registry.register(Registry.BLOCK, id, block);
            if (itemSettings != null && block != null) Registry.register(Registry.ITEM, id, new BlockItem(block, itemSettings));
            if (blockEntitySupplier != null && block != null) blockEntityType = Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.create(blockEntitySupplier, block).build(null));
            if (clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, clientHandlerFactory);
            if (recipeType != null) Registry.register(Registry.RECIPE_TYPE, id, recipeType);
            if (recipeSerializer != null) Registry.register(Registry.RECIPE_SERIALIZER, id, recipeSerializer);
        }

        @Environment(EnvType.CLIENT)
        public void registerClient() {
            if (screenHandlerType != null && screenFactory != null) ScreenRegistry.register(screenHandlerType, screenFactory);
        }
    }
}