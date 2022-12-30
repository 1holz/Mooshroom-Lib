package de.einholz.ehmooshroom.block.entity;

import java.util.Optional;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.SidedStorageManager.SideConfigType;
import de.einholz.ehmooshroom.storage.SidedStorageManager.StorageEntry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ProcessingBE extends ContainerBE {
    private boolean isProcessing = false;
    public ActivationState activationState = ActivationState.REDSTONE_OFF;
    private double progressMin = 0.0;
    private double progress = 0.0;
    private double progressMax = 1000.0;

    public ProcessingBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        isProcessing = progress > progressMin && isActivated();
        //powerBalance = getMachineCapacitorComp().getCurrentEnergy() - lastPower;
        //lastPower = getMachineCapacitorComp().getCurrentEnergy();
        transfer();
        if (!isProcessing && isActivated()) isProcessing = checkForRecipe();
        if (isProcessing) {
            if (progress == progressMin) start();
            if (process()) task();
            if (progress == progressMax) complete();
        } else idle();
        correct();
        markDirty();
    }

    public void transfer() {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Direction targetDir = dir.getOpposite();
            // self output
            for (StorageEntry<?> entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, true, dir))) {
                try (Transaction trans = Transaction.openOuter()) {
                    if (entry.lookup == null) continue;
                    Storage<?> targetStorage = entry.lookup.find(world, targetPos, targetDir);

                }
            }
            // self input
            for (StorageEntry<?> entry : getStorageMgr().getStorageEntries(null, SideConfigType.getFromParams(false, false, dir))) {
                try (Transaction trans = Transaction.openOuter()) {
                    if (entry.lookup == null) continue;
                    Storage<?> targetStorage = entry.lookup.find(world, targetPos, targetDir);

                }
            }
//                @SuppressWarnings("unchecked")
//                TransportingComponent<Component> comp = (TransportingComponent<Component>) entry.getValue();
//                BlockComponentHook hook = BlockComponentHook.INSTANCE;
//                if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_INPUT, dir)) {
//                    if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.pull(hook.getInvComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.pull(hook.getTankComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.pull(hook.getCapComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                }
//                if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_OUTPUT, dir)) {
//                    if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.push(hook.getTankComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.push(hook.getCapComponent(world, targetPos, targetDir), dir, Action.PERFORM);
//                }
//            }
        }
        //TODO: only for early development replace with proper creative battery
        if (getMachineInvComp().getStack(getMachineInvComp().getIntFromId(MooshroomLib.HELPER.makeId("power_input"))).getItem().equals(Items.BEDROCK) && getMachineCapacitorComp().getCurrentEnergy() < getMachineCapacitorComp().getMaxEnergy()) getMachineCapacitorComp().generateEnergy(world, pos, getMachineCapacitorComp().getPreferredType().getMaximumTransferSize());
    }

    @SuppressWarnings("unchecked")
    public boolean checkForRecipe() {
        Optional<AdvancedRecipe> optional = world.getRecipeManager().getFirstMatch((RecipeType<AdvancedRecipe>) recipeType, new InventoryWrapperPos(pos), world);
        getMachineDataComp().setRecipe(optional.orElse(null));
        return optional.isPresent();
    }

    public void start() {
        AdvancedRecipe recipe = (AdvancedRecipe) getMachineDataComp().getRecipe(world);
        boolean consumerRecipe = (recipe.consumes == Double.NaN ? 0.0 : recipe.consumes) > (recipe.generates == Double.NaN ? 0.0 : recipe.generates);
        int consum = (int) (getMachineDataComp().getEfficiency() * getMachineDataComp().getSpeed() * recipe.consumes);
        if ((consumerRecipe && getMachineCapacitorComp().extractEnergy(getMachineCapacitorComp().getPreferredType(), consum, ActionType.TEST) == consum) || !consumerRecipe) {
            for (ItemIngredient ingredient : recipe.input.items) {
                int consumingLeft = ingredient.amount;
                for (Slot slot : getSlots(Type.INPUT)) {
                    if (ingredient.ingredient.contains(slot.stack.getItem()) && NbtHelper.matches(ingredient.tag, slot.stack.getTag(), true)) {
                        if (slot.stack.getCount() >= consumingLeft) {
                            slot.stack.decrement(consumingLeft);
                            break;
                        } else {
                            consumingLeft -= slot.stack.getCount();
                            slot.stack.setCount(0);;
                        }
                    }
                }
            }
            //TODO: Fluids
        }
    }

    public boolean process() {
        AdvancedRecipe recipe = (AdvancedRecipe) getMachineDataComp().getRecipe(world);
        boolean doConsum = recipe.consumes != Double.NaN && recipe.consumes > 0.0;
        boolean canConsum = true;
        int consum = 0;
        boolean doGenerate = recipe.generates != Double.NaN && recipe.generates > 0.0;
        boolean canGenerate = true;
        int generate = 0;
        boolean canProcess = true;
        if (doConsum) {
            consum = (int) (getMachineDataComp().getEfficiency() * getMachineDataComp().getSpeed() * recipe.consumes);
            if (getMachineCapacitorComp().extractEnergy(getMachineCapacitorComp().getPreferredType(), consum, ActionType.TEST) < consum) canConsum = false;
        }
        if (doGenerate) {
            generate = (int) (getMachineDataComp().getEfficiency() * getMachineDataComp().getSpeed() * recipe.generates);
            if (getMachineCapacitorComp().getCurrentEnergy() + generate > getMachineCapacitorComp().getMaxEnergy()) canGenerate = false;
        }
        if (doConsum) {
            if (canConsum && canGenerate) getMachineCapacitorComp().extractEnergy(getMachineCapacitorComp().getPreferredType(), consum, ActionType.PERFORM);
            else canProcess = false;
        }
        if (doGenerate) {
            if (canConsum && canGenerate) getMachineCapacitorComp().generateEnergy(world, pos, generate);
            else canProcess = false;
        }
        if (canProcess) getMachineDataComp().addProgress(recipe.timeModifier * getMachineDataComp().getSpeed());
        return canProcess;
    }

    public void task() {}

    public void complete() {
        cancel();
    }

    public void cancel() {
        progress = progressMin;
        isProcessing = false;
        getMachineDataComp().resetRecipe();
    }

    public void idle() {}

    public void correct() {}

    public boolean isActivated() {
        if (activationState == ActivationState.ALWAYS_ON) return true;
        else if(activationState == ActivationState.REDSTONE_ON) return world.isReceivingRedstonePower(pos);
        else if(activationState == ActivationState.REDSTONE_OFF) return !world.isReceivingRedstonePower(pos);
        else return false;
    }

    public static enum ActivationState {
        ALWAYS_ON,
        REDSTONE_ON,
        REDSTONE_OFF,
        ALWAYS_OFF;
    }
}
