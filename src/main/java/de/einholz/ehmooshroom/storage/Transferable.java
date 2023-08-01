package de.einholz.ehmooshroom.storage;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.registry.TransferableRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class Transferable<T, V extends TransferVariant<T>> {
    private Identifier id;
    private final Registry<T> registry;
    @Nullable
    private final BlockApiLookup<? extends Storage<V>, Direction> lookup;

    public Transferable(Registry<T> registry, @Nullable BlockApiLookup<? extends Storage<V>, Direction> lookup) {
        this.registry = registry;
        this.lookup = lookup;
    }

    public void setId(Identifier id) {
        if (this.id == null)
            this.id = id;
    }

    public Identifier getId() {
        setId(TransferableRegistry.TRANSFERABLE.getId(this));
        return id;
    }

    public Registry<T> getRegistry() {
        return registry;
    }

    @Nullable
    public BlockApiLookup<? extends Storage<V>, Direction> getLookup() {
        return lookup;
    }

    public boolean isTransferable() {
        return lookup != null/* && variantType != null */;
    }

    public boolean isProcessable() {
        return true; // TODO
    }
}
