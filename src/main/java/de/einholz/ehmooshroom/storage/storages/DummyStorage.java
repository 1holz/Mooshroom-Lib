package de.einholz.ehmooshroom.storage.storages;

import java.util.Collections;
import java.util.Iterator;

import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;

public class DummyStorage<T> implements Storage<T>, NbtSerializable {
    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public Iterator<StorageView<T>> iterator(TransactionContext transaction) {
        return Collections.emptyIterator();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
    }

    @Override
    public void readNbt(NbtCompound nbt) {
    }
}
