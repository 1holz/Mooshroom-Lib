package de.einholz.ehmooshroom.storage.transferable;

import java.util.Objects;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

public abstract class NbtVariant<T> implements TransferVariant<T> {
    private final @Nullable NbtCompound nbt;
    private final int hash;

    protected NbtVariant(T obj, NbtCompound nbt) {
        this.nbt = nbt == null ? null : nbt.copy();
        hash = Objects.hash(obj);
    }

    @Override
    @Nullable
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public boolean hasNbt() {
        return getNbt() != null || getNbt().isEmpty();
    }

    @Override
    @Nullable
    public NbtCompound copyNbt() {
        NbtCompound nbt = getNbt();
        return nbt == null ? null : nbt.copy();
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
            return hash == ((NbtVariant<?>) obj).hash || getObject().equals(((NbtVariant<?>) obj).getObject())
                    && nbtMatches(((NbtVariant<?>) obj).getNbt());
        return false;
    }
}
