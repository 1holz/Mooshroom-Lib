package de.einholz.ehmooshroom.storage.variants;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class HeatVariant extends SingletonVariant {
    public static final HeatVariant INSTANCE = new HeatVariant();

    private HeatVariant() {}

    public static HeatVariant fromNbt(NbtCompound nbt) {
        return INSTANCE;
    }

    public static HeatVariant fromPacket(PacketByteBuf buf) {
        return INSTANCE;
    }
}
