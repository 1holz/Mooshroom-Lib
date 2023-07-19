package de.einholz.ehmooshroom.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.rebs.BlockEntitiesREB;
import de.einholz.ehmooshroom.registry.rebs.BlocksREB;
import de.einholz.ehmooshroom.registry.rebs.GuisREB;
import de.einholz.ehmooshroom.registry.rebs.ItemsREB;
import de.einholz.ehmooshroom.registry.rebs.RecipesREB;
import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockEntityApiProvider;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

// TODO use var instead of all the generics
public class RegEntryBuilder<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>>
        implements BlocksREB<B, G, S, R>, BlockEntitiesREB<B, G, S, R>, ItemsREB<B, G, S, R>, GuisREB<B, G, S, R>,
        RecipesREB<B, G, S, R> {
    private static final HashMap<Identifier, Collection<ItemStack>> itemGroupLists = new HashMap<>();
    private Identifier id;
    private Function<RegEntryBuilder<B, G, S, R>, Block> blockFunc = entry -> null;
    private Block block;
    private Map<Transferable<?, ?>, Function<RegEntryBuilder<B, G, S, R>, BlockApiProvider<Storage<?>, Direction>>> blockStorageProvFuncList = new HashMap<>();
    private Function<RegEntryBuilder<B, G, S, R>, BlockEntityType<B>> blockEntityTypeFunc = entry -> null;
    private BlockEntityType<B> blockEntityType;
    private Map<Transferable<?, ?>, Function<RegEntryBuilder<B, G, S, R>, BlockEntityApiProvider<Storage<?>, Direction>>> blockEntityStorageProvFuncList = new HashMap<>();
    private Function<RegEntryBuilder<B, G, S, R>, Item> itemFunc = entry -> null;
    private Item item;
    private Function<RegEntryBuilder<B, G, S, R>, Integer> fuelTicks = entry -> null;
    private Function<RegEntryBuilder<B, G, S, R>, Identifier> itemGroupAddFunc = entry -> null;
    private Function<RegEntryBuilder<B, G, S, R>, Boolean> itemGroupCreateFunc = entry -> false;
    private ItemGroup itemGroup;
    private Function<RegEntryBuilder<B, G, S, R>, ScreenHandlerType<G>> guiFunc = entry -> null;
    private ScreenHandlerType<G> gui;
    private Function<RegEntryBuilder<B, G, S, R>, ScreenRegistry.Factory<G, S>> screenFunc = entry -> null;
    private ScreenRegistry.Factory<G, S> screen;
    private Function<RegEntryBuilder<B, G, S, R>, RecipeType<R>> recipeTypeFunc = entry -> null;
    private RecipeType<R> recipeType;
    private Function<RegEntryBuilder<B, G, S, R>, RecipeSerializer<R>> recipeSerializerFunc = entry -> null;
    private RecipeSerializer<R> recipeSerializer;

    protected LoggerHelper getLogger() {
        return MooshroomLib.LOGGER;
    }

    protected Function<String, Identifier> getEasyIdFactory() {
        return MooshroomLib.HELPER::makeId;
    }

    public Identifier getId() {
        if (id != null)
            return id;
        getLogger().warnBug("Identifier is null! Probably it was not assigned yet");
        return getEasyIdFactory().apply("invalid");
    }

    // BLOCKS
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withBlockRaw(Function<RegEntryBuilder<B, G, S, R>, Block> blockFunc) {
        this.blockFunc = blockFunc;
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withBlockStorageProvFunc(Transferable<?, ?> trans,
            Function<RegEntryBuilder<B, G, S, R>, BlockApiProvider<Storage<?>, Direction>> storageProvFunc) {
        blockStorageProvFuncList.put(trans, storageProvFunc);
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withoutBlockStorageProvFunc(Transferable<?, ?> trans) {
        blockStorageProvFuncList.remove(trans);
        return this;
    }

    // BLOCK ENTITIES
    public BlockEntityType<B> getBlockEntityType() {
        return blockEntityType;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withBlockEntityRaw(
            Function<RegEntryBuilder<B, G, S, R>, BlockEntityType<B>> blockEntityTypeFunc) {
        this.blockEntityTypeFunc = blockEntityTypeFunc;
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withBlockEntityStorageProvFunc(Transferable<?, ?> trans,
            Function<RegEntryBuilder<B, G, S, R>, BlockEntityApiProvider<Storage<?>, Direction>> storageProvFunc) {
        blockEntityStorageProvFuncList.put(trans, storageProvFunc);
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withoutBlockEntityStorageProvFunc(Transferable<?, ?> trans) {
        blockEntityStorageProvFuncList.remove(trans);
        return this;
    }

    // ITEMS
    public Item getItem() {
        return item;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withItemRaw(Function<RegEntryBuilder<B, G, S, R>, Item> itemFunc) {
        this.itemFunc = itemFunc;
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withFuelRaw(Function<RegEntryBuilder<B, G, S, R>, Integer> fuelTicks) {
        this.fuelTicks = fuelTicks;
        return this;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withItemGroupAddRaw(Function<RegEntryBuilder<B, G, S, R>, Identifier> itemGroupAddFunc) {
        this.itemGroupAddFunc = itemGroupAddFunc;
        return this;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withItemGroupCreateRaw(Function<RegEntryBuilder<B, G, S, R>, Boolean> itemGroupCreateFunc) {
        this.itemGroupCreateFunc = itemGroupCreateFunc;
        return this;
    }

    // GUIS
    public ScreenHandlerType<G> getGui() {
        return gui;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withGuiRaw(Function<RegEntryBuilder<B, G, S, R>, ScreenHandlerType<G>> guiFunc) {
        this.guiFunc = guiFunc;
        return this;
    }

    public ScreenRegistry.Factory<G, S> getScreen() {
        return screen;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withScreenRaw(
            Function<RegEntryBuilder<B, G, S, R>, ScreenRegistry.Factory<G, S>> screenFunc) {
        this.screenFunc = screenFunc;
        return this;
    }

    // RECIPES
    public RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withRecipeTypeRaw(
            Function<RegEntryBuilder<B, G, S, R>, RecipeType<R>> recipeTypeFunc) {
        this.recipeTypeFunc = recipeTypeFunc;
        return this;
    }

    public RecipeSerializer<R> getRecipeSerializer() {
        return recipeSerializer;
    }

    @Override
    public RegEntryBuilder<B, G, S, R> withRecipeSerializerRaw(
            Function<RegEntryBuilder<B, G, S, R>, RecipeSerializer<R>> recipeSerializerFunc) {
        this.recipeSerializerFunc = recipeSerializerFunc;
        return this;
    }

    // BUILDING
    public RegEntry<B, G, S, R> build(String path) {
        return build(getEasyIdFactory().apply(path));
    }

    public RegEntry<B, G, S, R> build(Identifier id) {
        this.id = id;
        block = blockFunc.apply(this);
        if (getBlock() != null) {
            // XXX faster if registerForBlocks is properly used?
            for (var entry : blockStorageProvFuncList.entrySet()) {
                BlockApiLookup<?, Direction> lookup = entry.getKey().getLookup();
                if (lookup == null)
                    continue;
                ((BlockApiLookup<Storage<?>, Direction>) lookup)
                        .registerForBlocks(entry.getValue().apply(this), getBlock());
            }
        }
        blockEntityType = blockEntityTypeFunc.apply(this);
        if (getBlockEntityType() != null) {
            // XXX faster if registerForBlockEntities is properly used?
            for (var entry : blockEntityStorageProvFuncList.entrySet()) {
                BlockApiLookup<?, Direction> lookup = entry.getKey().getLookup();
                if (lookup == null)
                    continue;
                ((BlockApiLookup<Storage<?>, Direction>) lookup)
                        .registerForBlockEntities(entry.getValue().apply(this), getBlockEntityType());
            }
        }
        item = itemFunc.apply(this);
        Integer fuelTicksInt = fuelTicks.apply(this);
        if (getItem() != null && fuelTicksInt != null && fuelTicksInt > 0)
            FuelRegistry.INSTANCE.add(getItem(), fuelTicks.apply(this));
        Identifier itemGroupId = itemGroupAddFunc.apply(this);
        if (itemGroupId != null && getItem() != null) {
            Collection<ItemStack> itemGroupList = itemGroupLists.getOrDefault(itemGroupId, new ArrayList<>());
            itemGroupList.add(getItem().getDefaultStack());
            itemGroupLists.putIfAbsent(itemGroupId, itemGroupList);
        }
        Boolean createItemGroup = itemGroupCreateFunc.apply(this);
        if (createItemGroup != null && createItemGroup && getItem() != null)
            itemGroup = FabricItemGroupBuilder.create(id).icon(getItem()::getDefaultStack).appendItems(stacks -> {
                // .addAll(...) throws NullPointerException for some reason
                // stacks.addAll(itemGroupList.get(getId()));
                for (ItemStack stack : itemGroupLists.getOrDefault(getId(), new ArrayList<>()))
                    stacks.add(stack);
            }).build();
        gui = guiFunc.apply(this);
        screen = screenFunc.apply(this);
        if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType()) && getGui() != null
                && getScreen() != null) {
            ScreenRegistry.register(getGui(), getScreen());
            // HandledScreens.register
            // HandledScreens.<SyncedGuiDescription, CottonInventoryScreen<? extends
            // SyncedGuiDescription>>register(GUI, (gui, inventory, title) -> new
            // ContainerScreen(gui, inventory.player, title));
        }
        recipeType = recipeTypeFunc.apply(this);
        recipeSerializer = recipeSerializerFunc.apply(this);
        return new RegEntry<B, G, S, R>(getId(), getBlock(), getBlockEntityType(), getItem(), getItemGroup(), getGui(), getScreen(),
                getRecipeType(), getRecipeSerializer());
    }
}
