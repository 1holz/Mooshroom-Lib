package de.einholz.ehmooshroom.recipes;

import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class Output {
    public final ItemStack[] items;
    public final FluidVolume[] fluids;
    public final BlockState[] blocks;
    public final Entity[] entities;
    public final NbtElement[] data;

    public Output(ItemStack[] items, FluidVolume[] fluids, BlockState[] blocks, Entity[] entities, NbtElement[] data) {
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
            for (ItemStack stack : items) {
                buf.writeItemStack(stack);
            }
        } else {
            buf.writeBoolean(false);
        }
        if (fluids != null || fluids.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(fluids.length);
            for (FluidVolume volume : fluids) {
                Registry.FLUID.getId(volume.getFluid());
                buf.writeIdentifier(Registry.FLUID.getId(volume.getFluid()));
                buf.writeInt(volume.getAmount().getNumerator());
                buf.writeInt(volume.getAmount().getDenominator());
                if (!volume.hasTag() || volume.getTag().isEmpty()) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    buf.writeNbt(volume.getTag());
                }
            }
        } else {
            buf.writeBoolean(false);
        }
        if (blocks != null || blocks.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(blocks.length);
            for (BlockState state : blocks) {
                buf.writeIdentifier(Registry.BLOCK.getId(state.getBlock()));
            }
        } else {
            buf.writeBoolean(false);
        }
        if (entities != null || entities.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(entities.length);
            for (Entity entity : entities) {
                buf.writeIdentifier(Registry.ENTITY_TYPE.getId(entity.getType()));
                buf.writeNbt(entity.writeNbt(new NbtCompound()));
            }
        } else {
            buf.writeBoolean(false);
        }
        if (data != null || data.length > 0) {
            buf.writeBoolean(true);
            buf.writeInt(data.length);
            for (NbtElement tag : data) {
                NbtCompound compoundTag = new NbtCompound();
                compoundTag.put("dummy", tag);
                buf.writeNbt(compoundTag);
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Output read(PacketByteBuf buf) {
        ItemStack[] items = null;
        if (buf.readBoolean()) {
            items = new ItemStack[buf.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = buf.readItemStack();
            }
        }
        FluidVolume[] fluids = null;
        if (buf.readBoolean()) {
            fluids = new FluidVolume[buf.readInt()];
            for (int i = 0; i < fluids.length; i++) {
                fluids[i] = new FluidVolume(Registry.FLUID.get(buf.readIdentifier()), Fraction.of(buf.readInt(), buf.readInt()));
                if (buf.readBoolean()) {
                    fluids[i].setTag(buf.readNbt());
                }
            }
        }
        BlockState[] blocks = null;
        if (buf.readBoolean()) {
            blocks = new BlockState[buf.readInt()];
            for (int i = 0; i < blocks.length; i++) {
                blocks[i] = Registry.BLOCK.get(buf.readIdentifier()).getDefaultState();
            }
        }
        Entity[] entities = null;
        if (buf.readBoolean()) {
            entities = new Entity[buf.readInt()];
            for (int i = 0; i < entities.length; i++) {
                //use entity.setWorld(world) later
                entities[i] = Registry.ENTITY_TYPE.get(buf.readIdentifier()).create(null);
                entities[i].readNbt(buf.readNbt());
            }
        }
        NbtElement[] data = null;
        if (buf.readBoolean()) {
            data = new NbtElement[buf.readInt()];
            for (int i = 0; i < data.length; i++) {
                data[i] = buf.readNbt().get("dummy");
            }
        }
        return new Output(items, fluids, blocks, entities, data);
    }
}