package de.einholz.ehmooshroom.util;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializable {
    public NbtCompound writeNbt(NbtCompound nbt);
    public void readNbt(NbtCompound nbt);
}
