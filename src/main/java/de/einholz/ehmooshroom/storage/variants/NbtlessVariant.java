package de.einholz.ehmooshroom.storage.variants;

import java.util.Objects;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

public abstract class NbtlessVariant<T> implements TransferVariant<T> {
    private final int hash;

    public NbtlessVariant(T obj) {
        hash = Objects.hash(obj);
    }

    @Override
    @Nullable
    public NbtCompound getNbt() {
        return null;
    }

    @Override
    public boolean hasNbt() {
        return false;
    }

    @Override
    @Nullable
    public NbtCompound copyNbt() {
        return null;
    }

    /*
     * WTF is going on with fabrics ItemVariantImpl.equals(…) ???
     * why is ItemVariant uppercase when it is a variable
     * shouldn't the first && in the return line be a || instead?
     * TODO check if present in more recent versions and report as bug
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (getClass().isInstance(obj))
            return hash == ((NbtlessVariant<?>) obj).hash || getObject().equals(((NbtlessVariant<?>) obj).getObject());
        return false;
    }
}
