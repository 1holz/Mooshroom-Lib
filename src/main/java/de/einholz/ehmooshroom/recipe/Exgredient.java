package de.einholz.ehmooshroom.recipe;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.transferable.ElectricityVariant;
import de.einholz.ehmooshroom.storage.transferable.HeatVariant;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// XXX: anotate constructor with only server?
public class Exgredient<T> {
    public static final Map<Transferable<?, ? extends TransferVariant<?>>, ExgredientFactory<?>> FACTORIES = new HashMap<>();
    private final Transferable<T, ? extends TransferVariant<T>> type;
    @Nullable
    private final T output;
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
        output = (T) FACTORIES.get(this.type).build(id, amount, nbt);
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

    public static Exgredient<?> read(PacketByteBuf buf) {
        return new Exgredient<>(buf.readIdentifier(), buf.readBoolean() ? buf.readIdentifier() : null, buf.readBoolean() ? new NbtCompound() : buf.readNbt(), buf.readVarLong());
    }

    @SuppressWarnings("null")
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(type.getId());
        if (id == null) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeIdentifier(id);
        }
        if (nbt == null || nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
        buf.writeNbt(nbt).writeVarLong(amount);
    }

    public T getOutput() {
        return output;
    }

    public boolean matches(TransferVariant<T> test, NbtCompound testNbt) {
        if (type == null/* && tag == null*/) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("Attempted to perform match test on Exgredient with null type and tag. This Exgredient will be skiped!"));
            return true;
        }
        if (!NbtHelper.matches(nbt, testNbt, true)) return false;
        return type.getVariantType().equals(test.getObject());
        //if (tag == null) return type.getVariantType().equals(test.getObject());
        //return tag.contains(test);
    }

    public Transferable<T, ? extends TransferVariant<T>> getType() {
        return type;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    public long getAmount() {
        return amount;
    }

    @FunctionalInterface
    public static interface ExgredientFactory<T> {
        public T build(@Nullable Identifier id, long amount, @Nullable NbtCompound nbt);
    }

    static {
        FACTORIES.putIfAbsent(TransferablesReg.ITEMS, (id, amount, nbt) -> {
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), (int) amount);
            stack.setNbt(nbt);
            return stack;
        });
        FACTORIES.putIfAbsent(TransferablesReg.FLUIDS, (id, amount, nbt) -> {
            FluidVariant fluid = FluidVariant.of(Registry.FLUID.get(id), nbt);
            return fluid;
        });
        FACTORIES.putIfAbsent(TransferablesReg.BLOCKS, (id, amount, nbt) -> {
            // TODO nbt
            return Registry.BLOCK.get(id).getDefaultState();
        });
        FACTORIES.putIfAbsent(TransferablesReg.ENTITIES, (id, amount, nbt) -> {
            // TODO getWorld or return EntityType
            Entity entity = Registry.ENTITY_TYPE.get(id).create(null);
            if (nbt != null) entity.readNbt(nbt);
            return entity;
        });
        FACTORIES.putIfAbsent(TransferablesReg.ELECTRICITY, (id, amount, nbt) -> {
            ElectricityVariant electricity = ElectricityVariant.INSTANCE;
            return electricity;
        });
        FACTORIES.putIfAbsent(TransferablesReg.HEAT, (id, amount, nbt) -> {
            HeatVariant heat = HeatVariant.INSTANCE;
            return heat;
        });
    }
}
