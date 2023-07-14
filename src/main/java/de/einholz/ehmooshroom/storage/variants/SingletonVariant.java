package de.einholz.ehmooshroom.storage.variants;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public abstract class SingletonVariant extends NbtlessVariant<Void> {
    public SingletonVariant() {
        super(null);
    }

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
    public void toPacket(PacketByteBuf buf) {
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().isInstance(obj);
    }
}
