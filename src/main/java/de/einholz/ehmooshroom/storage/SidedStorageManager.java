package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SidedStorageManager implements NbtSerializable {
    private final Map<Identifier, StorageEntry<?>> STORAGES = new HashMap<>();

    public <T> void addStorage(Identifier id, Storage<T> storage, Class<T> clazz) {
        addStorage(id, storage, clazz, null);
    }

    public <T> void addStorage(Identifier id, Storage<T> storage, Class<T> clazz, BlockApiLookup<? extends Storage<T>, Direction> lookup) {
        STORAGES.put(id, new StorageEntry<>(storage, SideConfigType.getDefaultArray(), clazz, lookup));
    }

    public Storage<?> removeStorage(Identifier id) {
        return STORAGES.remove(id).storage;
    }

    public StorageEntry<?> getStorageEntry(Identifier id) {
        return STORAGES.get(id);
    }

    public <T> List<Storage<T>> getStorages(@Nullable Class<T> type, @Nullable SideConfigType... configType) {
        List<Storage<T>> list = new ArrayList<>();
        for (StorageEntry<T> entry : getStorageEntries(type, configType)) list.add(entry.storage);
        return list;
    }

    // XXX private? to hacky?
    @SuppressWarnings("unchecked")
    public <T> List<StorageEntry<T>> getStorageEntries(@Nullable Class<T> type, @Nullable SideConfigType... configTypes) {
        List<StorageEntry<T>> list = new ArrayList<>();
        for (Entry<Identifier, StorageEntry<?>> entry : STORAGES.entrySet()) {
            StorageEntry<?> storageEntry = entry.getValue();
            if (type == null || type.isAssignableFrom(storageEntry.clazz)) {
                if (configTypes == null) {
                    list.add((StorageEntry<T>) storageEntry);
                    continue;
                }
                for (SideConfigType configType : configTypes) if (storageEntry.allows(configType)) list.add((StorageEntry<T>) storageEntry);
            }
        }
        return list;
    }

    // XXX private? also for fields?
    public static class StorageEntry<T> implements Storage<T> {
        public final Storage<T> storage;
        public final char[] config;
        public final Class<T> clazz;
        @Nullable
        public final BlockApiLookup<? extends Storage<T>, Direction> lookup;

        public StorageEntry(Storage<T> storage, char[] config, Class<T> clazz, BlockApiLookup<? extends Storage<T>, Direction> lookup) {
            this.storage = storage;
            if (config.length != SideConfigType.values().length) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config char array should have a lenght of " + SideConfigType.values().length));
            this.config = config;
            this.clazz = clazz;
            this.lookup = lookup;
        }

        public boolean allows(SideConfigType type) {
            return Character.toUpperCase(config[type.ordinal()]) == 'T';
        }

        @Override
        public long insert(T resource, long maxAmount, TransactionContext transaction) {
            return storage.insert(resource, maxAmount, transaction);
        }

        @Override
        public long extract(T resource, long maxAmount, TransactionContext transaction) {
            return storage.extract(resource, maxAmount, transaction);
        }

        @Override
        public Iterator<StorageView<T>> iterator(TransactionContext transaction) {
            return storage.iterator(transaction);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (StorageEntry<?> entry : STORAGES.values()) if (entry.storage instanceof NbtSerializable serializable) nbt = serializable.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        for (StorageEntry<?> entry : STORAGES.values()) if (entry.storage instanceof NbtSerializable serializable) serializable.readNbt(nbt);
    }

    //AVAILABLE_TRUE -> T
    //AVAILABLE_FALSE -> F
    //RESTRICTED_TRUE -> t
    //RESTRICTED_FALSE -> f
    public static enum SideConfigType {
        SELF_IN_D('F', Direction.DOWN),
        SELF_IN_U('F', Direction.UP),
        SELF_IN_N('F', Direction.NORTH),
        SELF_IN_S('F', Direction.SOUTH),
        SELF_IN_W('F', Direction.WEST),
        SELF_IN_E('F', Direction.EAST),
        SELF_OUT_D('F', Direction.DOWN),
        SELF_OUT_U('F', Direction.UP),
        SELF_OUT_N('F', Direction.NORTH),
        SELF_OUT_S('F', Direction.SOUTH),
        SELF_OUT_W('F', Direction.WEST),
        SELF_OUT_E('F', Direction.EAST),
        FOREIGN_IN_D('T', Direction.DOWN),
        FOREIGN_IN_U('T', Direction.UP),
        FOREIGN_IN_N('T', Direction.NORTH),
        FOREIGN_IN_S('T', Direction.SOUTH),
        FOREIGN_IN_W('T', Direction.WEST),
        FOREIGN_IN_E('T', Direction.EAST),
        FOREIGN_OUT_D('T', Direction.DOWN),
        FOREIGN_OUT_U('T', Direction.UP),
        FOREIGN_OUT_N('T', Direction.NORTH),
        FOREIGN_OUT_S('T', Direction.SOUTH),
        FOREIGN_OUT_W('T', Direction.WEST),
        FOREIGN_OUT_E('T', Direction.EAST);

        public final char def;
        public final Direction dir;

        private SideConfigType(char def, Direction dir) {
            this.def = def;
            this.dir = dir;
        }

        public static char[] getDefaultArray() {
            final SideConfigType[] values = SideConfigType.values();
            char[] array = new char[values.length];
            for (int i = 0; i < array.length; i++) array[i] = values[i].getDefaultChar();
            return array;
        }

        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return def;
        }

        public static SideConfigType getFromParams(boolean foreign, boolean output, Direction dir) {
            int dirsAmount = Direction.values().length;
            return SideConfigType.values()[(foreign ? 2 * dirsAmount : 0) + (output ? dirsAmount : 0) + dir.ordinal()];
        }
    }

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
}
