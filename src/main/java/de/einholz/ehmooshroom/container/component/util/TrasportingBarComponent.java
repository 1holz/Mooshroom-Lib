package de.einholz.ehmooshroom.container.component.util;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.nbt.NbtCompound;

public interface TrasportingBarComponent<C extends TrasportingBarComponent<C>> extends BarComponent, TransportingComponent<C> {
    @Override
    default Number transport(C from, C to) {
        float transfer = Math.min((float) from.getMaxTransfer(), (float) to.getMaxTransfer());
        transfer = from.decrease(transfer);
        if (from.increase(transfer - to.increase(transfer)) >= 1.0F) MooshroomLib.LOGGER.smallBug(new Throwable("Transportation for " + getId().toString() + " had big losses."));
        return transfer;
    }
    
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
