package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import net.minecraft.util.Identifier;

public class ItemRegistryHelper {
    public static final HashMap<Identifier, ItemRegistryEntry> ITEMS = new HashMap<>();
    
    public static ItemRegistryEntry get(Identifier id) {
        return ITEMS.get(id);
    }

    public static ItemRegistryEntry create(Identifier id) throws NullPointerException {
        ItemRegistryEntry entry = id == null || !ITEMS.containsKey(id) ? new ItemRegistryEntry(id) : ITEMS.get(id);
        if (id != null) ITEMS.putIfAbsent(id, entry);
        return entry;
    }
}