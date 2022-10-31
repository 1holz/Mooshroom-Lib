package de.einholz.ehmooshroom.recipes.Ingrediets;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidIngredient {
    public final Identifier id;
    public final Identified<Fluid> ingredient;
    public final float amount;
    public final NbtCompound nbt;

    public FluidIngredient(Identifier id, float amount, NbtCompound nbt) {
        this.id = id;
        this.ingredient = RequiredTagListRegistry.register(Registry.FLUID_KEY, "tags/fluids").add(id.toString());
        //this.ingredient = TagKey.of(Registry.FLUID_KEY, id);
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(FluidState state) {
        return state.isIn(ingredient);
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
