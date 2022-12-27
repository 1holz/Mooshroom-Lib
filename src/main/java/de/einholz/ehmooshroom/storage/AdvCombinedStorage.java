package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.storage.SidedStorageManager.SideConfigType;
import de.einholz.ehmooshroom.storage.SidedStorageManager.StorageEntry;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.math.Direction;

// XXX is this needed?
public class AdvCombinedStorage<T, S extends Storage<T>> extends CombinedStorage<T, S> {
    @Nullable
    private final Direction dir;
    private final boolean blockInsertion;
    private final boolean blockExtraction;

    @SuppressWarnings("unchecked")
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, @Nullable Direction dir, List<StorageEntry<S>> parts) {
        super((List<S>) extractStorages(parts));
        this.dir = dir;
        this.blockInsertion = blockInsertion;
        this.blockExtraction = blockExtraction;
    }

    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, List<StorageEntry<S>> parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    public AdvCombinedStorage(@Nullable Direction dir, List<StorageEntry<S>> parts) {
        this(false, false, dir, parts);
    }

    public AdvCombinedStorage(List<StorageEntry<S>> parts) {
        this(null, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, @Nullable Direction dir, StorageEntry<S>... parts) {
        this(blockInsertion, blockExtraction, dir, Arrays.asList(parts));
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, StorageEntry<S>... parts) {
        this(blockInsertion, blockExtraction, null, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(@Nullable Direction dir, StorageEntry<S>... parts) {
        this(false, false, dir, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(StorageEntry<S>... parts) {
        this(null, parts);
    }

    private static <T> List<Storage<T>> extractStorages(List<StorageEntry<T>> list) {
        List<Storage<T>> newList = new ArrayList<>();
        for (StorageEntry<T> entry : list) {
            newList.add(entry.storage);
        }
        return newList;
    }

    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        if (dir == null) return super.insert(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (S part : parts) {
			amount += part.insert(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        if (dir == null)  return super.extract(resource, maxAmount, transaction);
		StoragePreconditions.notNegative(maxAmount);
		long amount = 0;
		for (S part : parts) {
			amount += part.extract(resource, maxAmount - amount, transaction);
			if (amount == maxAmount) break;
		}
		return amount;
    }

    @Override
    public boolean supportsInsertion() {
        if (blockInsertion) return false;
        if (dir == null) return super.supportsInsertion();
        for (S s : parts) if (s instanceof StorageEntry<?> entry) {
            if (entry.allows(SideConfigType.getFromParams(true, false, dir))) return true;
        }
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        if (blockExtraction) return false;
        if (dir == null) return super.supportsExtraction();
        for (S s : parts) if (s instanceof StorageEntry<?> entry) {
            if (entry.allows(SideConfigType.getFromParams(true, true, dir))) return true;
        }
        return false;
    }
}
