package de.einholz.ehmooshroom.util;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializable {
    abstract void writeNbt(NbtCompound nbt);

    abstract void readNbt(NbtCompound nbt);
}
