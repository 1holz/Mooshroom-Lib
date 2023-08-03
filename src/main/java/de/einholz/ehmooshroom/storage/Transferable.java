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

package de.einholz.ehmooshroom.storage;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class Transferable<T, V extends TransferVariant<T>> {
    public static final Identifier ELECTRICITY_ID = MooshroomLib.HELPER.makeId("electricity");
    public static final Identifier HEAT_ID = MooshroomLib.HELPER.makeId("heat");
    private Identifier id;
    @Nullable
    private final RegistryKey<Registry<T>> key;
    @Nullable
    private final BlockApiLookup<? extends Storage<V>, Direction> lookup;

    public Transferable(RegistryKey<Registry<T>> key,
            @Nullable BlockApiLookup<? extends Storage<V>, Direction> lookup) {
        this.id = key.getValue();
        this.key = key;
        this.lookup = lookup;
    }

    public Transferable(Identifier id, @Nullable BlockApiLookup<? extends Storage<V>, Direction> lookup) {
        this.id = id;
        this.key = null;
        this.lookup = lookup;
    }

    public Identifier getId() {
        return id;
    }

    @Nullable
    public RegistryKey<Registry<T>> getKey() {
        return key;
    }

    @Nullable
    public BlockApiLookup<? extends Storage<V>, Direction> getLookup() {
        return lookup;
    }

    public boolean isTransferable() {
        return lookup != null/* && variantType != null */;
    }

    public boolean isProcessable() {
        return true; // TODO
    }
}
