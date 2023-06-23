package de.einholz.ehmooshroom.storage.deprecated.providers;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

@Deprecated
public interface ItemStorageProv {
    public Storage<ItemVariant> getItemStorage(@Nullable Direction dir);
}
