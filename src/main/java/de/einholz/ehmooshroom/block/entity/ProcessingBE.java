package de.einholz.ehmooshroom.block.entity;

import java.util.Optional;
import de.einholz.ehmooshroom.recipe.AdvancedRecipe;
import de.einholz.ehmooshroom.recipe.Ingredient;
import de.einholz.ehmooshroom.recipe.PosAsInv;
import de.einholz.ehmooshroom.storage.SidedStorageManager.SideConfigType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProcessingBE extends ContainerBE {
    private final RecipeType<? extends Recipe<?>> recipeType;
    private AdvancedRecipe recipe;
    private boolean isProcessing = false;
    private ActivationState activationState = ActivationState.REDSTONE_OFF;
    private double progressMin = 0.0;
    private double progress = 0.0;
    private double progressMax = 1000.0;

    public ProcessingBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        recipeType = null;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        isProcessing = progress > progressMin && isActivated();
        //powerBalance = getMachineCapacitorComp().getCurrentEnergy() - lastPower;
        //lastPower = getMachineCapacitorComp().getCurrentEnergy();
        boolean dirty = transfer();
        if (!isProcessing && isActivated()) isProcessing = checkForRecipe();
        if (isProcessing) {
            if (progress == progressMin) start();
            if (process()) task();
            if (progress == progressMax) complete();
        } else idle();
        correct();
        markDirty();
    }

    @SuppressWarnings("unchecked")
    public boolean checkForRecipe() {
        Optional<AdvancedRecipe> optional = world.getRecipeManager().getFirstMatch((RecipeType<AdvancedRecipe>) recipeType, new PosAsInv(pos), world);
        recipe = optional.orElse(null);
        return optional.isPresent();
    }

    public void start() {
        for (Ingredient<?> ingredient : recipe.input) {
            if (ingredient.getAmount() == 0) continue;
            getStorageMgr().getStorageEntries(ingredient.getType(), SideConfigType.getFromParams(false, false, null));
        }

        /*
        // OLD:
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
        */
    }

    public boolean process() {
        /*
        boolean doConsum = recipe.consumes != Double.NaN && recipe.consumes > 0.0;
        boolean canConsum = true;
        int consum = 0;
        boolean doGenerate = recipe.generates != Double.NaN && recipe.generates > 0.0;
        boolean canGenerate = true;
        int generate = 0;
        */
        boolean canProcess = true;
        /*
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
        */
        return canProcess;
    }

    public void task() {}

    public void complete() {
        cancel();
    }

    public void cancel() {
        progress = progressMin;
        isProcessing = false;
        recipe = null;
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
