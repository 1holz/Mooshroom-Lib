package de.einholz.ehmooshroom.storage.transferable;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EntityVariant extends NbtVariant<EntityType<?>> {
    private final EntityType<?> entityType;

    public EntityVariant(EntityType<?> entityType, @Nullable NbtCompound nbt) {
        super(entityType, nbt);
        this.entityType = entityType;
    }

    @Override
    public boolean isBlank() {
        return entityType == null;
    }

    @Override
    public EntityType<?> getObject() {
        return entityType;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound to = new NbtCompound();
        to.putString("entityType", Registry.ENTITY_TYPE.getId(getObject()).toString());
        if (getNbt() != null)
            to.put("nbt", copyNbt());
        return to;
    }

    public static EntityVariant fromNbt(final NbtCompound from) {
        final EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(from.getString("entityType")));
        return new EntityVariant(entityType, from.getCompound("nbt"));
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        buf.writeIdentifier(Registry.ENTITY_TYPE.getId(getObject()));
        buf.writeNbt(copyNbt());
    }

    public static EntityVariant fromPacket(PacketByteBuf buf) {
        return new EntityVariant(Registry.ENTITY_TYPE.get(buf.readIdentifier()), buf.readNbt());
    }
}
