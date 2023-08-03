/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public interface Gredient<T> {
    public static <T, G extends Gredient<? extends T>> G readFromJson(GredientFactory<T, G> factory,
            JsonElement jsonElement) {
        JsonObject json = (JsonObject) jsonElement;
        if (!JsonHelper.hasString(json, "typeId"))
            MooshroomLib.LOGGER.error("Gredient is missing typeId!");
        Identifier typeId = new Identifier(JsonHelper.getString(json, "typeId"));
        Identifier id = null;
        if (json.has("id"))
            id = new Identifier(JsonHelper.getString(json, "id"));
        NbtCompound nbt;
        try {
            nbt = StringNbtReader.parse(JsonHelper.getString(json, "nbt", "{}"));
        } catch (CommandSyntaxException e) {
            MooshroomLib.LOGGER.errorBug("Something went wrong trying to parse the nbt for the Gredient "
                    + typeId, e);
            nbt = new NbtCompound();
        }
        long amount = JsonHelper.getLong(json, "amount", 0L);
        return factory.build(typeId, id, nbt, amount);
    }

    default void write(PacketByteBuf buf) {
        buf.writeIdentifier(getTypeId());
        if (isSingleton())
            buf.writeBoolean(true);
        else {
            buf.writeBoolean(false);
            buf.writeIdentifier(getId());
            buf.writeNbt(getNbt());
        }
        buf.writeVarLong(getAmount());
    }

    public static <T, G extends Gredient<? extends T>> G read(GredientFactory<T, G> factory, PacketByteBuf buf) {
        Identifier type = buf.readIdentifier();
        boolean isSingleton = buf.readBoolean();
        Identifier id = isSingleton ? null : buf.readIdentifier();
        NbtCompound nbt = isSingleton ? new NbtCompound() : buf.readNbt();
        long amount = buf.readVarLong();
        return factory.build(type, id, nbt, amount);
    }

    @SuppressWarnings({ "unchecked", "unused" })
    default boolean matches(TransferVariant<?> test) {
        // XXX is there a better way to do this?
        try {
            T t = (T) test.getObject();
        } catch (ClassCastException e) {
            return false;
        }
        if (isSingleton())
            return true;
        if (!NbtHelper.matches(test.copyNbt(), getNbt(), true))
            return false;
        return contains((T) test.getObject());
    }

    abstract boolean contains(T obj);

    abstract Identifier getTypeId();

    /**
     * {@code id} and {@code nbt} are ignored in Singleton {@link Gredient}s. For a
     * non Singleton {@link Gredient} {@code getId()} must not return {@code null}
     *
     * @return whether the {@link Gredient} is a Singleton
     */
    abstract boolean isSingleton();

    @Nullable
    abstract Identifier getId();

    abstract NbtCompound getNbt();

    abstract long getAmount();

    @FunctionalInterface
    public static interface GredientFactory<T, G extends Gredient<? extends T>> {
        G build(Identifier typeId, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount);
    }
}
