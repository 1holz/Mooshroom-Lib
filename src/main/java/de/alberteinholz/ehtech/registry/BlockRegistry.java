package de.alberteinholz.ehtech.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;
import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys.consumer.OreGrowerBlockEntity;
import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys.generator.CoalGeneratorBlockEntity;
import de.alberteinholz.ehtech.blocks.components.container.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.MachineBlock;
import de.alberteinholz.ehtech.blocks.guis.controllers.ContainerCraftingController;
import de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers.CoalGeneratorController;
import de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers.MachineConfigController;
import de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers.OreGrowerController;
import de.alberteinholz.ehtech.blocks.guis.screens.EHContainerScreen;
import de.alberteinholz.ehtech.blocks.recipes.MachineRecipe;
import de.alberteinholz.ehtech.itemgroups.ItemGroups;
import de.alberteinholz.ehtech.util.Ref;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public enum BlockRegistry {
    COAL_GENERATOR,
    MACHINE_CONFIG,
    ORE_GROWER;

    //static

    private static void setupAll() {
        COAL_GENERATOR.setup(new MachineBlock(getId(COAL_GENERATOR)), getDefaultItemSettings(), CoalGeneratorBlockEntity::new, getDefaultContainerFactory(CoalGeneratorController.class), getDefaultScreenFactory(CoalGeneratorController.class), getDefaultRecipeType(getId(COAL_GENERATOR)), getDefaultSerializer());
        MACHINE_CONFIG.setup(null, null, null, getDefaultContainerFactory(MachineConfigController.class), getDefaultScreenFactory(MachineConfigController.class), null, null);
        ORE_GROWER.setup(new MachineBlock(getId(ORE_GROWER)), getDefaultItemSettings(), OreGrowerBlockEntity::new, getDefaultContainerFactory(OreGrowerController.class), getDefaultScreenFactory(OreGrowerController.class), getDefaultRecipeType(getId(ORE_GROWER)), getDefaultSerializer());
    }

    public static BlockRegistry getEntry(Identifier id) {
        for (BlockRegistry entry : BlockRegistry.values()) {
            if (entry.name().equalsIgnoreCase(id.getPath())) {
                return entry;
            }
        }
        return null;
    }

    public static Identifier getId(BlockRegistry entry) {
        return new Identifier(Ref.MOD_ID, entry.toString().toLowerCase());
    }

    private static Item.Settings getDefaultItemSettings() {
        return new Item.Settings().group(ItemGroups.EH_TECH);
    }

    private static ContainerFactory<Container> getDefaultContainerFactory(Class<?> containerClass) {
        return (syncId, id, player, buf) -> {
			try {
				return (Container) containerClass.getConstructor(int.class, PlayerInventory.class, BlockContext.class).newInstance(new Object[] {syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())});
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                TechMod.LOGGER.bigBug(e);
                return null;
			}
		};
    }

    @SuppressWarnings("rawtypes")
    private static ContainerFactory<ContainerScreen> getDefaultScreenFactory(Class<?> containerClass) {
        return (syncId, id, player, buf) -> {
			try {
				return new EHContainerScreen((ContainerCraftingController) containerClass.getConstructor(int.class, PlayerInventory.class, BlockContext.class).newInstance(new Object[] {syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos())}), player);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                TechMod.LOGGER.bigBug(e);
                return null;
			}
		};
    }

    private static <T extends Recipe<?>> RecipeType<T> getDefaultRecipeType(Identifier id) {
        return new RecipeType<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public <C extends Inventory> Optional<T> get(Recipe<C> recipe, World world, C inventory) {
                return ((MachineRecipe) recipe).matches(((InventoryWrapper) inventory).pos, world) ? (Optional<T>) Optional.of(recipe) : Optional.empty();
            }

            @Override
            public String toString() {
                return id.getPath();
            }
        };
    }

    private static MachineRecipe.Serializer getDefaultSerializer() {
        return new MachineRecipe.Serializer();
    }

    //non static

    public Block block;
    public Item.Settings itemSettings;
    public Supplier<BlockEntity> blockEntitySupplier;
    public BlockEntityType<BlockEntity> blockEntityType;
    public ContainerFactory<Container> containerFactory;
    @SuppressWarnings("rawtypes")
    public ContainerFactory<ContainerScreen> screenFactory;
    public RecipeType<MachineRecipe> recipeType;
    public RecipeSerializer<?> recipeSerializer;

    @SuppressWarnings("rawtypes")
    private void setup(Block block, Item.Settings itemSettings, Supplier<BlockEntity> blockEntitySupplier, ContainerFactory<Container> containerFactory, ContainerFactory<ContainerScreen> screenFactory, RecipeType<MachineRecipe> recipeType, RecipeSerializer<?> recipeSerializer) {
        this.block = block;
        this.itemSettings = itemSettings;
        this.blockEntitySupplier = blockEntitySupplier;
        this.containerFactory = containerFactory;
        this.screenFactory = screenFactory;
        this.recipeType = recipeType;
        this.recipeSerializer = recipeSerializer;
    }

    public static void registerBlocks() {
        setupAll();
        for (BlockRegistry entry : BlockRegistry.values()) {
            if (entry.block != null) {
                Registry.register(Registry.BLOCK, getId(entry), entry.block);
            }
            if (entry.itemSettings != null && entry.block != null) {
                Registry.register(Registry.ITEM, getId(entry), new BlockItem(entry.block, entry.itemSettings));
            }
            if (entry.blockEntitySupplier != null && entry.block != null) {
                entry.blockEntityType = Registry.register(Registry.BLOCK_ENTITY_TYPE, getId(entry), BlockEntityType.Builder.create(entry.blockEntitySupplier, entry.block).build(null));
            }
            if (entry.containerFactory != null) {
                ContainerProviderRegistry.INSTANCE.registerFactory(getId(entry), entry.containerFactory);
            }
            if (entry.recipeType != null) {
                Registry.register(Registry.RECIPE_TYPE, getId(entry), entry.recipeType);
            }
            if (entry.recipeSerializer != null) {
                Registry.register(Registry.RECIPE_SERIALIZER, getId(entry), entry.recipeSerializer);
            }
        }
    }

    public static void registerBlockScreens() {
        for (BlockRegistry entry : BlockRegistry.values()) {
            if (entry.screenFactory != null) {
                ScreenProviderRegistry.INSTANCE.registerFactory(getId(entry), entry.screenFactory);
            }
        }
    }
}