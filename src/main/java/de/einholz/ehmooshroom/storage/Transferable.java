package de.einholz.ehmooshroom.storage;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.registry.TransferablesReg;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class Transferable<T, V extends TransferVariant<T>> {
    private Identifier id;
    private final Class<V> variantType;
    @Nullable
    private final TagFactory<T> tagFactory;
    @Nullable
    private final BlockApiLookup<? extends Storage<V>, Direction> lookup;

    public Transferable(final Class<V> variantType, final @Nullable TagFactory<T> tagFactory,
            final @Nullable BlockApiLookup<? extends Storage<V>, Direction> lookup) {
        this.variantType = variantType;
        this.tagFactory = tagFactory;
        this.lookup = lookup;
    }

    public void setId(Identifier id) {
        if (this.id == null)
            this.id = id;
    }

    public Identifier getId() {
        setId(TransferablesReg.TRANSFERABLE.getId(this));
        return id;
    }

    public Class<V> getVariantType() {
        return variantType;
    }

    @Nullable
    public TagFactory<T> getTagFactory() {
        return tagFactory;
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
