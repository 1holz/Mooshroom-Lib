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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import de.einholz.ehmooshroom.storage.storages.AdvCombinedStorage;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class SidedStorageMgr implements NbtSerializable {
    private final Map<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> STORAGES = new HashMap<>();
    private final BlockEntity dirtyMarker;

    public SidedStorageMgr(BlockEntity dirtyMarker) {
        this.dirtyMarker = dirtyMarker;
    }

    public List<Identifier> getIds() {
        return new ArrayList<>(STORAGES.keySet());
    }

    public List<Identifier> getAvaialableIds() {
        List<Identifier> list = new ArrayList<>();
        STORAGES.forEach((id, entry) -> {
            if (entry.available())
                list.add(id);
        });
        return list;
    }

    public <T, V extends TransferVariant<T>> SidedStorageMgr withStorage(Identifier id, Identifier transId, Storage<V> storage) {
        STORAGES.put(id, new StorageEntry<T, V>(storage, SideConfigType.getDefaultArray(), transId, dirtyMarker));
        return this;
    }

    public SidedStorageMgr removeStorage(Identifier id) {
        STORAGES.remove(id);
        return this;
    }

    public StorageEntry<?, ? extends TransferVariant<?>> getEntry(Identifier id) {
        return STORAGES.get(id);
    }

    public <T, V extends TransferVariant<T>, S extends Storage<V>> AdvCombinedStorage<T, V, S> getCombinedStorage(
            @Nullable Identifier typeId, SideConfigAccessor acc, @Nullable SideConfigType... configTypes) {
        return new AdvCombinedStorage<T, V, S>(acc, getStorageEntries(typeId, configTypes));
    }

    /**
     * @param <T>         Type of the {@link TransferVariant}
     * @param <V>         {@link TransferVariant}
     * @param trans       the {@link Transferable} the {@link StorageEntry}s have to
     *                    match. {@code null} will accept a {@link StorageEntry} no
     *                    matter the {@link Transferable}
     * @param configTypes the {@link SideConfigType}s the {@link StorageEntry}s have
     *                    to match. {@code null} will accept a {@link StorageEntry}
     *                    no matter the {@link SideConfigType}
     * @return a {@link List} of all {@link StorageEntry}s matching the parameters
     */
    // XXX private? to hacky?
    @SuppressWarnings("unchecked")
    public <T, V extends TransferVariant<T>> List<StorageEntry<T, V>> getStorageEntries(
            @Nullable Identifier typeId, @Nullable SideConfigType... configTypes) {
        List<StorageEntry<T, V>> list = new ArrayList<>();
        for (var entry : STORAGES.values()) {
            if (typeId != null && !typeId.equals(entry.getTransferId()))
                continue;
            if (configTypes == null) {
                list.add((StorageEntry<T, V>) entry);
                continue;
            }
            for (SideConfigType configType : configTypes) {
                if (!entry.allows(configType))
                    continue;
                list.add((StorageEntry<T, V>) entry);
                break;
            }
        }
        return list;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound sidedStorageMgrNbt = new NbtCompound();
        for (Entry<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> entry : STORAGES.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entry.getValue().writeNbt(entryNbt);
            if (entryNbt.isEmpty())
                continue;
            sidedStorageMgrNbt.put(entry.getKey().toString(), entryNbt);
        }
        if (!sidedStorageMgrNbt.isEmpty())
            nbt.put("SidedStorageMgr", sidedStorageMgrNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!nbt.contains("SidedStorageMgr", NbtType.COMPOUND))
            return;
        NbtCompound sidedStorageMgrNbt = nbt.getCompound("SidedStorageMgr");
        for (Entry<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> entry : STORAGES.entrySet()) {
            if (!sidedStorageMgrNbt.contains(entry.getKey().toString(), NbtType.COMPOUND))
                continue;
            entry.getValue().readNbt(sidedStorageMgrNbt.getCompound(entry.getKey().toString()));
        }
    }
}
