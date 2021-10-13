package de.einholz.ehmooshroom.container.component.util;

import net.minecraft.nbt.NbtCompound;

public interface TrasportingBarComponent<C extends TransportingComponent<?, T>, T> extends BarComponent, TransportingComponent<C, T> {
    @Override
    default void writeNbt(NbtCompound nbt) {
        BarComponent.super.writeNbt(nbt);
        TransportingComponent.super.writeNbt(nbt);
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        BarComponent.super.readNbt(nbt);
        TransportingComponent.super.readNbt(nbt);
    }
}
