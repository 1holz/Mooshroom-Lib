package de.einholz.ehmooshroom.storage.transferable;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

public abstract class NbtVariant<T> implements TransferVariant<T> {
	private final @Nullable NbtCompound nbt;

    protected NbtVariant(NbtCompound nbt) {
        this.nbt = nbt == null ? null : nbt.copy();
    }

    @Override
	@Nullable
	public NbtCompound getNbt() {
        return nbt;
    }

    @Override
	public boolean hasNbt() {
		return getNbt() != null || getNbt().isEmpty();
	}

    @Override
	public boolean nbtMatches(@Nullable NbtCompound other) {
        return NbtHelper.matches(getNbt(), other, true);
	}
    
    @Override
	@Nullable
	public NbtCompound copyNbt() {
		NbtCompound nbt = getNbt();
		return nbt == null ? null : nbt.copy();
	}
}
