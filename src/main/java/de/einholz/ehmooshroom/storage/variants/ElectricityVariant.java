package de.einholz.ehmooshroom.storage.variants;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class ElectricityVariant extends SingletonVariant {
    public static final ElectricityVariant INSTANCE = new ElectricityVariant();

    private ElectricityVariant() {}

    public static ElectricityVariant fromNbt(NbtCompound nbt) {
        return INSTANCE;
    }

    public static ElectricityVariant fromPacket(PacketByteBuf buf) {
        return INSTANCE;
    }
}
