package de.einholz.ehmooshroom.block.entity;

import java.util.Iterator;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.recipe.AdvRecipe;
import de.einholz.ehmooshroom.recipe.Exgredient;
import de.einholz.ehmooshroom.recipe.Gredient;
import de.einholz.ehmooshroom.recipe.Ingredient;
import de.einholz.ehmooshroom.recipe.PosAsInv;
import de.einholz.ehmooshroom.recipe.RecipeHolder;
import de.einholz.ehmooshroom.registry.RecipeTypeRegistry;
import de.einholz.ehmooshroom.registry.ScreenHandlerRegistry;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.StorageEntry;
import de.einholz.ehmooshroom.storage.Transferable;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType.ExtendedFactory;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.util.NbtType;
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
            ExtendedFactory<? extends ScreenHandler> clientHandlerFactory) {
        super(type, pos, state, clientHandlerFactory);
    }

    @Override
    protected void tick(World world, BlockPos pos, BlockState state) {
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
        updateBalances();
    }

    protected boolean checkForRecipe() {
        Optional<AdvRecipe> optional = world.getRecipeManager().getFirstMatch(getRecipeType(), new PosAsInv(pos),
                world);
        recipe = optional.orElse(null);
        return optional.isPresent();
    }

    @SuppressWarnings("null")
    protected void start() {
        try (Transaction trans = Transaction.openOuter()) {
            for (int i = 0; i < getRecipe().input.length; i++)
                if (!consumeOrGenerate(trans, getRecipe().input[i])) {
                    trans.abort();
                    break;
                }
            if (Transaction.isOpen())
                trans.commit();
            else
                cancel();
        }
    }

    @SuppressWarnings("null")
    protected boolean process() {
        boolean canProcess = isProcessing;
        if (canProcess)
            setProgress(getProgress() + getRecipe().timeModifier * getSpeed());
        return canProcess;
    }

    protected void task() {
    }

    @SuppressWarnings("null")
    protected void complete() {
        // TODO add proper overflow protection
        try (Transaction trans = Transaction.openOuter()) {
            for (int i = 0; i < getRecipe().output.length; i++)
                if (!consumeOrGenerate(trans, getRecipe().output[i])) {
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

    @SuppressWarnings("unchecked")
    protected <T, V extends TransferVariant<T>> boolean consumeOrGenerate(Transaction trans, Gredient<T> gredient) {
        boolean generate = gredient instanceof Exgredient<?, ?>;
        if (gredient.getAmount() <= 0)
            return true;
        long remaining = gredient.getAmount();
        SideConfigType sct = generate ? SideConfigType.OUT_PROC : SideConfigType.IN_PROC;
        var entries = getStorageMgr().<T, V>getStorageEntries((Transferable<T, V>) gredient.getType(), sct);
        for (StorageEntry<T, V> entry : entries) {
            if (!gredient.getType().equals(entry.trans))
                continue;
            Iterator<? extends StorageView<V>> iter = entry.storage.iterator(trans);
            while (iter.hasNext()) {
                StorageView<V> view = iter.next();
                if (!gredient.matches(view.getResource()))
                    continue;
                if (generate)
                    remaining -= entry.storage.insert(((Exgredient<T, V>) gredient).getOutputVariant(), remaining,
                            trans);
                else
                    remaining -= entry.storage.extract(view.getResource(), remaining, trans);
                if (remaining == 0)
                    break;
            }
            if (remaining > 0)
                return false;
            else
                // FIXME necessary? probably yes
                setDirty();
        }
        return true;
    }

    protected void cancel() {
        setProgress(PROGRESS_MIN);
        isProcessing = false;
        recipe = null;
    }

    protected void idle() {
    }

    protected void operate() {
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
                    Iterator<? extends StorageView<TransferVariant<Object>>> iter = entry.storage.iterator(trans);
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

    @SuppressWarnings("unchecked")
    public RecipeType<AdvRecipe> getRecipeType() {
        MooshroomLib.LOGGER.warnBug(getDisplayName().getString(), "should have its own RecipeType");
        return (RecipeType<AdvRecipe>) RecipeTypeRegistry.DUMMY_RECIPE_TYPE;
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

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        String recipeStr = "";
        if (getRecipe() != null)
            recipeStr = getRecipe().getId().toString();
        else if (recipeId != null)
            recipeStr = recipeId.toString();
        nbt.putString("Recipe", recipeStr);
        nbt.putString("ActivationState", getActivationState().name());
        nbt.putDouble("Progress", getProgress());
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

        @SuppressWarnings("unchecked")
        @Override
        public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            writeScreenOpeningData((ServerPlayerEntity) player, buf);
            return ((ExtendedScreenHandlerType<SideConfigGui>) ScreenHandlerRegistry.SIDE_CONFIG).create(syncId, inv,
                    buf);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
            buf.writeBlockPos(pos);
        }
    }
}
