package de.einholz.ehmooshroom.registry;

import java.util.function.Function;
import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class RegEntryBuilder {
    private Identifier id;
    private Function<RegEntryBuilder, Block> blockFunc = (entry) -> null;
    private Block block;
    private Function<RegEntryBuilder, BlockEntityType<? extends BlockEntity>> blockEntityTypeFunc = (entry) -> null;
    private BlockEntityType<? extends BlockEntity> blockEntityType;
    //private Factory<? extends BlockEntity> blockEntityTypeFactory;
    private Function<RegEntryBuilder, Item> itemFunc = (entry) -> null;
    private Item item;
    private Function<RegEntryBuilder, Integer> fuelTicks;

    /*
    //supplied:
    private ItemGroup itemGroup;
    //private ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    //private Factory<ScreenHandler, HandledScreen<ScreenHandler>> screenFactory;
    private RecipeType<? extends Recipe<?>> recipeType;
    private RecipeSerializer<? extends Recipe<?>> recipeSerializer;
    //created:
    //private ScreenHandlerType<? extends ScreenHandler> screenHandlerType;
    */

    public RegEntryBuilder applyTemplate(Function<RegEntryBuilder, ? extends RegEntryBuilder> template) {
        return template.apply(this);
    }

    protected LoggerHelper getLogger() {
        return MooshroomLib.LOGGER;
    }

    public Identifier getId() {
        if (id == null) getLogger().smallBug(new NullPointerException("Identifier is null! Probably it wasn't assigned yet"));
        return id;
    }

    // BLOCKS:
    public RegEntryBuilder withBlockRaw(Function<RegEntryBuilder, Block> blockFunc) {
        this.blockFunc = blockFunc;
        return this;
    }

    public RegEntryBuilder withBlockNull() {
        return withBlockRaw(entry -> null);
    }

    @FunctionalInterface
    public static interface BlockFactory<B extends Block> {
        B create(AbstractBlock.Settings settings);
    }

    public RegEntryBuilder withBlockBuild(BlockFactory<? extends Block> factory, AbstractBlock.Settings settings) {
        return withBlockRaw((entry) -> factory.create(settings));
    }

    // BLOCK ENTITIES:
    public RegEntryBuilder withBlockEntityRaw(Function<RegEntryBuilder, BlockEntityType<? extends BlockEntity>> blockEntityTypeFunc) {
        this.blockEntityTypeFunc = blockEntityTypeFunc;
        return this;
    }

    public RegEntryBuilder withBlockEntityNull() {
        return withBlockEntityRaw((entry) -> null);
    }

    public RegEntryBuilder withBlockEntityCustomBlocksBuild(Factory<? extends BlockEntity> blockEntityTypeFactory, Block... blocks) {
        return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, blocks).build());
    }

    public RegEntryBuilder withBlockEntityBuild(Factory<? extends BlockEntity> blockEntityTypeFactory) {
        return withBlockEntityCustomBlocksBuild(blockEntityTypeFactory, this.blockFunc.apply(this));
    }

    // ITEMS:
    public RegEntryBuilder withItemRaw(Function<RegEntryBuilder, Item> itemFunc) {
        this.itemFunc = itemFunc;
        return this;
    }

    public RegEntryBuilder withItemNull() {
        return withItemRaw((entry) -> null);
    }

    /*
    public RegEntry withItemBuildAutoItemGroup(ItemFactory<? extends Item> factory, Settings settings) {
        return withItemBuild(factory, settings.group(itemGroup));
    }
    */

    @FunctionalInterface
    public static interface ItemFactory<I extends Item> {
        I create(Item.Settings itemSettings);
    }

    public RegEntryBuilder withItemBuild(ItemFactory<? extends Item> factory, Item.Settings settings) {
        return withItemRaw((entry)-> factory.create(settings));
    }

    public RegEntryBuilder withBlockItemBuild(Item.Settings settings) {
        if (block == null) {
            getLogger().smallBug(new NullPointerException("You must add a Block before BlockItemBuild for " + id.toString()));
            return this;
        }
        return withItemRaw((entry) -> new BlockItem(block, settings));
    }

    public RegEntryBuilder withFuel(int ticks) {
        fuelTicks = (entry) -> ticks;
        return this;
    }

    public RegEntryBuilder withFuelNull() {
        fuelTicks = (entry) -> null;
        return this;
    }

    /*
    public RegEntry withItemGroupBuild() {
        return withItemGroup(FabricItemGroupBuilder.create(id).icon(() -> new ItemStack(item)).build());
    }
    */

    //Is this needed? Maybe for templates only?
    /*
    public RegEntry withItemGroup(ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }
    */

    /*
    public RegEntry withGui(ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
        if (this.clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, this.clientHandlerFactory);
        return this;
    }

    //FIXME: IF YOU KNOW A BETTER WAY OF DOING THIS PLEASE TELL ME!!!
    @SuppressWarnings({"rawtypes", "unchecked"})
    public RegEntry withScreenHacky(Factory screenFactory) {
        return withScreen(screenFactory);
    }

    public RegEntry withScreen(Factory<ScreenHandler, HandledScreen<ScreenHandler>> screenFactory) {
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

    /*
    public RegEntry withRecipe(RecipeType<? extends Recipe<?>> recipeType) {
        this.recipeType = recipeType;
        if (this.recipeType != null) Registry.register(Registry.RECIPE_TYPE, id, this.recipeType);
        return this;
    }
    */

    /*
    public RegEntry withRecipeSerializer(RecipeSerializer<? extends Recipe<?>> recipeSerializer) {
        this.recipeSerializer = recipeSerializer;
        if (this.recipeSerializer != null) Registry.register(Registry.RECIPE_SERIALIZER, id, this.recipeSerializer);
        return this;
    }
    */

    // BUILDING:
    public RegEntry build(String path) {
        return build(MooshroomLib.HELPER.makeId(path));
    }

    public RegEntry build(Identifier id) {
        this.id = id;
        block = blockFunc.apply(this);
        blockEntityType = blockEntityTypeFunc.apply(this);
        item = itemFunc.apply(this);
        if (fuelTicks != null) {
            if (item == null) getLogger().smallBug(new NullPointerException("You must add an Item before making it a fuel for " + id.toString()));
            else FuelRegistry.INSTANCE.add(item, fuelTicks.apply(this));
        }
        return new RegEntry(id, block, blockEntityType, item);
    }
}
