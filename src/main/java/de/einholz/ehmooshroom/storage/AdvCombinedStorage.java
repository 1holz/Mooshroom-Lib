package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.List;

import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;

public class AdvCombinedStorage<T, V extends TransferVariant<T>, S extends Storage<V>> extends CombinedStorage<V, S> {
    private final List<StorageEntry<T, V>> entries;
    private final SideConfigAccessor acc;
    private final boolean blockInsertion;
    private final boolean blockExtraction;

    @SuppressWarnings("unchecked")
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, SideConfigAccessor acc, List<StorageEntry<T, V>> parts) {
        super((List<S>) extractStorages(parts));
        this.entries = parts;
        this.acc = acc;
        this.blockInsertion = blockInsertion;
        this.blockExtraction = blockExtraction;
    }

    public AdvCombinedStorage(SideConfigAccessor acc, List<StorageEntry<T, V>> parts) {
        this(false, false, acc, parts);
    }

    /* TODO del if not needed
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, List<StorageEntry<T, U>> parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    public AdvCombinedStorage(@Nullable Direction dir, List<StorageEntry<T, U>> parts) {
        this(false, false, dir, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, @Nullable Direction dir, StorageEntry<T, U>... parts) {
        this(blockInsertion, blockExtraction, dir, Arrays.asList(parts));
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, StorageEntry<T, U>... parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(@Nullable Direction dir, StorageEntry<T, U>... parts) {
        this(false, false, dir, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(StorageEntry<T, U>... parts) {
        this(null, parts);
    }

    @Override
    public long insert(U resource, long maxAmount, TransactionContext transaction) {
        if (dir == null) return super.insert(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (StorageEntry<T, U> entry : entries) {
			amount += entry.storage.insert(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }

    @Override
    public long extract(U resource, long maxAmount, TransactionContext transaction) {
        if (dir == null) return super.extract(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (StorageEntry<T, U> entry : entries) {
			amount += entry.storage.extract(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }
    */

    @Override
    public boolean supportsInsertion() {
        if (blockInsertion) return false;
        for (StorageEntry<T, V> entry : entries)
            if (entry.allows(SideConfigType.getFromParams(true, false, acc)))
            return entry.storage.supportsInsertion();
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        if (blockExtraction) return false;
        for (StorageEntry<T, V> entry : entries)
            if (entry.allows(SideConfigType.getFromParams(true, true, acc)))
            return entry.storage.supportsExtraction();
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends TransferVariant<T>, S extends Storage<V>> List<S> extractStorages(List<StorageEntry<T, V>> list) {
        List<S> newList = new ArrayList<>();
        for (StorageEntry<T, V> entry : list) newList.add((S) entry.storage);
        return newList;
    }
}
