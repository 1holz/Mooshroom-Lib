package de.einholz.ehmooshroom.recipes.Ingrediets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

public class EntityIngredient {
    public final Identifier id;
    public final TagKey<EntityType<?>> ingredient;
    public final int amount;
    public final NbtCompound nbt;

    public EntityIngredient(Identifier id, int amount, NbtCompound nbt) {
        this.id = id;
        this.ingredient = ServerTagManagerHolder.getTagManager().getEntityTypes().getTag(id);
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(Entity entity) {
        return ingredient.contains(entity.getType()) && NbtHelper.matches(nbt, entity.writeNbt(new NbtCompound()), true);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id).writeInt(amount);
        if (nbt == null || nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
    }

    public static EntityIngredient read(PacketByteBuf buf) {
        return new EntityIngredient(buf.readIdentifier(), buf.readInt(), buf.readBoolean() ? buf.readNbt() : new NbtCompound());
    }
}
