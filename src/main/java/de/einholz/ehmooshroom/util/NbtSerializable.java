package de.einholz.ehmooshroom.util;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializable {
    default NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }

    default void readNbt(NbtCompound nbt) {}
}
