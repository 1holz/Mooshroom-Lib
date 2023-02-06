package de.einholz.ehmooshroom.storage.transferable;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.registry.TransferablesReg;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class Transferable<T/*, V extends TransferVariant<T>*/> {
    private Identifier id;
    private final Class<T> storedType;
    //@Deprecated
    //public final Class<V> variantType;
    @Nullable
    public final BlockApiLookup<? extends Storage<T>, Direction> lookup;

    public Transferable(Class<T> storedType, /*Class<? extends TransferVariant<T>> variantType, */BlockApiLookup<? extends Storage<T>, Direction> lookup) {
        this.storedType = storedType;
        //this.variantType = variantType;
        this.lookup = lookup;
    }

    public void setId(Identifier id) {
        if (this.id != null) this.id = id;
    }

    public Identifier getId() {
        return id == null ? TransferablesReg.TRANSFERABLE.getId(this) : id;
    }

    public Class<T> getStoredType() {
        return storedType;
    }

    public boolean isTransferable() {
        return lookup != null/* && variantType != null*/;
    }

    public boolean isProcessable() {
        return true; // TODO
    }
}
