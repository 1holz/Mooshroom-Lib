package de.einholz.ehmooshroom.storage.transferable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class ElectricityVariant extends NbtlessVariant<Void> {
    public static final ElectricityVariant INSTANCE = new ElectricityVariant();

    private ElectricityVariant() {}

    @Override
    public Void getObject() {
        return null;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    public static ElectricityVariant fromNbt(NbtCompound nbt) {
        return INSTANCE;
    }

    @Override
    public NbtCompound toNbt() {
        return new NbtCompound();
    }

    public static ElectricityVariant fromPacket(PacketByteBuf buf) {
        return INSTANCE;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {}
}
