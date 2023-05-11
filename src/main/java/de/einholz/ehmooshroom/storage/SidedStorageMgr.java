package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SidedStorageMgr implements NbtSerializable {
    private final Map<Transferable<?>, StorageEntry<?>> STORAGES = new HashMap<>();

    public List<Identifier> getIds() {
        List<Identifier> ids = new ArrayList<>(STORAGES.size());
        for (Transferable<?> trans : STORAGES.keySet()) ids.add(trans.getId());
        return ids;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public <T> SidedStorageMgr withStorage(Identifier id, Storage<T> storage) {
        return this.<T>withStorage(TransferablesReg.TRANSFERABLE.get(id), storage);
    }

    public <T> SidedStorageMgr withStorage(Transferable<T> trans, Storage<T> storage) {
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

    @SuppressWarnings("unchecked")
    public <T> StorageEntry<T> getStorageEntry(Identifier id) {
        return this.<T>getStorageEntry(TransferablesReg.TRANSFERABLE.get(id));
    }

    @SuppressWarnings("unchecked")
    public <T> StorageEntry<T> getStorageEntry(Transferable<T> trans) {
        return (StorageEntry<T>) STORAGES.get(trans);
    }

    public <T, S extends Storage<T>> AdvCombinedStorage<T, S> getCombinedStorage(/*@Nullable*/ Transferable<T> trans, /*@Nullable*/ SideConfigType... configTypes) {
        return new AdvCombinedStorage<T, S>(getStorageEntries(trans, configTypes));
    }

    // XXX private? to hacky?
    // TODO since Transferables are keys only one storage can exist per transferable
    @SuppressWarnings("unchecked")
    public <T> List<StorageEntry<T>> getStorageEntries(/*@Nullable*/ Transferable<T> trans, /*@Nullable*/ SideConfigType... configTypes) {
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

    // XXX private fields and getters?
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

        public void change(SideConfigType type) {
            for (int i = 0; i < SideConfigType.CHARS.length; i++)
                if (SideConfigType.CHARS[i] == config[type.ordinal()]) {
                    config[type.ordinal()] = SideConfigType.CHARS[i ^ 0x0001];
                    return;
                }
        }

        public boolean available(SideConfigType type) {
            return Character.isUpperCase(type.DEF);
        }

        public boolean allows(SideConfigType type) {
            return (type.OUTPUT ? storage.supportsExtraction() : storage.supportsInsertion()) && Character.toUpperCase(config[type.ordinal()]) == 'T';
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

    public static enum SideConfigAccessor {
        GUI,
        PROCESS,
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST;

        public static SideConfigAccessor getFromDir(Direction dir) {
            return dir == null ? PROCESS : SideConfigAccessor.values()[dir.ordinal() + 2];
        }
    }

    public static enum SideConfigType {
        IN_GUI('T', false, false, SideConfigAccessor.GUI),
        OUT_GUI('T', false, true, SideConfigAccessor.GUI),
        IN_PROC('T', false, false, SideConfigAccessor.PROCESS),
        OUT_PROC('T', false, true, SideConfigAccessor.PROCESS),
        SELF_IN_D('F', false, false, SideConfigAccessor.DOWN),
        SELF_IN_U('F', false, false, SideConfigAccessor.UP),
        SELF_IN_N('F', false, false, SideConfigAccessor.NORTH),
        SELF_IN_S('F', false, false, SideConfigAccessor.SOUTH),
        SELF_IN_W('F', false, false, SideConfigAccessor.WEST),
        SELF_IN_E('F', false, false, SideConfigAccessor.EAST),
        SELF_OUT_D('F', false, true, SideConfigAccessor.DOWN),
        SELF_OUT_U('F', false, true, SideConfigAccessor.UP),
        SELF_OUT_N('F', false, true, SideConfigAccessor.NORTH),
        SELF_OUT_S('F', false, true, SideConfigAccessor.SOUTH),
        SELF_OUT_W('F', false, true, SideConfigAccessor.WEST),
        SELF_OUT_E('F', false, true, SideConfigAccessor.EAST),
        FOREIGN_IN_D('T', true, false, SideConfigAccessor.DOWN),
        FOREIGN_IN_U('T', true, false, SideConfigAccessor.UP),
        FOREIGN_IN_N('T', true, false, SideConfigAccessor.NORTH),
        FOREIGN_IN_S('T', true, false, SideConfigAccessor.SOUTH),
        FOREIGN_IN_W('T', true, false, SideConfigAccessor.WEST),
        FOREIGN_IN_E('T', true, false, SideConfigAccessor.EAST),
        FOREIGN_OUT_D('T', true, true, SideConfigAccessor.DOWN),
        FOREIGN_OUT_U('T', true, true, SideConfigAccessor.UP),
        FOREIGN_OUT_N('T', true, true, SideConfigAccessor.NORTH),
        FOREIGN_OUT_S('T', true, true, SideConfigAccessor.SOUTH),
        FOREIGN_OUT_W('T', true, true, SideConfigAccessor.WEST),
        FOREIGN_OUT_E('T', true, true, SideConfigAccessor.EAST);

        public final static char[] CHARS = new char[]{
            'T', // 00 AVAILABLE_TRUE
            'F', // 01 AVAILABLE_FALSE
            't', // 10 RESTRICTED_TRUE
            'f'  // 11 RESTRICTED_FALSE
        };
        public final char DEF;
        public final boolean FOREIGN;
        public final boolean OUTPUT;
        public final SideConfigAccessor ACC;

        private SideConfigType(final char DEF, final boolean FOREIGN, final boolean OUTPUT, final SideConfigAccessor ACC) {
            this.DEF = DEF;
            this.FOREIGN = FOREIGN;
            this.OUTPUT = OUTPUT;
            this.ACC = ACC;
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
            return DEF;
        }

        public static SideConfigType getFromParams(boolean foreign, boolean output, Direction dir) {
            return getFromParams(foreign, output, SideConfigAccessor.getFromDir(dir));
        }

        public static SideConfigType getFromParams(boolean foreign, boolean output, SideConfigAccessor acc) {
            int dirLen = Direction.values().length;
            SideConfigType[] values = SideConfigType.values();
            if (SideConfigAccessor.GUI.equals(acc)) return values[output ? 1 : 0];
            if (SideConfigAccessor.PROCESS.equals(acc)) return values[output ? 3 : 2];
            return values[(foreign ? 2 * dirLen : 0) + (output ? dirLen : 0) + acc.ordinal() + 2];
        }
    }

    // TODO del
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

    // TODO del
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
