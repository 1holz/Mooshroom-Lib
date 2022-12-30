package de.einholz.ehmooshroom.recipe.Ingrediets;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class Ingredient<T, U> {
    private final String type;
    @Nullable
    private final Identifier id;
    @Nullable
    private final Tag<T> tag;
    private final long amount;
    private final NbtCompound nbt;

    public Ingredient(String type, @Nullable Identifier id, @Nullable Tag<T> tag, long amount, NbtCompound nbt) {
        this.type = type;
        this.id = id;
        this.tag = tag;
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(U test) {
        return false;
    }

    public void write(PacketByteBuf buf) {

    }

    public static ItemIngredient read(PacketByteBuf buf) {
        return null;
    }
}
