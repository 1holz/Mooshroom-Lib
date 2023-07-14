package de.einholz.ehmooshroom.storage;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.util.math.Direction;

public interface StorageProv {
    public <T, V extends TransferVariant<T>> Storage<V> getStorage(Transferable<T, V> trans, @Nullable Direction dir);
}
