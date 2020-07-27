package de.alberteinholz.ehmooshroom.registry;

import java.util.function.Supplier;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistryEntry {
    private Identifier id;
    //supplied:
    public Block block;
    public Item item;
    public BlockEntityType<? extends BlockEntity> blockEntityType;
    public ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    public Factory screenFactory;
    public RecipeType<? extends Recipe<?>> recipeType;
    public RecipeSerializer<? extends Recipe<?>> recipeSerializer;
    //created:
    public ScreenHandlerType<? extends ScreenHandler> screenHandlerType;

    protected BlockRegistryEntry(Identifier id) throws NullPointerException {
        if (id == null) {
            NullPointerException e = new NullPointerException("Skiping creation of BlockRegistryEntry with null id.");
            MooshroomLib.LOGGER.smallBug(e);
            throw e;
        }
        this.id = id;
    }

    public BlockRegistryEntry withBlock(Block block) {
        this.block = block;
        if (this.block != null) Registry.register(Registry.BLOCK, id, this.block);
        return this;
    }

    public BlockRegistryEntry withBlockItemBuild(Settings itemSettings) {
        if (block == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Block before BlockItemBuild for " + id.toString()));
            return this;
        }
        return withItem(new BlockItem(this.block, itemSettings));
    }

    public BlockRegistryEntry withItem(Item item) {
        this.item = item;
        if (this.item != null) Registry.register(Registry.ITEM, id, this.item);
        return this;
    }

    public BlockRegistryEntry withBlockEntity(Supplier<? extends BlockEntity> blockEntitySupplier) {
        if (block == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Block before BlockEntity for " + id.toString()));
            return this;
        }
        return withBlockEntityType(Builder.create(blockEntitySupplier, this.block).build(null));
    }

    public BlockRegistryEntry withBlockEntityType(BlockEntityType<? extends BlockEntity> blockEntityType) {
        this.blockEntityType = blockEntityType;
        if (this.blockEntityType != null) Registry.register(Registry.BLOCK_ENTITY_TYPE, id, this.blockEntityType);
        return this;
    }

    public BlockRegistryEntry withGui(ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
        if (this.clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, this.clientHandlerFactory);
        return this;
    }

    public BlockRegistryEntry withScreen(Factory screenFactory) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return this;
        this.screenFactory = screenFactory;
        if (screenHandlerType == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Gui before Screen for " + id.toString()));
            return this;
        }
        if (this.screenHandlerType != null && this.screenFactory != null) ScreenRegistry.register(this.screenHandlerType, this.screenFactory);
        return this;
    }

    public BlockRegistryEntry withRecipe(RecipeType<? extends Recipe<?>> recipeType) {
        this.recipeType = recipeType;
        if (this.recipeType != null) Registry.register(Registry.RECIPE_TYPE, id, this.recipeType);
        return this;
    }

    public BlockRegistryEntry withRecipeSerializer(RecipeSerializer<? extends Recipe<?>> recipeSerializer) {
        this.recipeSerializer = recipeSerializer;
        if (this.recipeSerializer != null) Registry.register(Registry.RECIPE_SERIALIZER, id, this.recipeSerializer);
        return this;
    }
}