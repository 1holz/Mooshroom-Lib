package de.alberteinholz.ehmooshroom.registry;

import java.util.HashMap;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class ItemRegistryHelper {
    public static final HashMap<Identifier, ItemRegistryEntry> ITEMS = new HashMap<>();
    public Item defaultItem;

    public ItemRegistryHelper() {
        this(null);
    }

    public ItemRegistryHelper(Item defaultItem) {
        this.defaultItem = defaultItem;
    }

    public static ItemRegistryEntry create(Identifier id) throws NullPointerException {
        if (id == null) {
            NullPointerException e = new NullPointerException("Skiping creation of ItemRegistryEntry with null id.");
            MooshroomLib.LOGGER.smallBug(e);
            throw e;
        }
        ItemRegistryEntry entry = ITEMS.containsKey(id) ? ITEMS.get(id) : new ItemRegistryEntry(id);
        ITEMS.putIfAbsent(id, entry);
        return entry;
    }
}