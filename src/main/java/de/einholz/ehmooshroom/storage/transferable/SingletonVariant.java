package de.einholz.ehmooshroom.storage.transferable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public abstract class SingletonVariant extends NbtlessVariant<Void> {
    @Override
    public Void getObject() {
        return null;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    @Override
    public NbtCompound toNbt() {
        return new NbtCompound();
    }

    @Override
    public void toPacket(PacketByteBuf buf) {}
}