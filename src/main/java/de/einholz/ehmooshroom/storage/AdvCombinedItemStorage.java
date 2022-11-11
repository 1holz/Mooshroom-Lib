package de.einholz.ehmooshroom.storage;

import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;

public class AdvCombinedItemStorage extends CombinedStorage<ItemVariant, Storage<ItemVariant>> {
    @SafeVarargs
    public AdvCombinedItemStorage(Storage<ItemVariant>... parts) {
        this(Arrays.asList(parts));
    }

    public AdvCombinedItemStorage(List<Storage<ItemVariant>> parts) {
        super(parts);
    }
    
}
