package de.einholz.ehmooshroom.registry;

import java.util.function.BiFunction;
import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.providers.FluidStorageProv;
import de.einholz.ehmooshroom.storage.providers.ItemStorageProv;
import de.einholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class RegEntryBuilder<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>> {
    private Identifier id;
    private Function<RegEntryBuilder<B, G, S>, Block> blockFunc = (entry) -> null;
    private Block block;
    private Function<RegEntryBuilder<B, G, S>, BlockApiProvider<Storage<ItemVariant>, Direction>> blockItemStorageProvFunc = (entry) -> null;
    private Function<RegEntryBuilder<B, G, S>, BlockApiProvider<Storage<FluidVariant>, Direction>> blockFluidStorageProvFunc = (entry) -> null;
    private Function<RegEntryBuilder<B, G, S>, BlockEntityType<B>> blockEntityTypeFunc = (entry) -> null;
    private BlockEntityType<B> blockEntityType;
    private Function<RegEntryBuilder<B, G, S>, BiFunction<B, Direction, Storage<ItemVariant>>> blockEntityItemStorageProvFunc = (entry) -> null;
    private Function<RegEntryBuilder<B, G, S>, BiFunction<B, Direction, Storage<FluidVariant>>> blockEntityFluidStorageProvFunc = (entry) -> null;
    private Function<RegEntryBuilder<B, G, S>, Item> itemFunc = (entry) -> null;
    private Item item;
    private Function<RegEntryBuilder<B, G, S>, Integer> fuelTicks;
    private Function<RegEntryBuilder<B, G, S>, ScreenHandlerType<G>> guiFunc = (entry) -> null;
    private ScreenHandlerType<G> gui;
    private Function<RegEntryBuilder<B, G, S>, ScreenRegistry.Factory<G, S>> screenFunc = (entry) -> null;
    private ScreenRegistry.Factory<G, S> screen;

    /*
    private Factory<B> blockEntityTypeFactory;

    //supplied:
    private ItemGroup itemGroup;
    //private ExtendedClientHandlerFactory<G> clientHandlerFactory;
    //private Factory<ScreenHandler, HandledScreen<ScreenHandler>> screenFactory;
    private RecipeType<? extends Recipe<?>> recipeType;
    private RecipeSerializer<? extends Recipe<?>> recipeSerializer;
    //created:
    //private ScreenHandlerType<G> screenHandlerType;
    */

    @Deprecated // TODO del if uunused
    @SuppressWarnings("unchecked") // XXX is there a better way to do this?
    public RegEntryBuilder<B, G, S> applyTemplate(Function<RegEntryBuilder<B, G, S>, ? extends RegEntryBuilder<? extends B, ? extends G, ? extends S>> template) {
        return (RegEntryBuilder<B, G, S>) template.apply(this);
    }

    protected LoggerHelper getLogger() {
        return MooshroomLib.LOGGER;
    }

    protected Function<String, Identifier> getEasyIdFactory() {
        return MooshroomLib.HELPER::makeId;
    }

    public Identifier getId() {
        if (id == null) {
            getLogger().smallBug(new NullPointerException("Identifier is null! Probably it wasn't assigned yet"));
            return getEasyIdFactory().apply("invalid");
        }
        return id;
    }

    // BLOCKS:
    public Block getBlock() {
        return block;
    }

    public RegEntryBuilder<B, G, S> withBlockRaw(Function<RegEntryBuilder<B, G, S>, Block> blockFunc) {
        this.blockFunc = blockFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockNull() {
        return withBlockRaw(entry -> null);
    }

    @FunctionalInterface
    public static interface BlockFactory<B extends Block> {
        B create(AbstractBlock.Settings settings);
    }

    public RegEntryBuilder<B, G, S> withBlockBuild(BlockFactory<? extends Block> factory, AbstractBlock.Settings settings) {
        return withBlockRaw((entry) -> factory.create(settings));
    }

    public RegEntryBuilder<B, G, S> withBlockItemStorageProvRaw(Function<RegEntryBuilder<B, G, S>, BlockApiProvider<Storage<ItemVariant>, Direction>> blockItemStorageProvFunc) {
        this.blockItemStorageProvFunc = blockItemStorageProvFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockItemStorageProvNull() {
        return withBlockItemStorageProvRaw(entry -> null);
    }

    public RegEntryBuilder<B, G, S> withBlockFluidStorageProvRaw(Function<RegEntryBuilder<B, G, S>, BlockApiProvider<Storage<FluidVariant>, Direction>> blockFluidStorageProvFunc) {
        this.blockFluidStorageProvFunc = blockFluidStorageProvFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockFluidStorageProvNull() {
        return withBlockFluidStorageProvRaw(entry -> null);
    }

    // BLOCK ENTITIES:
    public BlockEntityType<B> getBlockEntityType() {
        return blockEntityType;
    }

    public RegEntryBuilder<B, G, S> withBlockEntityRaw(Function<RegEntryBuilder<B, G, S>, BlockEntityType<B>> blockEntityTypeFunc) {
        this.blockEntityTypeFunc = blockEntityTypeFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockEntityNull() {
        return withBlockEntityRaw((entry) -> null);
    }

    public RegEntryBuilder<B, G, S> withBlockEntityCustomBlocksBuild(Factory<B> blockEntityTypeFactory, Block... blocks) {
        return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, blocks).build());
    }

    public RegEntryBuilder<B, G, S> withBlockEntityBuild(Factory<B> blockEntityTypeFactory) {
        return withBlockEntityCustomBlocksBuild(blockEntityTypeFactory, this.blockFunc.apply(this));
    }

    public RegEntryBuilder<B, G, S> withBlockEntityItemStorageProvRaw(Function<RegEntryBuilder<B, G, S>, BiFunction<B, Direction, Storage<ItemVariant>>> blockEntityItemStorageProvFunc) {
        this.blockEntityItemStorageProvFunc = blockEntityItemStorageProvFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockEntityItemStorageProvNull() {
        return withBlockEntityItemStorageProvRaw(entry -> null);
    }

    public RegEntryBuilder<B, G, S> withBlockEntityItemStorageProvBuild() {
        return withBlockEntityItemStorageProvRaw(entry -> (be, dir) -> ((ItemStorageProv) be).getItemStorage(dir));
    }

    public RegEntryBuilder<B, G, S> withBlockEntityFluidStorageProvRaw(Function<RegEntryBuilder<B, G, S>, BiFunction<B, Direction, Storage<FluidVariant>>> blockEntityFluidStorageProvFunc) {
        this.blockEntityFluidStorageProvFunc = blockEntityFluidStorageProvFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withBlockEntityFluidStorageProvNull() {
        return withBlockEntityFluidStorageProvRaw(entry -> null);
    }

    public RegEntryBuilder<B, G, S> withBlockEntityFluidStorageProvBuild() {
        return withBlockEntityFluidStorageProvRaw(entry -> (be, dir) -> ((FluidStorageProv) be).getFluidStorage(dir));
    }

    // ITEMS:
    public Item getItem() {
        return item;
    }

    public RegEntryBuilder<B, G, S> withItemRaw(Function<RegEntryBuilder<B, G, S>, Item> itemFunc) {
        this.itemFunc = itemFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withItemNull() {
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

    public RegEntryBuilder<B, G, S> withItemBuild(ItemFactory<? extends Item> factory, Item.Settings settings) {
        return withItemRaw((entry)-> factory.create(settings));
    }

    public RegEntryBuilder<B, G, S> withBlockItemBuild(Item.Settings settings) {
        if (blockFunc.apply(this) == null) {
            getLogger().smallBug(new NullPointerException("You must add a Block before BlockItemBuild for " + getId().toString()));
            return this;
        }
        return withItemRaw((entry) -> new BlockItem(getBlock(), settings));
    }

    public RegEntryBuilder<B, G, S> withFuel(int ticks) {
        fuelTicks = (entry) -> ticks;
        return this;
    }

    public RegEntryBuilder<B, G, S> withFuelNull() {
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

    // GUIS:
    public ScreenHandlerType<G> getGui() {
        return gui;
    }

    public RegEntryBuilder<B, G, S> withGuiRaw(Function<RegEntryBuilder<B, G, S>, ScreenHandlerType<G>> guiFunc) {
        this.guiFunc = guiFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withGuiNull() {
        return withGuiRaw((entry) -> null);
    }

    @FunctionalInterface
    public static interface GuiFactory<G extends ScreenHandler, T extends ScreenHandlerType<G>> {
        T create(ExtendedClientHandlerFactory<G> factory);
    }

    public <T extends ScreenHandlerType<G>> RegEntryBuilder<B, G, S> withGuiBuild(GuiFactory<G, T> factory, ExtendedClientHandlerFactory<G> clientHandlerFactory) {
        return withGuiRaw((entry) -> factory.create(clientHandlerFactory));
    }

    public ScreenRegistry.Factory<G, S> getScreen() {
        return screen;
    }

    public RegEntryBuilder<B, G, S> withScreenRaw(Function<RegEntryBuilder<B, G, S>, ScreenRegistry.Factory<G, S>> screenFunc) {
        if (guiFunc.apply(this) == null) {
            getLogger().smallBug(new NullPointerException("You must add a Gui before the Screen for " + getId().toString()));
            return this;
        }
        this.screenFunc = screenFunc;
        return this;
    }

    public RegEntryBuilder<B, G, S> withScreenNull() {
        return withScreenRaw((entry) -> null);
    }

    /* TODO del if not needed
    @FunctionalInterface
    public static interface ScreenFactory<G extends ScreenHandler, T extends ScreenHandlerType<G>> {
        T create(ExtendedClientHandlerFactory<G> factory);
    }
    */

    public RegEntryBuilder<B, G, S> withScreenBuild(ScreenRegistry.Factory<G, S> screenFactory) {
        return withScreenRaw((entry) -> screenFactory);
    }

    /*
    // fixme IF YOU KNOW A BETTER WAY OF DOING THIS PLEASE TELL ME!!!
    @SuppressWarnings({"rawtypes", "unchecked"})
    public RegEntry withScreenHacky(Factory screenFactory) {
        return withScreen(screenFactory);
    }

    public RegEntry<B, G, S> withScreen(Factory<G, S> screenFactory) {
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

    /* TODO del WithGui
    public RegEntry withGui(ExtendedClientHandlerFactory<G> clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
        if (this.clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, this.clientHandlerFactory);
        return this;
    }

    // fixme IF YOU KNOW A BETTER WAY OF DOING THIS PLEASE TELL ME!!!
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
    public RegEntry<B, G, S> build(String path) {
        return build(getEasyIdFactory().apply(path));
    }

    public RegEntry<B, G, S> build(Identifier id) {
        this.id = id;
        block = blockFunc.apply(this);
        if (getBlock() != null) {
            // XXX faster if registerForBlockEntities is used?
            if (blockItemStorageProvFunc.apply(this) != null)
                ItemStorage.SIDED.registerForBlocks(blockItemStorageProvFunc.apply(this), getBlock());
            if (blockFluidStorageProvFunc.apply(this) != null)
                FluidStorage.SIDED.registerForBlocks(blockFluidStorageProvFunc.apply(this), getBlock());
        }
        blockEntityType = blockEntityTypeFunc.apply(this);
        if (getBlockEntityType() != null) {
            // XXX faster if registerForBlockEntities is used?
            if (blockEntityItemStorageProvFunc.apply(this) != null)
                ItemStorage.SIDED.registerForBlockEntity(blockEntityItemStorageProvFunc.apply(this), getBlockEntityType());
            if (blockEntityFluidStorageProvFunc.apply(this) != null)
                FluidStorage.SIDED.registerForBlockEntity(blockEntityFluidStorageProvFunc.apply(this), getBlockEntityType());
        }
        item = itemFunc.apply(this);
        if (fuelTicks != null) {
            if (getItem() == null) getLogger().smallBug(new NullPointerException("You must add an Item before making it a fuel for " + id.toString()));
            else FuelRegistry.INSTANCE.add(getItem(), fuelTicks.apply(this));
        }
        gui = guiFunc.apply(this);
        screen = screenFunc.apply(this);
        if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType()) && getGui() != null && getScreen() != null) {
            ScreenRegistry.register(getGui(), getScreen());
            //HandledScreens.register
            //HandledScreens.<SyncedGuiDescription, CottonInventoryScreen<? extends SyncedGuiDescription>>register(GUI, (gui, inventory, title) -> new ContainerScreen(gui, inventory.player, title));
        }
        return new RegEntry<B, G, S>(getId(), getBlock(), getBlockEntityType(), getItem(), getGui(), getScreen());
    }
}
