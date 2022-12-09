package de.einholz.ehmooshroom.storage.providers;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface FluidStorageProv {
    public Storage<FluidVariant> getFluidStorage();
}
