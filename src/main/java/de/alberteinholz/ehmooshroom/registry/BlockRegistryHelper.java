package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import net.minecraft.util.Identifier;

public class BlockRegistryHelper {
    public static final HashMap<Identifier, BlockRegistryEntry> BLOCKS = new HashMap<>();

    public static BlockRegistryEntry getEntry(Identifier id) {
        return BLOCKS.get(id);
    }

    public static BlockRegistryEntry[] makeCompressed(Identifier id, int size, int baseIndex, BlockRegistryEntry belowBaseTemplate, BlockRegistryEntry baseTemplate, BlockRegistryEntry overBaseTemplate) {
        BlockRegistryEntry[] result = new BlockRegistryEntry[size];
        for (int i = 0; i < size; i++) {
            //TODO: something with autoid id = new Identifier(id.getNamespace(), i == baseIndex ? )
        }
        return result;
    }

    public static BlockRegistryEntry create(Identifier id) {
        BlockRegistryEntry entry = id == null || !BLOCKS.containsKey(id) ? new BlockRegistryEntry(id) : BLOCKS.get(id);
        if (id != null) BLOCKS.putIfAbsent(id, entry);
        return entry;
    }
}