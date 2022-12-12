package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SidedStorageManager {
    private final Map<Identifier, StorageEntry<?>> STORAGES = new HashMap<>();

    public <T> void addStorage(Identifier id, Storage<T> storage, Class<T> clazz) {
        STORAGES.put(id, new StorageEntry<>(storage, SideConfigType.getDefaultArray(), clazz));
    }

    public Storage<?> getStorage(Identifier id) {
        return STORAGES.get(id).storage;
    }

    // XXX public?
    private List<Storage<?>> getStorages(@Nullable Class<?> type, @Nullable SideConfigType configType) {
        List<Storage<?>> list = new ArrayList<>();
        for (Entry<Identifier, StorageEntry<?>> entry : STORAGES.entrySet()) {
            StorageEntry<?> storage = entry.getValue();
            if (type == null || storage.clazz.equals(type) && configType == null || allows(entry.getKey(), configType)) list.add(storage.storage);
        }
        return list;
    }

    // XXX public?
    private boolean allows(Identifier id, SideConfigType type) {
        char[] config = STORAGES.get(id).config;
        if (config == null) {
            MooshroomLib.LOGGER.smallBug(new NoSuchElementException("No config was found for the id " + id.toString()));
            return false;
        }
        return Character.toUpperCase(config[type.ordinal()]) == 'T';
    }

    // XXX public?
    private static class StorageEntry<T> {
        private final Storage<T> storage;
        private final char[] config;
        private final Class<T> clazz;

        public StorageEntry(Storage<T> storage, char[] config, Class<T> clazz) {
            this.storage = storage;
            if (config.length != SideConfigType.values().length) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config char array should have a lenght of " + SideConfigType.values().length));
            this.config = config;
            this.clazz = clazz;
        }
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
