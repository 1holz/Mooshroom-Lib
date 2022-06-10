package de.einholz.ehmooshroom.recipes.Ingrediets;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemIngredient {
    public final Identifier id;
    public final TagKey<Item> ingredient;
    public final int amount;
    public final NbtCompound nbt;

    public ItemIngredient(Identifier id, int amount, NbtCompound nbt) {
        this.id = id;
        this.ingredient = ServerTagManagerHolder.getTagManager().getItems().getTag(id);
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(ItemStack stack) {
        return ingredient.contains(stack.getItem()) && NbtHelper.matches(nbt, stack.getTag(), true);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id).writeInt(amount);
        if (nbt == null || nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
    }

    public static ItemIngredient read(PacketByteBuf buf) {
        return new ItemIngredient(buf.readIdentifier(), buf.readInt(), buf.readBoolean() ? buf.readNbt() : new NbtCompound());
    }
}
