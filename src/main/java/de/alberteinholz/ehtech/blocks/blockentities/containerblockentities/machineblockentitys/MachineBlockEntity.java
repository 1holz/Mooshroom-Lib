package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys;

import java.util.Optional;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.ContainerBlockEntity;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.components.container.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.ContainerBlock;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.MachineBlock;
import de.alberteinholz.ehtech.blocks.recipes.Input;
import de.alberteinholz.ehtech.blocks.recipes.MachineRecipe;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.util.Helper;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.type.EnergyTypes;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class MachineBlockEntity extends ContainerBlockEntity implements Tickable {
    public MachineCapacitorComponent capacitor = initializeCapacitorComponent();

    public MachineBlockEntity(BlockEntityType<?> type) {
        super(type);
        capacitor.setDataProvider((MachineDataProviderComponent) data);
        inventory.stacks.put("power_input", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.OTHER));
        inventory.stacks.put("power_output", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.OTHER));
        inventory.stacks.put("upgrade", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.OTHER));
        inventory.stacks.put("network", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.OTHER));
    }

    @Override
    public void tick() {
        MachineDataProviderComponent data = (MachineDataProviderComponent) this.data;
        boolean isRunning = data.progress.getBarCurrent() > data.progress.getBarMinimum() && isActivated();
        transfer();
        if (!isRunning && isActivated()) {
            isRunning = checkForRecipe();
        }
        if (isRunning) {
            if (data.progress.getBarCurrent() == data.progress.getBarMinimum()) {
                start();
            }
            process();
            task();
            if (data.progress.getBarCurrent() == data.progress.getBarMaximum()) {
                finish();
            }
        } else {
            idle();
        }
        correct();
        markDirty();
    }

    public void transfer() {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Block targetBlock = world.getBlockState(targetPos).getBlock();
            if (targetBlock instanceof ContainerBlock) {
                ContainerInventoryComponent inv = (ContainerInventoryComponent) ((ContainerBlock) targetBlock).getComponent(world, targetPos, UniversalComponents.INVENTORY_COMPONENT, null);
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_INPUT, dir)) {
                    inventory.pull(inv, ActionType.PERFORM, dir);
                }
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_OUTPUT, dir)) {
                    inventory.push(inv, ActionType.PERFORM, dir);
                }
            } else if (world.getBlockEntity(targetPos) instanceof Inventory) {
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_INPUT, dir)) {
                    Helper.pull((MachineDataProviderComponent) data, inventory, (Inventory) world.getBlockEntity(targetPos), 1, dir);
                }
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_OUTPUT, dir)) {
                    Helper.push((MachineDataProviderComponent) data, inventory, (Inventory) world.getBlockEntity(targetPos), 1, dir);
                }
            }
            //TODO Fluid
            if (targetBlock instanceof Block) {
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.FLUID, ConfigBehavior.SELF_INPUT, dir)) {
                    //TODO
                }
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.FLUID, ConfigBehavior.SELF_OUTPUT, dir)) {
                    //TODO
                }
            }
            if (targetBlock instanceof MachineBlock) {
                MachineCapacitorComponent cap = (MachineCapacitorComponent) ((MachineBlock) targetBlock).getComponent(world, targetPos, UniversalComponents.CAPACITOR_COMPONENT, null);
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.POWER, ConfigBehavior.SELF_INPUT, dir)) {
                    capacitor.pull(cap, ActionType.PERFORM, dir);
                }
                if (((MachineDataProviderComponent) data).getConfig(ConfigType.POWER, ConfigBehavior.SELF_OUTPUT, dir)) {
                    capacitor.push(cap, ActionType.PERFORM, dir);
                }
            }
        }
    }

    public boolean checkForRecipe() {
        Optional<MachineRecipe> optional = world.getRecipeManager().getFirstMatch(BlockRegistry.getEntry(BlockEntityType.getId(getType())).recipeType, new InventoryWrapper(pos), world);
        ((MachineDataProviderComponent) this.data).setRecipe(optional.orElse(null));
        return optional.isPresent();
    }

    public void start() {
        
    }

    public void process() {
        //only for testing TODO: remove
        if (inventory.getItemStack("power_input").getItem() == Items.BEDROCK && capacitor.getCurrentEnergy() < capacitor.getMaxEnergy()) {
            capacitor.generateEnergy(world, pos, 4);
        }
    }

    public void task() {

    }

    public void finish() {
        cancle();
    }

    public void cancle() {
        MachineDataProviderComponent data = (MachineDataProviderComponent) this.data;
        data.resetProgress();
        data.resetRecipe();
    }

    public void idle() {

    }

    public void correct() {

    }

    public boolean containsItemIngredients(Input.ItemIngredient... ingredients) {
        boolean bl = true;
        for (Input.ItemIngredient ingredient : ingredients) {
            if (!inventory.containsInput(ingredient)) {
                bl = false;
            }
        }
        return bl;
    }

    public boolean containsFluidIngredients(Input.FluidIngredient... ingredients) {
        boolean bl = true;
        for (Input.FluidIngredient ingredient : ingredients) {
            TechMod.LOGGER.wip("Containment Check for " + ingredient);
            //TODO
        }
        return bl;
    }

    //only by overriding
    public boolean containsBlockIngredients(Input.BlockIngredient... ingredients) {
        return true;
    }

    //only by overriding
    public boolean containsEntityIngredients(Input.EntityIngredient... ingredients) {
        return true;
    }

    //only by overriding
    public boolean containsDataIngredients(Input.DataIngredient... ingredients) {
        return true;
    }

    public boolean isActivated() {
        MachineDataProviderComponent.ActivationState activationState = ((MachineDataProviderComponent) data).getActivationState();
        if (activationState == MachineDataProviderComponent.ActivationState.ALWAYS_ON) {
            return true;
        } else if(activationState == MachineDataProviderComponent.ActivationState.REDSTONE_ON) {
            return world.isReceivingRedstonePower(pos);
        } else if(activationState == MachineDataProviderComponent.ActivationState.REDSTONE_OFF) {
            return !world.isReceivingRedstonePower(pos);
        } else {
            return false;
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (world != null) {
            capacitor.fromTag(tag);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (world != null) {
            capacitor.toTag(tag);
        }
        return tag;
    }
    
    protected MachineCapacitorComponent initializeCapacitorComponent() {
        return new MachineCapacitorComponent(EnergyTypes.ULTRA_LOW_VOLTAGE);
    }

    @Override
    protected MachineDataProviderComponent initializeDataProviderComponent() {
        return (MachineDataProviderComponent) data;
    }
}