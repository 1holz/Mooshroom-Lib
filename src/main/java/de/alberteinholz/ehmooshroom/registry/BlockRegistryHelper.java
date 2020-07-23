package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public class BlockRegistryHelper {
    public static final HashMap<Identifier, BlockRegistryEntry> BLOCKS = new HashMap<>();
    public Block defaultBlock;
    public Settings defaultItemSettings;
    public Supplier<? extends BlockEntity> defaultBlockEntitySupplier;
    public ExtendedClientHandlerFactory<ScreenHandler> defaultClientHandlerFactory;
    public Factory<ScreenHandler, ? extends Screen> defaultScreenFactory; //if you know a way to make ? extends Screen & ScreenHandlerProvider<? extends ScreenHandler> tell me
    public RecipeType<? extends Recipe<?>> defaultRecipeType;
    public RecipeSerializer<? extends Recipe<?>> defaultRecipeSerializer;

    public BlockRegistryHelper() {
        this(null, null, null, null, null, null, null);
    }

    public BlockRegistryHelper(Block defaultBlock, Settings defaultItemSettings, Supplier<? extends BlockEntity> defaultBlockEntitySupplier, ExtendedClientHandlerFactory<ScreenHandler> defaultClientHandlerFactory, Factory<ScreenHandler, ? extends Screen> defaultScreenFactory, RecipeType<? extends Recipe<?>> defaultRecipeType, RecipeSerializer<? extends Recipe<?>> defaultRecipeSerializer) {
        this.defaultBlock = defaultBlock;
        this.defaultItemSettings = defaultItemSettings;
        this.defaultBlockEntitySupplier = defaultBlockEntitySupplier;
        this.defaultClientHandlerFactory = defaultClientHandlerFactory;
        this.defaultScreenFactory = defaultScreenFactory;
        this.defaultRecipeType = defaultRecipeType;
        this.defaultRecipeSerializer = defaultRecipeSerializer;
    }

    public BlockRegistryEntry register(BlockRegistryEntry entry) {
        return entry.register();
    }

    public BlockRegistryEntry addBlockEntity(Identifier id, Block block, Settings itemSettings, Supplier<? extends BlockEntity> blockEntitySupplier) {
        return addBasicBlock(id, block, itemSettings).withBlockEntity(blockEntitySupplier);
    }

    public BlockRegistryEntry addBasicBlock(Identifier id, Block block, Settings itemSettings) {
        return addTechnicalBlock(id, block).withBlockItem(itemSettings);
    }

    public BlockRegistryEntry addTechnicalBlock(Identifier id, Block block) {
        return create(id).withBlock(block).register();
    }

    protected BlockRegistryEntry create(Identifier id) {
        return BLOCKS.containsKey(id) ? BLOCKS.get(id) : new BlockRegistryEntry(id);
    }
}