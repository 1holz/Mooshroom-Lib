package de.einholz.ehmooshroom.container.component.util;

import net.minecraft.nbt.NbtCompound;

public interface TrasportingBarComponent<C extends TransportingComponent<?, T>, T> extends BarComponent, TransportingComponent<C, T> {
    @Override
    default void writeNbt(NbtCompound tag) {
        tag.putFloat("Cur", getCur());
        tag.putDouble("MaxTransfer", getMaxTransfer().doubleValue());
    }

    @Override
    default void readNbt(NbtCompound tag) {
        setCur(tag.getFloat("Cur"));
        setMaxTransfer(tag.getDouble("MaxTransfer"));
    }
}
