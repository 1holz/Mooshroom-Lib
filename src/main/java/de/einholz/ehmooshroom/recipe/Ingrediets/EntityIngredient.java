package de.einholz.ehmooshroom.recipe.Ingrediets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityIngredient {
    public final Identifier id;
    public final Identified<EntityType<?>> ingredient;
    public final int amount;
    public final NbtCompound nbt;

    public EntityIngredient(Identifier id, int amount, NbtCompound nbt) {
        this.id = id;
        this.ingredient = RequiredTagListRegistry.register(Registry.ENTITY_TYPE_KEY, "tags/entity_types").add(id.toString());
        //this.ingredient = TagKey.of(Registry.ENTITY_TYPE_KEY, id);
        this.amount = amount;
        this.nbt = nbt;
    }

    public boolean matches(Entity entity) {
        return entity.getType().isIn(ingredient) && NbtHelper.matches(nbt, entity.writeNbt(new NbtCompound()), true);
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
