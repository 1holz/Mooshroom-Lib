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

import java.util.Iterator;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

// XXX anotate constructor with only server?
public class Ingredient<T> implements Gredient<T> {
    // private final Transferable<T, ? extends TransferVariant<T>> type;
    private final Identifier typeId;
    private final boolean isSingleton;
    @Nullable
    private final TagKey<T> tag;
    private final NbtCompound nbt;
    private final long amount;

    public Ingredient(Identifier typeId, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount) {
        this.typeId = typeId;
        this.isSingleton = !Registry.REGISTRIES.containsId(typeId);
        if (isSingleton) {
            this.tag = null;
            this.nbt = new NbtCompound();
        } else {
            this.tag = (TagKey<T>) TagKey.of(Registry.REGISTRIES.get(typeId).getKey(), id);
            this.nbt = nbt == null ? new NbtCompound() : nbt;
        }
        this.amount = amount;
    }

    @Override
    public boolean contains(T obj) {
        if (tag == null)
            return false;
        Iterator<RegistryEntry<T>> iter = ((Registry<T>) Registry.REGISTRIES.get(typeId)).iterateEntries(tag)
                .iterator();
        while (iter.hasNext())
            if (obj.equals(iter.next().value()))
                return true;
        return false;
    }

    @Override
    public Identifier getTypeId() {
        return typeId;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Nullable
    @Override
    public Identifier getId() {
        if (tag == null)
            return null;
        return tag.id();
    }

    @Override
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public long getAmount() {
        return amount;
    }
}
