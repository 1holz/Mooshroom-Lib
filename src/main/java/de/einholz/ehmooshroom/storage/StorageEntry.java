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

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class StorageEntry<T, U extends TransferVariant<T>> implements NbtSerializable {
    private final Storage<U> storage;
    private final char[] config;
    private final Transferable<T, U> trans;
    private final BlockEntity dirtyMarker;

    public StorageEntry(Storage<U> storage, char[] config, Transferable<T, U> trans, BlockEntity dirtyMarker) {
        this.storage = storage;
        SideConfigType[] values = SideConfigType.values();
        if (config.length != values.length) {
            MooshroomLib.LOGGER
                    .warnBug("The config char array should have a lenght of " + values.length);
            char[] newConfig = new char[values.length];
            for (int i = 0; i < config.length; i++)
                newConfig[i] = config[i];
            for (int i = config.length; i < values.length; i++)
                newConfig[i] = values[i].getDefaultChar();
            this.config = newConfig;
        } else
            this.config = config;
        this.trans = trans;
        this.dirtyMarker = dirtyMarker;
    }

    public void change(SideConfigType type) {
        for (int i = 0; i < SideConfigType.CHARS.length; i++) {
            if (SideConfigType.CHARS[i] != config[type.ordinal()])
                continue;
            config[type.ordinal()] = SideConfigType.CHARS[i ^ 0x0001];
            dirtyMarker.markDirty();
            return;
        }
    }

    public boolean available(SideConfigType type) {
        return Character.isUpperCase(config[type.ordinal()]);
    }

    public boolean available() {
        for (char c : config)
            if (!Character.isLowerCase(c))
                return true;
        return false;
    }

    /**
     * Sets the availability for the given {@link SideConfigType}s
     *
     * @param available the availabiltity it is set to
     * @param types     if {@code types == null} this will default to
     *                  {@code SideConfigType.values()}
     */
    public void setAvailability(boolean available, SideConfigType... types) {
        if (types == null)
            types = SideConfigType.values();
        for (SideConfigType type : types)
            if (Character.toUpperCase(config[type.ordinal()]) == 'T')
                config[type.ordinal()] = available ? 'T' : 't';
            else
                config[type.ordinal()] = available ? 'F' : 'f';
    }

    public boolean allows(SideConfigType type) {
        if (Character.toUpperCase(config[type.ordinal()]) != 'T')
            return false;
        return type.OUTPUT ? getStorage().supportsExtraction() : getStorage().supportsInsertion();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (getStorage() instanceof NbtSerializable seri) {
            NbtCompound storageNbt = new NbtCompound();
            seri.writeNbt(storageNbt);
            if (!storageNbt.isEmpty())
                nbt.put("Storage", storageNbt);
        }
        nbt.putString("Config", String.valueOf(config));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (getStorage() instanceof NbtSerializable seri && nbt.contains("Storage", NbtType.COMPOUND))
            seri.readNbt(nbt.getCompound("Storage"));
        if (nbt.contains("Config", NbtType.STRING)) {
            String str = nbt.getString("Config");
            if (str.length() < config.length) {
                MooshroomLib.LOGGER
                        .warnBug("Config string for " + getTransferable().getId() + " has a lenght of " + str.length()
                                + " but should have " + config.length);
                return;
            }
            for (int i = 0; i < config.length; i++)
                config[i] = str.charAt(i);
        }
    }

    public Storage<U> getStorage() {
        return storage;
    }

    public Transferable<T, U> getTransferable() {
        return trans;
    }
}
