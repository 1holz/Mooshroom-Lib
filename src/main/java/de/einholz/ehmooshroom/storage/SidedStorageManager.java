package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SidedStorageManager implements NbtSerializable {
    private final Map<Transferable<?>, StorageEntry<?>> STORAGES = new HashMap<>();

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> SidedStorageManager withStorage(Identifier id, Storage<T> storage) {
        return this.<T>withStorage(TransferablesReg.TRANSFERABLE.get(id), storage);
    }

    public <T> SidedStorageManager withStorage(Transferable<T> trans, Storage<T> storage) {
        STORAGES.put(trans, new StorageEntry<>(storage, SideConfigType.getDefaultArray(), trans));
        return this;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> Storage<T> removeStorage(Identifier id) {
        return this.<T>removeStorage(TransferablesReg.TRANSFERABLE.get(id));
    }

    @SuppressWarnings("unchecked")
    public <T> Storage<T> removeStorage(Transferable<T> trans) {
        return (Storage<T>) STORAGES.remove(trans).storage;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> StorageEntry<T> getStorageEntry(Identifier id) {
        return this.<T>getStorageEntry(TransferablesReg.TRANSFERABLE.get(id));
    }

    @SuppressWarnings("unchecked")
    public <T> StorageEntry<T> getStorageEntry(Transferable<T> trans) {
        return (StorageEntry<T>) STORAGES.get(trans);
    }

    public <T, S extends Storage<T>> AdvCombinedStorage<T, S> getCombinedStorage(@Nullable Transferable<T> trans, @Nullable SideConfigType... configTypes) {
        return new AdvCombinedStorage<T, S>(getStorageEntries(trans, configTypes));
    }

    // XXX private? to hacky?
    @SuppressWarnings("unchecked")
    public <T> List<StorageEntry<T>> getStorageEntries(@Nullable Transferable<T> trans, @Nullable SideConfigType... configTypes) {
        List<StorageEntry<T>> list = new ArrayList<>();
        for (Entry<Transferable<?>, StorageEntry<?>> entry : STORAGES.entrySet()) {
            StorageEntry<?> storageEntry = entry.getValue();
            if (trans != null && !trans.equals(storageEntry.trans)) continue;
            if (configTypes == null) {
                list.add((StorageEntry<T>) storageEntry);
                continue;
            }
            for (SideConfigType configType : configTypes) if (storageEntry.allows(configType)) list.add((StorageEntry<T>) storageEntry);
        }
        return list;
    }

    // XXX private fields?
    public static class StorageEntry<T> {
        public final Storage<T> storage;
        public final char[] config;
        public final Transferable<T> trans;

        public StorageEntry(Storage<T> storage, char[] config, Transferable<T> trans) {
            this.storage = storage;
            if (config.length != SideConfigType.values().length) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config char array should have a lenght of " + SideConfigType.values().length));
            this.config = config;
            this.trans = trans;
        }

        public boolean allows(SideConfigType type) {
            return (type.side == null || type.output ? storage.supportsExtraction() : storage.supportsInsertion()) && Character.toUpperCase(config[type.ordinal()]) == 'T';
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
        IN_IN('T', false, false, null),
        OUT_IN('T', false, true, null),
        SELF_IN_D('F', false, false, Direction.DOWN),
        SELF_IN_U('F', false, false, Direction.UP),
        SELF_IN_N('F', false, false, Direction.NORTH),
        SELF_IN_S('F', false, false, Direction.SOUTH),
        SELF_IN_W('F', false, false, Direction.WEST),
        SELF_IN_E('F', false, false, Direction.EAST),
        SELF_OUT_D('F', false, true, Direction.DOWN),
        SELF_OUT_U('F', false, true, Direction.UP),
        SELF_OUT_N('F', false, true, Direction.NORTH),
        SELF_OUT_S('F', false, true, Direction.SOUTH),
        SELF_OUT_W('F', false, true, Direction.WEST),
        SELF_OUT_E('F', false, true, Direction.EAST),
        FOREIGN_IN_D('T', true, false, Direction.DOWN),
        FOREIGN_IN_U('T', true, false, Direction.UP),
        FOREIGN_IN_N('T', true, false, Direction.NORTH),
        FOREIGN_IN_S('T', true, false, Direction.SOUTH),
        FOREIGN_IN_W('T', true, false, Direction.WEST),
        FOREIGN_IN_E('T', true, false, Direction.EAST),
        FOREIGN_OUT_D('T', true, true, Direction.DOWN),
        FOREIGN_OUT_U('T', true, true, Direction.UP),
        FOREIGN_OUT_N('T', true, true, Direction.NORTH),
        FOREIGN_OUT_S('T', true, true, Direction.SOUTH),
        FOREIGN_OUT_W('T', true, true, Direction.WEST),
        FOREIGN_OUT_E('T', true, true, Direction.EAST);

        public final char def;
        public final boolean foreign;
        public final boolean output;
        public final Direction side;

        private SideConfigType(char def, boolean foreign, boolean output, Direction side) {
            this.def = def;
            this.foreign = foreign;
            this.output = output;
            this.side = side;
        }

        public static char[] getDefaultArray() {
            final SideConfigType[] values = SideConfigType.values();
            char[] array = new char[values.length];
            for (int i = 0; i < array.length; i++) array[i] = values[i].getDefaultChar();
            return array;
        }

        @Deprecated
        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return def;
        }

        public static SideConfigType getFromParams(boolean foreign, boolean output, @Nullable Direction dir) {
            int dirsAmount = Direction.values().length;
            SideConfigType[] values = SideConfigType.values();
            if (dir == null) return values[output ? 1 : 0];
            return values[(foreign ? 2 * dirsAmount : 0) + (output ? dirsAmount : 0) + dir.ordinal() + 2];
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
