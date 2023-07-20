package de.einholz.ehmooshroom.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.storage.variants.BlockVariant;
import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
import de.einholz.ehmooshroom.storage.variants.EntityVariant;
import de.einholz.ehmooshroom.storage.variants.HeatVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// XXX: anotate constructor with only server?
public class Exgredient<T, V extends TransferVariant<T>> implements Gredient<T> {
    public static final Map<Transferable<?, ? extends TransferVariant<?>>, Factories<?, ?>> FACTORIES = new HashMap<>();
    private final Transferable<T, V> type;
    @Nullable
    private final T output;
    @Nullable
    private final V outputVariant;
    @Nullable
    private final Identifier id;
    @Nullable
    private final NbtCompound nbt;
    private final long amount;

    @SuppressWarnings("unchecked")
    public Exgredient(Identifier type, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount) {
        this.type = TransferablesReg.TRANSFERABLE.get(type);
        this.id = id;
        this.nbt = nbt == null ? new NbtCompound() : nbt;
        this.amount = amount;
        output = (T) FACTORIES.get(this.type).outputFactory().build(id, amount, nbt);
        outputVariant = ((Function<T, V>) FACTORIES.get(this.type).variantFactory()).apply(output);
    }

    public Exgredient(Identifier type, @Nullable Identifier id, long amount) {
        this(type, id, null, amount);
    }

    public Exgredient(Identifier type, @Nullable NbtCompound nbt, long amount) {
        this(type, null, nbt, amount);
    }

    public Exgredient(Identifier type, long amount) {
        this(type, null, null, amount);
    }

    public T getOutput() {
        return output;
    }

    public V getOutputVariant() {
        return outputVariant;
    }

    @Override
    public boolean contains(T obj) {
        return getOutputVariant().isOf(obj);
    }

    @Override
    public Transferable<T, ? extends TransferVariant<T>> getType() {
        return type;
    }

    @Nullable
    @Override
    public Identifier getId() {
        return id;
    }

    @Nullable
    @Override
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @FunctionalInterface
    public static interface OutputFactory<T> {
        public T build(@Nullable Identifier id, long amount, @Nullable NbtCompound nbt);
    }

    public static record Factories<T, V>(OutputFactory<T> outputFactory, Function<T, V> variantFactory) {
    }

    static {
        FACTORIES.putIfAbsent(TransferablesReg.ITEMS, new Factories<>((id, amount, nbt) -> {
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), (int) amount);
            stack.setNbt(nbt);
            return stack;
        }, ItemVariant::of));
        FACTORIES.putIfAbsent(TransferablesReg.FLUIDS, new Factories<>((id, amount, nbt) -> {
            FluidVariant fluid = FluidVariant.of(Registry.FLUID.get(id), nbt);
            return fluid;
        }, fluid -> fluid));
        FACTORIES.putIfAbsent(TransferablesReg.BLOCKS, new Factories<>((id, amount, nbt) -> {
            return Registry.BLOCK.get(id).getDefaultState();
        }, state -> new BlockVariant(state.getBlock())));
        FACTORIES.putIfAbsent(TransferablesReg.ENTITIES, new Factories<>((id, amount, nbt) -> {
            // TODO getWorld or return EntityType
            Entity entity = Registry.ENTITY_TYPE.get(id).create(null);
            if (nbt != null)
                entity.readNbt(nbt);
            return entity;
        }, entity -> new EntityVariant(entity.getType(), entity.writeNbt(new NbtCompound()))));
        FACTORIES.putIfAbsent(TransferablesReg.ELECTRICITY, new Factories<>((id, amount, nbt) -> {
            return ElectricityVariant.INSTANCE;
        }, electricity -> electricity));
        FACTORIES.putIfAbsent(TransferablesReg.HEAT, new Factories<>((id, amount, nbt) -> {
            return HeatVariant.INSTANCE;
        }, heat -> heat));
    }
}
