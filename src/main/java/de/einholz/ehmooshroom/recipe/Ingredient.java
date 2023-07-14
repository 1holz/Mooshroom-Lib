package de.einholz.ehmooshroom.recipe;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.registry.TransferablesReg;
import de.einholz.ehmooshroom.storage.Transferable;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;

// XXX anotate constructor with only server?
public class Ingredient<T> {
    private final Transferable<T, ? extends TransferVariant<T>> type;
    @Nullable
    private final Identified<T> tag;
    @Nullable
    private final NbtCompound nbt;
    private final long amount;

    @SuppressWarnings({ "unchecked", "null" })
    public Ingredient(Identifier typeId, @Nullable Identifier tagId, @Nullable NbtCompound nbt, long amount) {
        this.type = TransferablesReg.TRANSFERABLE.get(typeId);
        this.tag = tagId != null ? (Identified<T>) type.getTagFactory().create(tagId) : null;
        this.nbt = nbt == null ? new NbtCompound() : nbt;
        this.amount = amount;
    }

    public Ingredient(Identifier type, @Nullable NbtCompound nbt, long amount) {
        this(type, null, nbt, amount);
    }

    public Ingredient(Identifier type, @Nullable Identifier tagId, long amount) {
        this(type, tagId, null, amount);
    }

    public Ingredient(Identifier type, long amount) {
        this(type, null, null, amount);
    }

    public static Ingredient<?> read(PacketByteBuf buf) {
        Identifier type = buf.readIdentifier();
        boolean bl = buf.readBoolean();
        return new Ingredient<>(type, bl ? buf.readIdentifier() : null, buf.readBoolean() ? buf.readNbt() : null,
                buf.readVarLong());
    }

    @SuppressWarnings("null")
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(type.getId());
        if (tag == null)
            buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeIdentifier(tag.getId());
        }
        if (nbt == null || nbt.isEmpty())
            buf.writeBoolean(false);
        else {
            buf.writeBoolean(true);
            buf.writeNbt(nbt);
        }
        buf.writeNbt(nbt).writeVarLong(amount);
    }

    @SuppressWarnings({ "null", "unchecked" })
    public boolean matches(TransferVariant<?> test) {
        if (type == null && tag == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException(
                    "Attempted to perform match test on Ingredient with null type and tag. This Ingredient will be skiped!"));
            return true;
        }
        if (!NbtHelper.matches(test.copyNbt(), nbt, true))
            return false;
        if (tag == null)
            return type.getVariantType().equals(test.getClass());
        return tag.contains((T) test.getObject());
    }

    public Transferable<T, ? extends TransferVariant<T>> getType() {
        return type;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    public long getAmount() {
        return amount;
    }
}
