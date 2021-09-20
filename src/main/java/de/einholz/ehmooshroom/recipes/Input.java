package de.einholz.ehmooshroom.recipes;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

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
            buf.writeBoolean(true).writeInt(items.length);
            for (ItemIngredient ingredient : items) {
                ingredient.write(buf);
            }
        } else buf.writeBoolean(false);
        if (fluids != null || fluids.length > 0) {
            buf.writeBoolean(true).writeInt(fluids.length);
            for (FluidIngredient ingredient : fluids) {
                ingredient.write(buf);
            }
        } else buf.writeBoolean(false);
        if (blocks != null || blocks.length > 0) {
            buf.writeBoolean(true).writeInt(blocks.length);
            for (BlockIngredient ingredient : blocks) {
                ingredient.write(buf);
            }
        } else buf.writeBoolean(false);
        if (entities != null || entities.length > 0) {
            buf.writeBoolean(true).writeInt(entities.length);
            for (EntityIngredient ingredient : entities) {
                ingredient.write(buf);
            }
        } else buf.writeBoolean(false);
        if (data != null || data.length > 0) {
            buf.writeBoolean(true).writeInt(data.length);
            for (DataIngredient ingredient : data) {
                ingredient.write(buf);
            }
        } else buf.writeBoolean(false);
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
        public final Identifier id;
        public final Tag<Item> ingredient;
        public final int amount;
        public final NbtCompound nbt;

        public ItemIngredient(Identifier id, int amount, NbtCompound nbt) {
            this.id = id;
            this.ingredient = ServerTagManagerHolder.getTagManager().getItems().getTag(id);
            this.amount = amount;
            this.nbt = nbt;
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

    public static class FluidIngredient {
        public final Identifier id;
        public final Tag<Fluid> ingredient;
        public final Fraction amount;
        public final NbtCompound nbt;

        public FluidIngredient(Identifier id, Fraction amount, NbtCompound nbt) {
            this.id = id;
            this.ingredient = ServerTagManagerHolder.getTagManager().getFluids().getTag(id);
            this.amount = amount;
            this.nbt = nbt;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(id).writeInt(amount.getNumerator()).writeInt(amount.getDenominator());
            if (nbt == null || nbt.isEmpty()) buf.writeBoolean(false);
            else {
                buf.writeBoolean(true);
                buf.writeNbt(nbt);
            }
        }

        public static FluidIngredient read(PacketByteBuf buf) {
            return new FluidIngredient(buf.readIdentifier(), Fraction.of(buf.readInt(), buf.readInt()), buf.readBoolean() ? buf.readNbt() : new NbtCompound());
        }
    }

    public static class BlockIngredient {
        public final Identifier id;
        public final Tag<Block> ingredient;
        //XXX: are states the right thing here? probably not...
        public final BlockState state;

        public BlockIngredient(Identifier id) {
            this.id = id;
            this.ingredient = ServerTagManagerHolder.getTagManager().getBlocks().getTag(id);
            this.state = null;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(id);
        }

        public static BlockIngredient read(PacketByteBuf buf) {
            return new BlockIngredient(buf.readIdentifier());
        }
    }

    public static class EntityIngredient {
        public final Identifier id;
        public final Tag<EntityType<?>> ingredient;
        public final int amount;
        public final NbtCompound nbt;

        public EntityIngredient(Identifier id, int amount, NbtCompound nbt) {
            this.id = id;
            this.ingredient = ServerTagManagerHolder.getTagManager().getEntityTypes().getTag(id);
            this.amount = amount;
            this.nbt = nbt;
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

    public static class DataIngredient {
        public final NbtElement nbt;

        public DataIngredient(NbtElement nbt) {
            this.nbt = nbt;
        }

        public void write(PacketByteBuf buf) {
            NbtCompound NbtCompound = new NbtCompound();
            NbtCompound.put("indata", nbt);
            buf.writeNbt(NbtCompound);
        }

        public static DataIngredient read(PacketByteBuf buf) {
            return new DataIngredient(buf.readNbt().get("indata"));
        }
    }
}