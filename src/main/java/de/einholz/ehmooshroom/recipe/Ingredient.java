package de.einholz.ehmooshroom.recipe;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

// XXX: anotate constructor with only server?
public class Ingredient<T> {
    private final Transferable<T> type;
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
    public Ingredient(Identifier type, @Nullable Identifier tagRegId, @Nullable String dataType, @Nullable Identifier tagId, @Nullable NbtCompound nbt, long amount) {
        this.type = TransferablesReg.TRANSFERABLE.get(type);
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

    public Ingredient(Identifier type, @Nullable NbtCompound nbt, long amount) {
        this(type, null, null, null, nbt, amount);
    }

    public Ingredient(Identifier type, @Nullable Identifier tagRegId, @Nullable String dataType, @Nullable Identifier tagId, long amount) {
        this(type, tagRegId, dataType, tagId, null, amount);
    }

    public Ingredient(Identifier type, long amount) {
        this(type, null, null, null, amount);
    }

    public static Ingredient<?> read(PacketByteBuf buf) {
        Identifier type = buf.readIdentifier();
        boolean bl = buf.readBoolean();
        return new Ingredient<>(type, bl ? buf.readIdentifier() : null, bl ? buf.readString() : null, bl ? buf.readIdentifier() : null, buf.readBoolean() ? buf.readNbt() : null, buf.readVarLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(type.getId());
        if (tag == null) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeIdentifier(tagRegId).writeString(dataType).writeIdentifier(tag.getId());
        }
        if (nbt.isEmpty()) buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
        buf.writeNbt(nbt).writeVarLong(amount);
    }

    public boolean matches(T test, NbtCompound testNbt) {
        if (type == null && tag == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("Attempted to perform match test on Ingredient with null type and tag. This Ingredient will be skiped!"));
            return true;
        }
        if (!NbtHelper.matches(nbt, testNbt, true)) return false;
        if (tag == null) return type.getStoredType().equals(test);
        return tag.contains(test);
    }

    public Transferable<T> getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }
}
