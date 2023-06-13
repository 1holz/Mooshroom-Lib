package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class SidedStorageMgr implements NbtSerializable {
    private final Map<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> STORAGES = new HashMap<>();

    public Set<Identifier> getIdSet() {
        return STORAGES.keySet();
    }

    public List<Identifier> getIds() {
        return new ArrayList<>(getIdSet());
    }

    public <T, V extends TransferVariant<T>> SidedStorageMgr withStorage(Identifier id, Transferable<T, V> trans, Storage<V> storage) {
        STORAGES.put(id, new StorageEntry<T, V>(storage, SideConfigType.getDefaultArray(), trans));
        return this;
    }

    public SidedStorageMgr removeStorage(Identifier id) {
        STORAGES.remove(id);
        return this;
    }

    public StorageEntry<?, ? extends TransferVariant<?>> getEntry(Identifier id) {
        return STORAGES.get(id);
    }

    /* TODO del if not needed
    public <T, U extends TransferVariant<T>> SidedStorageMgr withStorage(Identifier id, Storage<U> storage) {
        return this.<T, U>withStorage(TransferablesReg.TRANSFERABLE.get(id), storage);
    }

    public <T, U extends TransferVariant<T>> SidedStorageMgr withStorage(Transferable<T, U> trans, Storage<U> storage) {
        STORAGES.put((Transferable<?, TransferVariant<?>>) trans, new StorageEntry<T, U>(storage, SideConfigType.getDefaultArray(), trans));
        return this;
    }

    public <T, U extends TransferVariant<T>> Storage<U> removeStorage(Identifier id) {
        return this.<T, U>removeStorage(TransferablesReg.TRANSFERABLE.get(id));
    }

    public <T, U extends TransferVariant<T>> Storage<U> removeStorage(Transferable<T, U> trans) {
        return (Storage<U>) STORAGES.remove(trans).storage;
    }

    public <T, U extends TransferVariant<T>> StorageEntry<T, U> getStorageEntry(Identifier id) {
        return this.<T, U>getStorageEntry(TransferablesReg.TRANSFERABLE.get(id));
    }

    public <T, U extends TransferVariant<T>> StorageEntry<T, U> getStorageEntry(Transferable<T, U> trans) {
        return (StorageEntry<T, U>) STORAGES.get(trans);
    }
    */

    public <T, V extends TransferVariant<T>, S extends Storage<V>> AdvCombinedStorage<T, V, S> getCombinedStorage(@Nullable Transferable<T, V> trans, SideConfigAccessor acc, @Nullable SideConfigType... configTypes) {
        return new AdvCombinedStorage<T, V, S>(acc, getStorageEntries(trans, configTypes));
    }

    // XXX private? to hacky?
    /*
     * If trans or configTypes are null they will accept all Transferables/SideConfigTypes
     */
    @SuppressWarnings("unchecked")
    public <T, V extends TransferVariant<T>> List<StorageEntry<T, V>> getStorageEntries(@Nullable Transferable<T, V> trans, @Nullable SideConfigType... configTypes) {
        List<StorageEntry<T, V>> list = new ArrayList<>();
        for (StorageEntry<?, ? extends TransferVariant<?>> storageEntry : STORAGES.values()) {
            if (trans != null && !trans.equals(storageEntry.trans)) continue;
            if (configTypes == null) {
                list.add((StorageEntry<T, V>) storageEntry);
                continue;
            }
            for (SideConfigType configType : configTypes) if (storageEntry.allows(configType)) {
                list.add((StorageEntry<T, V>) storageEntry);
                continue;
            }
        }
        return list;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound sidedStorageMgrNbt = new NbtCompound();
        for (Entry<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> entry : STORAGES.entrySet()) {
            NbtCompound entryNbt = entry.getValue().writeNbt(new NbtCompound());
            if (entryNbt.isEmpty()) continue;
            sidedStorageMgrNbt.put(entry.getKey().toString(), entryNbt);
        }
        if (!sidedStorageMgrNbt.isEmpty()) nbt.put("SidedStorageMgr", sidedStorageMgrNbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (!nbt.contains("SidedStorageMgr", NbtType.COMPOUND)) return;
        NbtCompound sidedStorageMgrNbt = nbt.getCompound("SidedStorageMgr");
        for (Entry<Identifier, StorageEntry<?, ? extends TransferVariant<?>>> entry : STORAGES.entrySet()) {
            if (!sidedStorageMgrNbt.contains(entry.getKey().toString(), NbtType.COMPOUND)) continue;
            entry.getValue().readNbt(sidedStorageMgrNbt.getCompound(entry.getKey().toString()));
        }
    }

    /* TODO del
    @Deprecated
    public static enum SideConfig {
        SPECIAL(false, false),
        INPUT(true, false),
        OUTPUT(false, true),
        STORAGE(true, true);
        
        public static SideConfig[][] getDefaultConfig() {
            return new SideConfig[][] {
                {STORAGE, STORAGE, STORAGE, STORAGE, STORAGE, STORAGE},
                {SPECIAL, SPECIAL, SPECIAL, SPECIAL, SPECIAL, SPECIAL}
            };
        }

        public final boolean IN;
        public final boolean OUT;

        private SideConfig(final boolean IN, final boolean OUT) {
            this.IN = IN;
            this.OUT = OUT;
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public static enum SideConfigBehavior {
        SELF_IN('F'),
        SELF_OUT('F'),
        FOREIGN_IN('T'),
        FOREIGN_OUT('T');

        private final char def;

        private SideConfigBehavior(char def) {
            this.def = def;
        }
    }
    */
}
