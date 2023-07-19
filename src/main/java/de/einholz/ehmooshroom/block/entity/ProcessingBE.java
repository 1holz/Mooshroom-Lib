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
import de.einholz.ehmooshroom.recipe.RecipeHolder;
import de.einholz.ehmooshroom.registry.Reg;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.StorageEntry;
import de.einholz.ehmooshroom.storage.Transferable;
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

public class ProcessingBE extends ContainerBE implements RecipeHolder {
    private Identifier recipeId;
    @Nullable
    private AdvRecipe recipe;
    private boolean isProcessing = false;
    private ActivationState activationState = ActivationState.REDSTONE_OFF;
    public static final double PROGRESS_MIN = 0.0;
    private double progress = 0.0;
    public static final double PROGRESS_MAX = 1000.0;
    private double speed = 1;
    private double efficiency = 1;

    public ProcessingBE(BlockEntityType<?> type, BlockPos pos, BlockState state,
            ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state, clientHandlerFactory);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state) {
        resetDitry();
        if (recipe == null && recipeId != null) {
            recipe = (AdvRecipe) world.getRecipeManager().get(recipeId).orElse(null);
            recipeId = null;
        }
        isProcessing = progress > PROGRESS_MIN && isActivated();
        // powerBalance = getMachineCapacitorComp().getCurrentEnergy() - lastPower;
        // lastPower = getMachineCapacitorComp().getCurrentEnergy();
        transfer();
        if (!isProcessing && isActivated())
            isProcessing = checkForRecipe();
        if (isProcessing) {
            if (progress <= PROGRESS_MIN)
                start();
            if (process())
                task();
            if (progress >= PROGRESS_MAX)
                complete();
        } else
            idle();
        operate();
        if (isDirty())
            markDirty();
    }

    public boolean checkForRecipe() {
        Optional<AdvRecipe> optional = world.getRecipeManager().getFirstMatch(getRecipeType(), new PosAsInv(pos),
                world);
        recipe = optional.orElse(null);
        return optional.isPresent();
    }

    @SuppressWarnings("null")
    public void start() {
        try (Transaction trans = Transaction.openOuter()) {
            for (int i = 0; i < getRecipe().input.length; i++)
                if (!consume(trans, i)) {
                    trans.abort();
                    break;
                }
            if (Transaction.isOpen())
                trans.commit();
            else
                cancel();
        }
    }

    @SuppressWarnings({ "null", "unchecked" })
    protected <T, V extends TransferVariant<T>> boolean consume(Transaction trans, int i) {
        Ingredient<T> ingredient = (Ingredient<T>) getRecipe().input[i];
        if (ingredient.getAmount() == 0)
            return true;
        long remaining = ingredient.getAmount();
        List<StorageEntry<T, V>> entries = getStorageMgr()
                .<T, V>getStorageEntries((Transferable<T, V>) ingredient.getType(), SideConfigType.IN_PROC);
        for (StorageEntry<T, V> entry : entries) {
            if (!ingredient.getType().equals(entry.trans))
                continue;
            Iterator<StorageView<V>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<V> view = iter.next();
                if (!ingredient.matches(view.getResource()))
                    continue;
                remaining -= entry.storage.extract(view.getResource(), remaining, trans);
                if (remaining == 0)
                    break;
            }
            if (remaining > 0)
                return false;
            else
                setDirty();
        }
        return true;
    }

    @SuppressWarnings("null")
    public boolean process() {
        boolean canProcess = isProcessing;
        if (canProcess)
            setProgress(getProgress() + getRecipe().timeModifier * getSpeed());
        return canProcess;
    }

    public void task() {
    }

    @SuppressWarnings("null")
    public void complete() {
        // TODO add proper overflow protection
        try (Transaction trans = Transaction.openOuter()) {
            for (int i = 0; i < getRecipe().output.length; i++)
                if (!generate(trans, i)) {
                    trans.abort();
                    break;
                }
            if (Transaction.isOpen()) {
                trans.commit();
                setDirty();
            }
        }
        cancel();
    }

    // TODO combine with consume?
    @SuppressWarnings({ "null", "unchecked" })
    protected <T, V extends TransferVariant<T>> boolean generate(Transaction trans, int i) {
        Exgredient<T, V> exgredient = (Exgredient<T, V>) getRecipe().output[i];
        if (exgredient.getAmount() == 0)
            return true;
        long remaining = exgredient.getAmount();
        List<StorageEntry<T, V>> entries = getStorageMgr()
                .<T, V>getStorageEntries((Transferable<T, V>) exgredient.getType(), SideConfigType.OUT_PROC);
        for (StorageEntry<T, V> entry : entries) {
            if (!exgredient.getType().equals(entry.trans))
                continue;
            Iterator<StorageView<V>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<V> view = iter.next();
                if (!exgredient.matches(view.getResource()))
                    continue;
                remaining -= entry.storage.insert((V) exgredient.getOutputVariant(), remaining, trans);
                if (remaining == 0)
                    break;
            }
            if (remaining > 0)
                return false;
            else
                setDirty();
        }
        return true;
    }

    public void cancel() {
        setProgress(PROGRESS_MIN);
        isProcessing = false;
        recipe = null;
    }

    public void idle() {
    }

    public void operate() {
    }

    // FIXME make less nested
    @Override
    public boolean containsIngredients(Ingredient<?>... ingredients) {
        try (Transaction trans = Transaction.openOuter()) {
            for (Ingredient<?> ingredient : ingredients) {
                long remaining = ingredient.getAmount();
                for (StorageEntry<Object, TransferVariant<Object>> entry : getStorageMgr().getStorageEntries(null,
                        SideConfigType.IN_PROC)) {
                    if (!ingredient.getType().equals(entry.trans))
                        continue;
                    Iterator<StorageView<TransferVariant<Object>>> iter = entry.storage.iterator(trans);
                    while (iter.hasNext()) {
                        StorageView<TransferVariant<Object>> view = iter.next();
                        if (view.isResourceBlank())
                            continue;
                        TransferVariant<Object> variant = view.getResource();
                        if (!ingredient.matches(variant))
                            continue;
                        remaining -= view.extract(variant, remaining, trans);
                        if (remaining <= 0)
                            break;
                    }
                    // remaining -= entry.storage.simulateExtract(null, remaining, trans);
                    if (remaining <= 0)
                        break;
                }
                if (remaining > 0)
                    return false;
            }
        }
        return true;
    }

    public RecipeType<AdvRecipe> getRecipeType() {
        MooshroomLib.LOGGER.warnBug(getDisplayName().getString(), "should have its own RecipeType");
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

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public ActivationState getActivationState() {
        return activationState;
    }

    public void nextActivationState() {
        ActivationState[] values = ActivationState.values();
        activationState = values[(getActivationState().ordinal() + 1) % values.length];
        markDirty();
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
        String recipeStr = "";
        if (getRecipe() != null)
            recipeStr = getRecipe().getId().toString();
        else if (recipeId != null)
            recipeStr = recipeId.toString();
        nbt.putString("Recipe", recipeStr);
        nbt.putString("ActivationState", getActivationState().name());
        nbt.putDouble("Progress", getProgress());
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Recipe", NbtType.STRING)) {
            String str = nbt.getString("Recipe");
            recipeId = null;
            if (str.isBlank())
                recipe = null;
            else if (world == null)
                recipeId = new Identifier(str);
            else
                recipe = (AdvRecipe) world.getRecipeManager().get(new Identifier(str)).orElse(null);
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
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(pos);
        }
    }
}
