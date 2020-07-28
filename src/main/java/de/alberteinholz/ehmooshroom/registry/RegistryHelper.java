package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import net.minecraft.util.Identifier;

public class RegistryHelper {
    public static final HashMap<Identifier, RegistryEntry> BLOCKS = new HashMap<>();

    public static RegistryEntry getEntry(Identifier id) {
        return BLOCKS.get(id);
    }

    public static RegistryEntry[] makeCompressed(Identifier id, int size, int baseIndex, RegistryEntry belowBaseTemplate, RegistryEntry baseTemplate, RegistryEntry overBaseTemplate) {
        RegistryEntry[] result = new RegistryEntry[size];
        for (int i = 0; i < size; i++) {
            //TODO: something with autoid id = new Identifier(id.getNamespace(), i == baseIndex ? )
        }
        return result;
    }

    public static RegistryEntry create(Identifier id) {
        RegistryEntry entry = id == null || !BLOCKS.containsKey(id) ? new RegistryEntry(id) : BLOCKS.get(id);
        if (id != null) BLOCKS.putIfAbsent(id, entry);
        return entry;
    }
}