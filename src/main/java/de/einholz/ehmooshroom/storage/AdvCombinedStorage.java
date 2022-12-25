package de.einholz.ehmooshroom.storage;

import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;

// XXX is this needed?
public class AdvCombinedStorage<T, S extends Storage<T>> extends CombinedStorage<T, S> {
    private final boolean blockInsertion;
    private final boolean blockExtraction;

    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, List<S> parts) {
        super(parts);
        this.blockInsertion = blockInsertion;
        this.blockExtraction = blockExtraction;
    }

    public AdvCombinedStorage(List<S> parts) {
        this(false, false, parts);
    }

    @SafeVarargs
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, S... parts) {
        this(blockInsertion, blockExtraction, Arrays.asList(parts));
    }

    @SafeVarargs
    public AdvCombinedStorage(S... parts) {
        this(false, false, parts);
    }

    @Override
    public boolean supportsInsertion() {
        return blockInsertion ? false : super.supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return blockExtraction ? false : super.supportsExtraction();
    }
}
