package de.einholz.ehmooshroom.registry.deprecated;

import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Deprecated
public class RegistryEntry {
    public final Identifier id;
    //supplied:
    public Block block;
    public Item item;
    public ItemGroup itemGroup;
    public BlockEntityType<? extends BlockEntity> blockEntityType;
    //public ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    //public Factory<ScreenHandler, HandledScreen<ScreenHandler>> screenFactory;
    public RecipeType<? extends Recipe<?>> recipeType;
    public RecipeSerializer<? extends Recipe<?>> recipeSerializer;
    //created:
    //public ScreenHandlerType<? extends ScreenHandler> screenHandlerType;

    protected RegistryEntry(Identifier id) {
        this.id = id;
    }

    public RegistryEntry withBlockBuild(BlockFactory<? extends Block> factory, AbstractBlock.Settings blockSettings) {
        return withBlock(factory.create(blockSettings));
    }

    @FunctionalInterface
    public static interface BlockFactory<B extends Block> {
        B create(AbstractBlock.Settings blockSettings);
    }

    public RegistryEntry withBlock(Block block) {
        this.block = block;
        if (this.block != null) Registry.register(Registry.BLOCK, id, this.block);
        return this;
    }

    public RegistryEntry withBlockItemBuild(Settings itemSettings) {
        if (block == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Block before BlockItemBuild for " + id.toString()));
            return this;
        }
        return withItem(new BlockItem(this.block, itemSettings));
    }

    public RegistryEntry withItemBuildAutoItemGroup(ItemFactory<? extends Item> factory, Settings settings) {
        return withItemBuild(factory, settings.group(itemGroup));
    }

    public RegistryEntry withItemBuild(ItemFactory<? extends Item> factory, Item.Settings settings) {
        return withItem(factory.create(settings));
    }

    @FunctionalInterface
    public static interface ItemFactory<I extends Item> {
        I create(Item.Settings itemSettings);
    }

    public RegistryEntry withItem(Item item) {
        this.item = item;
        if (this.item != null) Registry.register(Registry.ITEM, id, this.item);
        return this;
    }

    public RegistryEntry makeItemFurnaceFuel(int ticks) {
        if (item == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add an Item before making it a fuel for " + id.toString()));
            return this;
        }
        FuelRegistry.INSTANCE.add(item, ticks);
        return this;
    }

    public RegistryEntry withItemGroupBuild() {
        return withItemGroup(FabricItemGroupBuilder.create(id).icon(() -> new ItemStack(item)).build());
    }

    //Is this needed? Maybe for templates only?
    public RegistryEntry withItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public RegistryEntry withBlockEntityBuild(Factory<? extends BlockEntity> blockEntitySupplier) {
        if (block == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Block before BlockEntityBuild for " + id.toString()));
            return this;
        }
        return withBlockEntity(FabricBlockEntityTypeBuilder.create(blockEntitySupplier, this.block).build(null));
    }

    public RegistryEntry withBlockEntity(BlockEntityType<? extends BlockEntity> blockEntityType) {
        this.blockEntityType = blockEntityType;
        if (this.blockEntityType != null) Registry.register(Registry.BLOCK_ENTITY_TYPE, id, this.blockEntityType);
        return this;
    }

    /*
    public RegistryEntry withGui(ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
        if (this.clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, this.clientHandlerFactory);
        return this;
    }

    //FIXME: IF YOU KNOW A BETTER WAY OF DOING THIS PLEASE TELL ME!!!
    @SuppressWarnings({"rawtypes", "unchecked"})
    public RegistryEntry withScreenHacky(Factory screenFactory) {
        return withScreen(screenFactory);
    }

    public RegistryEntry withScreen(Factory<ScreenHandler, HandledScreen<ScreenHandler>> screenFactory) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return this;
        this.screenFactory = screenFactory;
        if (screenHandlerType == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Gui before Screen for " + id.toString()));
            return this;
        }
        if (screenHandlerType != null && this.screenFactory != null) ScreenRegistry.<ScreenHandler, HandledScreen<ScreenHandler>>register(screenHandlerType, this.screenFactory);
        return this;
    }
    */

    public RegistryEntry withRecipe(RecipeType<? extends Recipe<?>> recipeType) {
        this.recipeType = recipeType;
        if (this.recipeType != null) Registry.register(Registry.RECIPE_TYPE, id, this.recipeType);
        return this;
    }

    public RegistryEntry withRecipeSerializer(RecipeSerializer<? extends Recipe<?>> recipeSerializer) {
        this.recipeSerializer = recipeSerializer;
        if (this.recipeSerializer != null) Registry.register(Registry.RECIPE_SERIALIZER, id, this.recipeSerializer);
        return this;
    }

    public RegistryEntry applyTemplate(Function<RegistryEntry, RegistryEntry> template) {
        return template.apply(this);
    }
}
