package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.recipe.Ingrediets.BlockIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.DataIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.EntityIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.FluidIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.ItemIngredient;
import net.minecraft.network.PacketByteBuf;

public class Input {
    //XXX use interface for all ingredients? anotate their constructor with the only server thing?
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
}
