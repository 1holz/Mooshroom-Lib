package de.einholz.ehmooshroom.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import de.einholz.ehmooshroom.recipe.AdvRecipe;
import de.einholz.ehmooshroom.recipe.Exgredient;
import de.einholz.ehmooshroom.recipe.Ingredient;
import de.einholz.ehmooshroom.recipe.PosAsInv;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.SideConfigType;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.StorageEntry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProcessingBE extends ContainerBE {
    private final RecipeType<? extends Recipe<?>> recipeType;
    private AdvRecipe recipe;
    private boolean isProcessing = false;
    private ActivationState activationState = ActivationState.REDSTONE_OFF;
    public final static double PROGRESS_MIN = 0.0;
    private double progress = 0.0;
    public final static double PROGRESS_MAX = 1000.0;
    private double speed = 1;

    public ProcessingBE(BlockEntityType<?> type, BlockPos pos, BlockState state, ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state, clientHandlerFactory);
        recipeType = null;
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        resetDitry();
        isProcessing = progress > PROGRESS_MIN && isActivated();
        //powerBalance = getMachineCapacitorComp().getCurrentEnergy() - lastPower;
        //lastPower = getMachineCapacitorComp().getCurrentEnergy();
        transfer();
        if (!isProcessing && isActivated()) isProcessing = checkForRecipe();
        if (isProcessing) {
            if (progress == PROGRESS_MIN) start();
            if (process()) task();
            if (progress == PROGRESS_MAX) complete();
        } else idle();
        correct();
        if (isDirty()) markDirty();
    }

    @SuppressWarnings("unchecked")
    public boolean checkForRecipe() {
        Optional<AdvRecipe> optional = world.getRecipeManager().getFirstMatch((RecipeType<AdvRecipe>) recipeType, new PosAsInv(pos), world);
        recipe = optional.orElse(null);
        return optional.isPresent();
    }

    public void start() {
        Transaction trans = Transaction.openOuter();
        for (int i = 0; i < recipe.input.length; i++) if (!consume(trans, i)) {
            trans.abort();
            break;
        }
        if (Transaction.isOpen()) {
            trans.commit();
            setDirty();
        } else cancel();

        /*
        for (Ingredient<?> ingredient : recipe.input) {
            if (ingredient.getAmount() == 0) continue;
            long amount = ingredient.getAmount();
            List<?> entries = getStorageMgr().getStorageEntries(ingredient.getType(), SideConfigType.IN_IN);
            for (StorageEntry<Object> entry : (List<StorageEntry<Object>>) entries) {
                if (!ingredient.getType().isAssignableFrom(entry.clazz)) continue;
                Iterator<StorageView<Object>> iter = entry.storage.iterator(trans);
                while (iter.hasNext()) {
                    StorageView<Object> view = iter.next();
                    if (ingredient.matches(view.getResource(), new NbtCompound())) continue;
                }
            }
        }
        */

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

    @SuppressWarnings("unchecked")
    protected <T> boolean consume(Transaction trans, int i) {
        Ingredient<T> ingredient = (Ingredient<T>) recipe.input[i];
        if (ingredient.getAmount() == 0) return true;
        long amount = ingredient.getAmount();
        List<?> entries = getStorageMgr().getStorageEntries(ingredient.getType(), SideConfigType.IN_PROC);
        for (StorageEntry<T> entry : (List<StorageEntry<T>>) entries) {
            if (!ingredient.getType().equals(entry.trans)) continue;
            Iterator<StorageView<T>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<T> view = iter.next();
                if (!ingredient.matches(view.getResource(), new NbtCompound())) continue;
                amount -= entry.storage.extract(view.getResource(), amount, trans);
                if (amount == 0) break;
            }
            if (amount > 0) return false;
            else setDirty();
        }
        return true;
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
        boolean canProcess = isProcessing;
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
        */
        if (canProcess) progress = recipe.timeModifier * getSpeed();
        return canProcess;
    }

    public void task() {}

    public void complete() {
        Transaction trans = Transaction.openOuter();
        for (int i = 0; i < recipe.output.length; i++) if (!generate(trans, i)) {
            trans.abort();
            break;
        }
        if (Transaction.isOpen()) {
            trans.commit();
            setDirty();
        } else cancel();

        /*
        for (Exgredient<?> exgredient : recipe.output) {
            List<?> entries = getStorageMgr().getStorageEntries(exgredient.getClass(), SideConfigType.OUT_IN);
            for (StorageEntry<?> object : (List<StorageEntry<?>>) entries) {
                
            }
        }
        cancel();
        */
    }

    // combine with consume?
    @SuppressWarnings("unchecked")
    protected <T> boolean generate(Transaction trans, int i) {
        Exgredient<T> exgredient = (Exgredient<T>) recipe.output[i];
        if (exgredient.getAmount() == 0) return true;
        long amount = exgredient.getAmount();
        List<?> entries = getStorageMgr().getStorageEntries(exgredient.getType(), SideConfigType.OUT_PROC);
        for (StorageEntry<T> entry : (List<StorageEntry<T>>) entries) {
            if (!exgredient.getType().equals(entry.trans)) continue;
            Iterator<StorageView<T>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<T> view = iter.next();
                //if (!exgredient.matches(view.getResource(), new NbtCompound())) continue;
                amount -= entry.storage.insert(view.getResource(), amount, trans);
                if (amount == 0) break;
            }
            if (amount > 0) return false;
            else setDirty();
        }
        return true;
    }

    public void cancel() {
        progress = PROGRESS_MIN;
        isProcessing = false;
        recipe = null;
    }

    public void idle() {}

    public void correct() {}

    public double getProgress() {
        return progress;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public ActivationState getActivationState() {
        return activationState;
    }

    public void nextActivationState() {
        ActivationState[] values = ActivationState.values();
        activationState = values[getActivationState().ordinal() % values.length];
    }

    public boolean isActivated() {
        switch (getActivationState()) {
            case ON:
                return true;
            case REDSTONE_ON:
                return world.isReceivingRedstonePower(pos);
            case REDSTONE_OFF:
                return !world.isReceivingRedstonePower(pos);
            case OFF:
                return false;
        }
        return false;
    }

    public static enum ActivationState {
        ON,
        REDSTONE_ON,
        REDSTONE_OFF,
        OFF;
    }
}
