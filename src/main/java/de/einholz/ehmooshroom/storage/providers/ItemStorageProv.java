package de.einholz.ehmooshroom.storage.providers;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

public interface ItemStorageProv {
    public Storage<ItemVariant> getItemStorage(@Nullable Direction dir);
}
