package de.einholz.ehmooshroom.storage.deprecated.providers;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

@Deprecated
public interface FluidStorageProv {
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction dir);
}
