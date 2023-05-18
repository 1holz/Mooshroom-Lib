package de.einholz.ehmooshroom.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.recipe.AdvRecipe;
import de.einholz.ehmooshroom.recipe.Exgredient;
import de.einholz.ehmooshroom.recipe.Ingredient;
import de.einholz.ehmooshroom.recipe.PosAsInv;
import de.einholz.ehmooshroom.registry.Reg;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.StorageEntry;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProcessingBE extends ContainerBE {
    @Nullable
    private AdvRecipe recipe;
    private boolean isProcessing = false;
    private ActivationState activationState = ActivationState.REDSTONE_OFF;
    public final static double PROGRESS_MIN = 0.0;
    private double progress = 0.0;
    public final static double PROGRESS_MAX = 1000.0;
    private double speed = 1;

    public ProcessingBE(BlockEntityType<?> type, BlockPos pos, BlockState state, ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state, clientHandlerFactory);
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

    public boolean checkForRecipe() {
        Optional<AdvRecipe> optional = world.getRecipeManager().getFirstMatch(getRecipeType(), new PosAsInv(pos), world);
        recipe = optional.orElse(null);
        return optional.isPresent();
    }

    @SuppressWarnings("null")
    public void start() {
        Transaction trans = Transaction.openOuter();
        for (int i = 0; i < getRecipe().input.length; i++) if (!consume(trans, i)) {
            trans.abort();
            break;
        }
        if (Transaction.isOpen()) {
            trans.commit();
            setDirty();
        } else cancel();

        /*
        for (Ingredient<?> ingredient : getRecipe().input) {
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
        boolean consumerRecipe = (getRecipe().consumes == Double.NaN ? 0.0 : getRecipe().consumes) > (getRecipe().generates == Double.NaN ? 0.0 : getRecipe().generates);
        int consum = (int) (getMachineDataComp().getEfficiency() * getMachineDataComp().getSpeed() * getRecipe().consumes);
        if ((consumerRecipe && getMachineCapacitorComp().extractEnergy(getMachineCapacitorComp().getPreferredType(), consum, ActionType.TEST) == consum) || !consumerRecipe) {
            for (ItemIngredient ingredient : getRecipe().input.items) {
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

    @SuppressWarnings({"null", "unchecked"})
    protected <T, V extends TransferVariant<T>> boolean consume(Transaction trans, int i) {
        Ingredient<T> ingredient = (Ingredient<T>) getRecipe().input[i];
        if (ingredient.getAmount() == 0) return true;
        long amount = ingredient.getAmount();
        List<StorageEntry<T, V>> entries = getStorageMgr().<T, V>getStorageEntries((Transferable<T, V>) ingredient.getType(), SideConfigType.IN_PROC);
        for (StorageEntry<T, V> entry : entries) {
            if (!ingredient.getType().equals(entry.trans)) continue;
            Iterator<StorageView<V>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<V> view = iter.next();
                if (!ingredient.matches(view.getResource(), ingredient.getNbt())) continue;
                amount -= entry.storage.extract(view.getResource(), amount, trans);
                if (amount == 0) break;
            }
            if (amount > 0) return false;
            else setDirty();
        }
        return true;
    }

    @SuppressWarnings("null")
    public boolean process() {
        /*
        boolean doConsum = getRecipe().consumes != Double.NaN && getRecipe().consumes > 0.0;
        boolean canConsum = true;
        int consum = 0;
        boolean doGenerate = getRecipe().generates != Double.NaN && getRecipe().generates > 0.0;
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
        if (canProcess) progress = getRecipe().timeModifier * getSpeed();
        return canProcess;
    }

    public void task() {}

    @SuppressWarnings("null")
    public void complete() {
        Transaction trans = Transaction.openOuter();
        for (int i = 0; i < getRecipe().output.length; i++) if (!generate(trans, i)) {
            trans.abort();
            break;
        }
        if (Transaction.isOpen()) {
            trans.commit();
            setDirty();
        }
        cancel();

        /*
        for (Exgredient<?> exgredient : getRecipe().output) {
            List<?> entries = getStorageMgr().getStorageEntries(exgredient.getClass(), SideConfigType.OUT_IN);
            for (StorageEntry<?> object : (List<StorageEntry<?>>) entries) {
                
            }
        }
        cancel();
        */
    }

    // TODO combine with consume?
    @SuppressWarnings({"null", "unchecked"})
    protected <T, V extends TransferVariant<T>> boolean generate(Transaction trans, int i) {
        Exgredient<T> exgredient = (Exgredient<T>) getRecipe().output[i];
        if (exgredient.getAmount() == 0) return true;
        long amount = exgredient.getAmount();
        List<StorageEntry<T, V>> entries = getStorageMgr().<T, V>getStorageEntries((Transferable<T, V>) exgredient.getType(), SideConfigType.OUT_PROC);
        for (StorageEntry<T, V> entry : entries) {
            if (!exgredient.getType().equals(entry.trans)) continue;
            Iterator<StorageView<V>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<V> view = iter.next();
                if (!exgredient.matches(view.getResource(), exgredient.getNbt())) continue;
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

    public RecipeType<AdvRecipe> getRecipeType() {
        MooshroomLib.LOGGER.smallBug(new IllegalStateException(getDisplayName() + " should have its own RecipeType"));
        return Reg.DUMMY_RECIPE_TYPE.RECIPE_TYPE;
    }

    @Nullable
    public AdvRecipe getRecipe() {
        return recipe;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
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
        activationState = values[(getActivationState().ordinal() + 1) % values.length];
        setDirty();
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

    @SuppressWarnings("null")
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("Recipe", getRecipe() == null ? "" : getRecipe().getId().toString());
        nbt.putString("ActivationState", getActivationState().name());
        nbt.putDouble("Progress", getProgress());
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Recipe", NbtType.STRING)) {
            String str = nbt.getString("Recipe");
            if (str.isBlank()) recipe = null;
            else recipe = (AdvRecipe) world.getRecipeManager().get(new Identifier(str)).orElse(null);
        }
        if (nbt.contains("ActivationState", NbtType.STRING))
            activationState = ActivationState.valueOf(nbt.getString("ActivationState"));
        if (nbt.contains("Progress", NbtType.NUMBER))
            setProgress(nbt.getDouble("Progress"));
    }

    public static enum ActivationState {
        ON,
        REDSTONE_ON,
        REDSTONE_OFF,
        OFF;
    }
    
    public class SideConfigScreenHandlerFactory implements ExtendedScreenHandlerFactory {
        @Override
        public Text getDisplayName() {
            return ProcessingBE.this.getDisplayName();
        }

        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            writeScreenOpeningData((ServerPlayerEntity) player, buf);
            return ((ExtendedScreenHandlerType<SideConfigGui>) Reg.SIDE_CONFIG.GUI).create(syncId, inv, buf);
            //return RegistryHelper.getEntry(MooshroomLib.HELPER.makeId("side_config")).clientHandlerFactory.create(syncId, inv, buf);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(pos);
        }
    }
}
