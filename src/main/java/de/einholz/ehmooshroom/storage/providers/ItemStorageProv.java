package de.einholz.ehmooshroom.storage.providers;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface ItemStorageProv {
    public Storage<ItemVariant> getItemStorage();
}
