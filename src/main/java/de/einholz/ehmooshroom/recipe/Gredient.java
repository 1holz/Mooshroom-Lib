package de.einholz.ehmooshroom.recipe;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.storage.variants.SingletonVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public interface Gredient<T> {
    @Deprecated
    public static <T, G extends Gredient<? extends T>> G of(GredientFactory<T, G> factory, Identifier typeId,
            @Nullable Identifier id, @Nullable NbtCompound nbt,
            long amount) {
        return factory.build(typeId, id, nbt, amount);
    }

    public static <T, G extends Gredient<? extends T>> G readFromJson(GredientFactory<T, G> factory,
            JsonElement jsonElement) {
        JsonObject json = (JsonObject) jsonElement;
        Identifier type = new Identifier(JsonHelper.getString(json, "type", ""));
        Identifier id = null;
        if (json.has("id"))
            id = new Identifier(JsonHelper.getString(json, "id"));
        NbtCompound nbt;
        try {
            nbt = StringNbtReader.parse(JsonHelper.getString(json, "nbt", "{}"));
        } catch (CommandSyntaxException e) {
            MooshroomLib.LOGGER.errorBug("Something went wrong trying to parse the nbt for the Gredient "
                    + type, e);
            nbt = new NbtCompound();
        }
        long amount = JsonHelper.getLong(json, "amount", 0L);
        return factory.build(type, id, nbt, amount);
    }

    @SuppressWarnings("null")
    default void write(PacketByteBuf buf) {
        buf.writeIdentifier(getType().getId());
        if (getId() == null)
            buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeIdentifier(getId());
        }
        if (getNbt() == null || getNbt().isEmpty())
            buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(getNbt());
        }
        buf.writeNbt(getNbt()).writeVarLong(getAmount());
    }

    public static <T, G extends Gredient<? extends T>> G read(GredientFactory<T, G> factory, PacketByteBuf buf) {
        Identifier type = buf.readIdentifier();
        Identifier id = buf.readBoolean() ? buf.readIdentifier() : null;
        NbtCompound nbt = buf.readBoolean() ? buf.readNbt() : new NbtCompound();
        long amount = buf.readVarLong();
        return factory.build(type, id, nbt, amount);
    }

    @SuppressWarnings("unchecked")
    default boolean matches(TransferVariant<?> test) {
        if (getType() == null) {
            MooshroomLib.LOGGER.warnBug(
                    "Attempted to perform match test on Gredient with null type and " + getId()
                            + " id. This is likely due to a malformated recipe json file. This Exgredient will be skipped!");
            return false;
        }
        if (!getType().getVariantType().isAssignableFrom(test.getClass()))
            return false;
        if (getId() == null) {
            if (test instanceof SingletonVariant)
                return true;
            MooshroomLib.LOGGER.warnBug("Attempted to perform match test on Gredient with " + getType().getId(),
                    "type, null id and a TransferVariant that doesnt extend SingletonVariant. This is likely due to a malformated recipe json file. This Exgredient will be skipped!");
            return false;
        }
        if (!NbtHelper.matches(test.copyNbt(), getNbt(), true))
            return false;
        return contains((T) test.getObject());
    }

    abstract boolean contains(T obj);

    abstract Transferable<T, ? extends TransferVariant<T>> getType();

    @Nullable
    abstract Identifier getId();

    @Nullable
    abstract NbtCompound getNbt();

    abstract long getAmount();

    @FunctionalInterface
    public static interface GredientFactory<T, G extends Gredient<? extends T>> {
        G build(Identifier typeId, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount);
    }
}
