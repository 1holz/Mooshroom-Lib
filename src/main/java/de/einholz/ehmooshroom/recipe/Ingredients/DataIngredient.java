package de.einholz.ehmooshroom.recipe.Ingredients;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;

//should this be called NbtIngredient?
@Deprecated
public class DataIngredient {
    public final NbtElement nbt;

    public DataIngredient(NbtElement nbt) {
        this.nbt = nbt;
    }

    public boolean matches(NbtElement nbt) {
        return NbtHelper.matches(this.nbt, nbt, true);
    }

    public void write(PacketByteBuf buf) {
        NbtCompound NbtCompound = new NbtCompound();
        NbtCompound.put("indata", nbt);
        buf.writeNbt(NbtCompound);
    }

    public static DataIngredient read(PacketByteBuf buf) {
        return new DataIngredient(buf.readNbt().get("indata"));
    }
}
