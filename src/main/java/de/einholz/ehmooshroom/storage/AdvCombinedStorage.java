package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.storage.SidedStorageMgr.SideConfigType;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.StorageEntry;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.math.Direction;

public class AdvCombinedStorage<T, S extends Storage<T>> extends CombinedStorage<T, S> {
    private final List<StorageEntry<T>> entries;
    @Nullable
    private final Direction dir;
    private final boolean blockInsertion;
    private final boolean blockExtraction;

    @SuppressWarnings("unchecked")
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, @Nullable Direction dir, List<StorageEntry<T>> parts) {
        super((List<S>) extractStorages(parts));
        this.entries = parts;
        this.dir = dir;
        this.blockInsertion = blockInsertion;
        this.blockExtraction = blockExtraction;
    }

    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, List<StorageEntry<T>> parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    public AdvCombinedStorage(@Nullable Direction dir, List<StorageEntry<T>> parts) {
        this(false, false, dir, parts);
    }

    public AdvCombinedStorage(List<StorageEntry<T>> parts) {
        this(null, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, @Nullable Direction dir, StorageEntry<T>... parts) {
        this(blockInsertion, blockExtraction, dir, Arrays.asList(parts));
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, StorageEntry<T>... parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(@Nullable Direction dir, StorageEntry<T>... parts) {
        this(false, false, dir, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(StorageEntry<T>... parts) {
        this(null, parts);
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        if (dir == null) return super.insert(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (StorageEntry<T> entry : entries) {
			amount += entry.storage.insert(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        if (dir == null)  return super.extract(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (StorageEntry<T> entry : entries) {
			amount += entry.storage.extract(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }

    @Override
    public boolean supportsInsertion() {
        if (blockInsertion) return false;
        if (dir == null) return super.supportsInsertion();
        for (StorageEntry<T> entry : entries) {
            if (entry.allows(SideConfigType.getFromParams(true, false, dir))) return entry.storage.supportsInsertion();
        }
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        if (blockExtraction) return false;
        if (dir == null) return super.supportsExtraction();
        for (StorageEntry<T> entry : entries) {
            if (entry.allows(SideConfigType.getFromParams(true, true, dir))) return entry.storage.supportsExtraction();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Storage<T>> List<S> extractStorages(List<StorageEntry<T>> list) {
        List<S> newList = new ArrayList<>();
        for (StorageEntry<T> entry : list) newList.add((S) entry.storage);
        return newList;
    }
}
