package de.einholz.ehmooshroom.recipes.Ingrediets;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FluidIngredient {
    public final Identifier id;
    public final Tag<Fluid> ingredient;
    public final float amount;
    public final NbtCompound nbt;

    public FluidIngredient(Identifier id, float amount, NbtCompound nbt) {
        this.id = id;
        this.ingredient = ServerTagManagerHolder.getTagManager().getFluids().getTag(id);
        this.amount = amount;
        this.nbt = nbt;
    }

    //TODO
    public boolean matches(Void fluid) {
        MooshroomLib.LOGGER.wip("Containment Check for " + id);
        return false;
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id).writeFloat(amount);
        if (nbt == null || nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
    }

    public static FluidIngredient read(PacketByteBuf buf) {
        return new FluidIngredient(buf.readIdentifier(), buf.readFloat(), buf.readBoolean() ? buf.readNbt() : new NbtCompound());
    }
}
