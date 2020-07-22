package de.alberteinholz.ehmooshroom.impl.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
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
    public static final HashMap<Identifier, Entry> BLOCKS = new HashMap<>();

    @Deprecated //move to api
    public static <S extends Screen & ScreenHandlerProvider<? extends ScreenHandler>> void addFull(Identifier id, Block block, Settings itemSettings, Supplier<BlockEntity> blockEntitySupplier, ExtendedClientHandlerFactory<ScreenHandler> clientHandlerFactory, Factory<ScreenHandler, ? extends  S> screenFactory, RecipeType<Recipe<?>> recipeType, RecipeSerializer<Recipe<?>> recipeSerializer) {
        BLOCKS.put(id, new Entry(id).withBlock(block).withBlockItem(itemSettings).withBlockEntity(blockEntitySupplier).withGui(clientHandlerFactory).withScreen(screenFactory).withRecipe(recipeType).withRecipeSerializer(recipeSerializer));
    }

    public static void registerMain() {
        for (Entry entry : BLOCKS.values()) entry.registerMain();
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        for (Entry entry : BLOCKS.values()) entry.registerClient();
    }

    public static class Entry {
        public Identifier id;
        //supplied:
        public Block block;
        public Settings itemSettings;
        public BlockEntityType<? extends BlockEntity> blockEntityType;
        public ExtendedClientHandlerFactory<ScreenHandler> clientHandlerFactory;
        public Factory<ScreenHandler, ? extends Screen> screenFactory; //if you know a way to make ? extends Screen & ScreenHandlerProvider<? extends ScreenHandler> tell me
        public RecipeType<? extends Recipe<?>> recipeType;
        public RecipeSerializer<? extends Recipe<?>> recipeSerializer;
        //created:
        public ScreenHandlerType<ScreenHandler> screenHandlerType;

        public Entry(Identifier id) throws NullPointerException {
            if (id == null) MooshroomLib.LOGGER.smallBug(new NullPointerException("Skiping registering of block with null id."));
            else this.id = id;
        }

        public Entry withBlock(Block block) {
            this.block = block;
            return this;
        }

        public Entry withBlockItem(Settings itemSettings) {
            if (block == null) MooshroomLib.LOGGER.smallBug(new NullPointerException("You should register Block before BlockItem for " + id.toString()));
            this.itemSettings = itemSettings;
            return this;
        }

        public Entry withBlockEntity(Supplier<? extends BlockEntity> blockEntitySupplier) {
            if (block == null) MooshroomLib.LOGGER.smallBug(new NullPointerException("You must register Block before BlockEntity for " + id.toString()));
            else blockEntityType = Builder.create(blockEntitySupplier, block).build(null);
            return this;
        }

        public Entry withGui(ExtendedClientHandlerFactory<ScreenHandler> clientHandlerFactory) {
            this.clientHandlerFactory = clientHandlerFactory;
            return this;
        }

        public <S extends Screen & ScreenHandlerProvider<? extends ScreenHandler>> Entry withScreen(Factory<ScreenHandler, ? extends S> screenFactory) {
            this.screenFactory = screenFactory;
            return this;
        }

        public Entry withRecipe(RecipeType<? extends Recipe<?>> recipeType) {
            this.recipeType = recipeType;
            return this;
        }

        public Entry withRecipeSerializer(RecipeSerializer<? extends Recipe<?>> recipeSerializer) {
            this.recipeSerializer = recipeSerializer;
            return this;
        }

        public void registerMain() {
            if (id != null) {
                if (block != null) Registry.register(Registry.BLOCK, id, block);
                if (itemSettings != null && block != null) Registry.register(Registry.ITEM, id, new BlockItem(block, itemSettings));
                if (blockEntityType != null) Registry.register(Registry.BLOCK_ENTITY_TYPE, id, blockEntityType);
                if (clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, clientHandlerFactory);
                if (recipeType != null) Registry.register(Registry.RECIPE_TYPE, id, recipeType);
                if (recipeSerializer != null) Registry.register(Registry.RECIPE_SERIALIZER, id, recipeSerializer);
            }
        }

        @Environment(EnvType.CLIENT)
        public void registerClient() {
            if (id != null) {
                if (screenHandlerType != null && screenFactory != null) ScreenRegistry.register(screenHandlerType, screenFactory);
            }
        }
    }
}