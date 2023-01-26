package de.einholz.ehmooshroom.recipe;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// XXX: anotate constructor with only server?
public class Exgredient<T> {
    public static final Map<Class<?>, ExgredientFactory<?>> FACTORIES = new HashMap<>();
    private final Class<T> type;
    @Nullable 
    private final T output;
    @Nullable
    private final Identifier id;
    @Nullable
    private final NbtCompound nbt;
    private final long amount;

    @SuppressWarnings("unchecked")
    public Exgredient(String type, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount) {
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            MooshroomLib.LOGGER.bigBug(e);
        }
        if (clazz == null) MooshroomLib.LOGGER.bigBug(new NullPointerException("Exgredient with null type was created. id: " + id == null ? "null" : id.toString() + " nbt: " + nbt == null ? "null" : nbt.asString() + " amount: " + amount));
        this.type = clazz;
        this.id = id;
        this.nbt = nbt == null ? new NbtCompound() : nbt;
        this.amount = amount;
        output = (@Nullable T) FACTORIES.get(clazz).build(id, amount, nbt);
    }

    public Exgredient(String type, @Nullable Identifier id, long amount) {
        this(type, id, null, amount);
    }

    public Exgredient(String type, @Nullable NbtCompound nbt, long amount) {
        this(type, null, nbt, amount);
    }

    public Exgredient(String type, long amount) {
        this(type, null, null, amount);
    }

    public static Exgredient<?> read(PacketByteBuf buf) {
        return new Exgredient<>(buf.readString(), buf.readBoolean() ? buf.readIdentifier() : null, buf.readBoolean() ? new NbtCompound() : buf.readNbt(), buf.readVarLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(type.getName());
        if (id == null) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeIdentifier(id);
        }
        if (nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
        buf.writeNbt(nbt).writeVarLong(amount);
    }

    public T getOutput() {
        return output;
    }

    // TODO delete if not used
    @Deprecated
    public boolean matches(T test, NbtCompound testNbt) {
        if (type == null/* && tag == null*/) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("Attempted to perform match test on Exgredient with null type and tag. This Exgredient will be skiped!"));
            return true;
        }
        if (!NbtHelper.matches(nbt, testNbt, true)) return false;
        return type.isAssignableFrom(test.getClass());
        //if (tag == null) return type.isAssignableFrom(test.getClass());
        //return tag.contains(test);
    }

    // TODO delete if not used
    @Deprecated
    public Class<T> getType() {
        return type;
    }

    // TODO delete if not used
    @Deprecated
    public long getAmount() {
        return amount;
    }

    @FunctionalInterface
    public static interface ExgredientFactory<T> {
        public T build(@Nullable Identifier id, long amount, @Nullable NbtCompound nbt);
    }

    static {
        FACTORIES.putIfAbsent(ItemStack.class, (id, amount, nbt) -> {
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), (int) amount);
            stack.setNbt(nbt);
            return stack;
        });
        FACTORIES.putIfAbsent(FluidVariant.class, (id, amount, nbt) -> {
            FluidVariant fluid = FluidVariant.of(Registry.FLUID.get(id), nbt);
            return fluid;
        });
        FACTORIES.putIfAbsent(Block.class, (id, amount, nbt) -> {
            BlockState state = Registry.BLOCK.get(id).getDefaultState();
            return state;
        });
        FACTORIES.putIfAbsent(Entity.class, (id, amount, nbt) -> {
            Entity entity = Registry.ENTITY_TYPE.get(id).create(null);
            if (nbt != null) entity.readNbt(nbt);
            return entity;
        });
    }
}
