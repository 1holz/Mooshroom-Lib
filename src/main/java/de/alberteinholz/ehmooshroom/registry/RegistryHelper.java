package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.minecraft.util.Identifier;

public class RegistryHelper {
    protected static final HashMap<Identifier, RegistryEntry> ENTRIES = new HashMap<>();

    public static RegistryEntry getEntry(Identifier id) {
        return ENTRIES.get(id);
    }

    public static RegistryEntry[] makeCompresseable(Identifier id, int size, int baseIndex, RegistryEntry belowBaseTemplate, RegistryEntry baseTemplate, RegistryEntry overBaseTemplate) {
        RegistryEntry[] result = new RegistryEntry[size];
        for (int i = 0; i < size; i++) {
            //TODO: something with autoid id = new Identifier(id.getNamespace(), i == baseIndex ? )
        }
        return result;
    }

    public static RegistryEntry create(Identifier id) {
        if (id == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("Skipping RegistryEntry with null id"));
            return null;
        }
        RegistryEntry entry = ENTRIES.containsKey(id) ? ENTRIES.get(id) : new RegistryEntry(id);
        ENTRIES.putIfAbsent(id, entry);
        return entry;
    }
}