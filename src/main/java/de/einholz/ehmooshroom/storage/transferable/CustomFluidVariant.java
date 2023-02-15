package de.einholz.ehmooshroom.storage.transferable;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

@Deprecated
public class CustomFluidVariant implements FluidVariant {

    @Override
    public @Nullable NbtCompound getNbt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Fluid getObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBlank() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public NbtCompound toNbt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void toPacket(PacketByteBuf arg0) {
        // TODO Auto-generated method stub
        
    }
    
}
