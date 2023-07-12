package de.einholz.ehmooshroom.storage.deprecated;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

// TODO del if unneeded
@Deprecated
public class CustomItemVariant implements ItemVariant {

    @Override
    public @Nullable NbtCompound getNbt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Item getObject() {
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
