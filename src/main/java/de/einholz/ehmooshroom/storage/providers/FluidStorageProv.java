package de.einholz.ehmooshroom.storage.providers;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

public interface FluidStorageProv {
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction dir);
}
