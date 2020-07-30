package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import net.minecraft.util.Identifier;

public class RegistryHelper {
    protected static final HashMap<Identifier, RegistryEntry> ENTRIES = new HashMap<>();

    public static RegistryEntry getEntry(Identifier id) {
        return ENTRIES.get(id);
    }

    public static RegistryEntry[] makeCompressed(Identifier id, int size, int baseIndex, RegistryEntry belowBaseTemplate, RegistryEntry baseTemplate, RegistryEntry overBaseTemplate) {
        RegistryEntry[] result = new RegistryEntry[size];
        for (int i = 0; i < size; i++) {
            //TODO: something with autoid id = new Identifier(id.getNamespace(), i == baseIndex ? )
        }
        return result;
    }

    public static RegistryEntry create(Identifier id) {
        RegistryEntry entry = id == null || !ENTRIES.containsKey(id) ? new RegistryEntry(id) : ENTRIES.get(id);
        if (id != null) ENTRIES.putIfAbsent(id, entry);
        return entry;
    }
}