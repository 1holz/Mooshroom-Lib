package de.einholz.ehmooshroom.storage.transferable;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;

public abstract class NbtlessVariant<T> implements TransferVariant<T> {
    @Override
	@Nullable
	public NbtCompound getNbt() {
        return null;
    }

    @Override
	public boolean hasNbt() {
		return false;
	}
    
    @Override
	@Nullable
	public NbtCompound copyNbt() {
		return null;
	}
}
