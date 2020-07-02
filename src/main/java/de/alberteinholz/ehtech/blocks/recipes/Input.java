package de.alberteinholz.ehtech.blocks.recipes;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag.Identified;

public class Input {
    public final ItemIngredient[] items;
    public final FluidIngredient[] fluids;
    public final BlockIngredient[] blocks;
    public final EntityIngredient[] entities;
    public final DataIngredient[] data;

    public Input(ItemIngredient[] items, FluidIngredient[] fluids, BlockIngredient[] blocks, EntityIngredient[] entities, DataIngredient[] data) {
        this.items = items;
        this.fluids = fluids;
        this.blocks = blocks;
        this.entities = entities;
        this.data = data;
    }

    public void write(PacketByteBuf buf) {
        if (items != null || items.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(items.length);
            for (ItemIngredient ingredient : items) {
                ingredient.write(buf);
            }
        } else {
            buf.writeBoolean(false);
        }
        if (fluids != null || fluids.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(fluids.length);
            for (FluidIngredient ingredient : fluids) {
                ingredient.write(buf);
            }
        } else {
            buf.writeBoolean(false);
        }
        if (blocks != null || blocks.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(blocks.length);
            for (BlockIngredient ingredient : blocks) {
                ingredient.write(buf);
            }
        } else {
            buf.writeBoolean(false);
        }
        if (entities != null || entities.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(entities.length);
            for (EntityIngredient ingredient : entities) {
                ingredient.write(buf);
            }
        } else {
            buf.writeBoolean(false);
        }
        if (data != null || data.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(data.length);
            for (DataIngredient ingredient : data) {
                ingredient.write(buf);
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Input read(PacketByteBuf buf) {
        ItemIngredient[] items = null;
        if (buf.readBoolean()) {
            items = new ItemIngredient[buf.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = ItemIngredient.read(buf);
            }
        }
        FluidIngredient[] fluids = null;
        if (buf.readBoolean()) {
            fluids = new FluidIngredient[buf.readInt()];
            for (int i = 0; i < fluids.length; i++) {
                fluids[i] = FluidIngredient.read(buf);
            }
        }
        BlockIngredient[] blocks = null;
        if (buf.readBoolean()) {
            blocks = new BlockIngredient[buf.readInt()];
            for (int i = 0; i < blocks.length; i++) {
                blocks[i] = BlockIngredient.read(buf);
            }
        }
        EntityIngredient[] entities = null;
        if (buf.readBoolean()) {
            entities = new EntityIngredient[buf.readInt()];
            for (int i = 0; i < entities.length; i++) {
                entities[i] = EntityIngredient.read(buf);
            }
        }
        DataIngredient[] data = null;
        if (buf.readBoolean()) {
            data = new DataIngredient[buf.readInt()];
            for (int i = 0; i < data.length; i++) {
                data[i] = DataIngredient.read(buf);
            }
        }
        return new Input(items, fluids, blocks, entities, data);
    }

    public static class ItemIngredient {
        public final Identified<Item> ingredient;
        public final int amount;
        public final CompoundTag tag;

        public ItemIngredient(Identified<Item> ingredient, int amount, CompoundTag tag) {
            this.ingredient = ingredient;
            this.amount = amount;
            this.tag = tag;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(ingredient.getId());
            buf.writeInt(amount);
            if (tag == null || tag.isEmpty()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeCompoundTag(tag);
            }
        }

        public static ItemIngredient read(PacketByteBuf buf) {
            Identified<Item> ingredient = (Identified<Item>) ItemTags.getContainer().get(buf.readIdentifier());
            int amount = buf.readInt();
            CompoundTag tag = buf.readBoolean() ? buf.readCompoundTag() : new CompoundTag();
            return new ItemIngredient(ingredient, amount, tag);
        }
    }

    public static class FluidIngredient {
        public final Identified<Fluid> ingredient;
        public final Fraction amount;
        public final CompoundTag tag;

        public FluidIngredient(Identified<Fluid> ingredient, Fraction amount, CompoundTag tag) {
            this.ingredient = ingredient;
            this.amount = amount;
            this.tag = tag;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(ingredient.getId());
            buf.writeInt(amount.getNumerator());
            buf.writeInt(amount.getDenominator());
            if (tag == null || tag.isEmpty()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeCompoundTag(tag);
            }
        }

        public static FluidIngredient read(PacketByteBuf buf) {
            Identified<Fluid> ingredient = (Identified<Fluid>) FluidTags.getContainer().get(buf.readIdentifier());
            Fraction amount = Fraction.of(buf.readInt(), buf.readInt());
            CompoundTag tag = buf.readBoolean() ? buf.readCompoundTag() : new CompoundTag();
            return new FluidIngredient(ingredient, amount, tag);
        }
    }

    public static class BlockIngredient {
        public final Identified<Block> ingredient;
        //Are states the right thing here? Probably not...
        public final BlockState state;

        public BlockIngredient(Identified<Block> ingredient) {
            this.ingredient = ingredient;
            this.state = null;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(ingredient.getId());
        }

        public static BlockIngredient read(PacketByteBuf buf) {
            return new BlockIngredient((Identified<Block>) BlockTags.getContainer().get(buf.readIdentifier()));
        }
    }

    public static class EntityIngredient {
        public final Identified<EntityType<?>> ingredient;
        public final int amount;
        public final CompoundTag tag;

        public EntityIngredient(Identified<EntityType<?>> ingredient, int amount, CompoundTag tag) {
            this.ingredient = ingredient;
            this.amount = amount;
            this.tag = tag;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(ingredient.getId());
            buf.writeInt(amount);
            if (tag == null || tag.isEmpty()) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeCompoundTag(tag);
            }
        }

        public static EntityIngredient read(PacketByteBuf buf) {
            Identified<EntityType<?>> ingredient = (Identified<EntityType<?>>) EntityTypeTags.getContainer()
                    .get(buf.readIdentifier());
            int amount = buf.readInt();
            CompoundTag tag = buf.readBoolean() ? buf.readCompoundTag() : new CompoundTag();
            return new EntityIngredient(ingredient, amount, tag);
        }
    }

    public static class DataIngredient {
        public final net.minecraft.nbt.Tag tag;

        public DataIngredient(net.minecraft.nbt.Tag tag) {
            this.tag = tag;
        }

        public void write(PacketByteBuf buf) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("dummy", tag);
            buf.writeCompoundTag(compoundTag);
        }

        public static DataIngredient read(PacketByteBuf buf) {
            net.minecraft.nbt.Tag tag = buf.readCompoundTag().get("dummy");
            return new DataIngredient(tag);
        }
    }
}