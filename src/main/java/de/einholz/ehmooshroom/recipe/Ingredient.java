package de.einholz.ehmooshroom.recipe;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

// XXX: anotate constructor with only server?
public class Ingredient<T> {
    private final Class<T> type;
    @Nullable 
    private final Identifier tagRegId;
    @Nullable 
    private final String dataType;
    @Nullable 
    private final Identified<T> tag;
    @Nullable
    private final NbtCompound nbt;
    private final long amount;

    @SuppressWarnings("unchecked")
    public Ingredient(String type, @Nullable Identifier tagRegId, @Nullable String dataType, @Nullable Identifier tagId, @Nullable NbtCompound nbt, long amount) {
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz == null) MooshroomLib.LOGGER.bigBug(new NullPointerException("Ingredient with null type was created. tagRegId: " + tagRegId == null ? "null" : tagRegId.toString() + " dataType: " + dataType == null ? "null" : dataType + " tagId: " + tagId == null ? "null" : tagId.toString() + " nbt: " + nbt == null ? "null" : nbt.asString() + " amount: " + amount));
        this.type = clazz;
        if (tagRegId != null && dataType != null && tagId != null) {
            this.tagRegId = tagRegId;
            this.dataType = dataType;
            tag = (Identified<T>) TagFactory.of(RegistryKey.ofRegistry(tagRegId), dataType).create(tagId);
        } else {
            this.tagRegId = null;
            this.dataType = "";
            tag = null;
        }
        this.nbt = nbt == null ? new NbtCompound() : nbt;
        this.amount = amount;
    }

    public Ingredient(String type, @Nullable NbtCompound nbt, long amount) {
        this(type, null, null, null, nbt, amount);
    }

    public Ingredient(String type, @Nullable Identifier tagRegId, @Nullable String dataType, @Nullable Identifier tagId, long amount) {
        this(type, tagRegId, dataType, tagId, null, amount);
    }

    public Ingredient(String type, long amount) {
        this(type, null, null, null, amount);
    }

    public static Ingredient<?> read(PacketByteBuf buf) {
        String type = buf.readString();
        boolean bl = buf.readBoolean();
        return new Ingredient<>(type, bl ? null : buf.readIdentifier(), bl ? null : buf.readString(), bl ? null : buf.readIdentifier(), buf.readBoolean() ? new NbtCompound() : buf.readNbt(), buf.readLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(type.getName());
        if (tag == null) {
            buf.writeBoolean(false);
            buf.writeIdentifier(tagRegId).writeString(dataType).writeIdentifier(tag.getId());
        } else buf.writeBoolean(false);
        if (nbt.isEmpty()) {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
        else buf.writeBoolean(false);
        buf.writeNbt(nbt).writeLong(amount);
    }

    public boolean matches(T test, NbtCompound testNbt) {
        if (type == null && tag == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("Attempted to perform match test on Ingredient with null type and tag. This Ingredient will be skiped!"));
            return true;
        }
        if (!NbtHelper.matches(nbt, testNbt, true)) return false;
        if (tag == null) return type.isAssignableFrom(test.getClass());
        return tag.contains(test);
    }

    public Class<T> getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }
}
