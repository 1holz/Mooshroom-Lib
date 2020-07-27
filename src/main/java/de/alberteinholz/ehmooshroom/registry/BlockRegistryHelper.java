package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;
import java.util.function.Supplier;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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
    public ExtendedClientHandlerFactory<? extends ScreenHandler> defaultClientHandlerFactory;
    public Factory<ScreenHandler, HandledScreen<ScreenHandler>> defaultScreenFactory;
    public RecipeType<? extends Recipe<?>> defaultRecipeType;
    public RecipeSerializer<? extends Recipe<?>> defaultRecipeSerializer;

    public BlockRegistryHelper() {
        this(null, null, null, null, null, null, null);
    }

    /*TODO: machine registry
    public BlockRegistryHelper(Block defaultBlock, Settings defaultItemSettings, Supplier<? extends BlockEntity> defaultBlockEntitySupplier, ExtendedClientHandlerFactory<ScreenHandler> defaultClientHandlerFactory, Factory<ScreenHandler, ? extends Screen> defaultScreenFactory, RecipeType<? extends Recipe<?>> defaultRecipeType, RecipeSerializer<? extends Recipe<?>> defaultRecipeSerializer) {

    }
    */

    public BlockRegistryHelper(Block defaultBlock, Settings defaultItemSettings, Supplier<? extends BlockEntity> defaultBlockEntitySupplier, ExtendedClientHandlerFactory<? extends ScreenHandler> defaultClientHandlerFactory, Factory<ScreenHandler, HandledScreen<ScreenHandler>> defaultScreenFactory, RecipeType<? extends Recipe<?>> defaultRecipeType, RecipeSerializer<? extends Recipe<?>> defaultRecipeSerializer) {
        this.defaultBlock = defaultBlock;
        this.defaultItemSettings = defaultItemSettings;
        this.defaultBlockEntitySupplier = defaultBlockEntitySupplier;
        this.defaultClientHandlerFactory = defaultClientHandlerFactory;
        this.defaultScreenFactory = defaultScreenFactory;
        this.defaultRecipeType = defaultRecipeType;
        this.defaultRecipeSerializer = defaultRecipeSerializer;
    }

    public static BlockRegistryEntry create(Identifier id) throws NullPointerException {
        if (id == null) {
            NullPointerException e = new NullPointerException("Skiping creation of BlockRegistryEntry with null id.");
            MooshroomLib.LOGGER.smallBug(e);
            throw e;
        }
        BlockRegistryEntry entry = BLOCKS.containsKey(id) ? BLOCKS.get(id) : new BlockRegistryEntry(id);
        BLOCKS.putIfAbsent(id, entry);
        return entry;
    }
}