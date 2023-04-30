package de.einholz.ehmooshroom.storage.transferable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class HeatVariant extends NbtlessVariant<Void> {
    public static final HeatVariant INSTANCE = new HeatVariant();

    private HeatVariant() {}

    @Override
    public Void getObject() {
        return null;
    }

    @Override
    public boolean isBlank() {
        return false;
    }

    public static HeatVariant fromNbt(NbtCompound nbt) {
        return INSTANCE;
    }

    @Override
    public NbtCompound toNbt() {
        return new NbtCompound();
    }

    public static HeatVariant fromPacket(PacketByteBuf buf) {
        return INSTANCE;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {}
}
