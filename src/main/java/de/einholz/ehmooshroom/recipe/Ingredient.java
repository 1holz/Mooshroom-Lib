package de.einholz.ehmooshroom.recipe;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.registry.TransferableRegistry;
import de.einholz.ehmooshroom.storage.Transferable;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;

// XXX anotate constructor with only server?
public class Ingredient<T> implements Gredient<T> {
    private final Transferable<T, ? extends TransferVariant<T>> type;
    @Nullable
    private final Identified<T> tag;
    @Nullable
    private final NbtCompound nbt;
    private final long amount;

    @SuppressWarnings({ "unchecked", "null" })
    public Ingredient(Identifier typeId, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount) {
        this.type = (Transferable<T, ? extends TransferVariant<T>>) TransferableRegistry.TRANSFERABLE.get(typeId);
        this.tag = id != null ? (Identified<T>) type.getTagFactory().create(id) : null;
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

    @SuppressWarnings("null")
    @Override
    public boolean contains(T obj) {
        if (tag == null)
            return false;
        return tag.contains(obj);
    }

    @Override
    public Transferable<T, ? extends TransferVariant<T>> getType() {
        return type;
    }

    @Nullable
    @SuppressWarnings("null")
    @Override
    public Identifier getId() {
        if (tag == null)
            return null;
        return tag.getId();
    }

    @Nullable
    @Override
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public long getAmount() {
        return amount;
    }
}
