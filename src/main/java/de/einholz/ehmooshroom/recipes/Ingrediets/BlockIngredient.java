package de.einholz.ehmooshroom.recipes.Ingrediets;

import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

//also for BlockEntities etc.?
public class BlockIngredient {
    public final Identifier id;
    public final TagKey<Block> ingredient;

    public BlockIngredient(Identifier id) {
        this.id = id;
        this.ingredient = ServerTagManagerHolder.getTagManager().getBlocks().getTag(id);
    }

    public boolean matches(Block block) {
        return ingredient.contains(block);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
    }

    public static BlockIngredient read(PacketByteBuf buf) {
        return new BlockIngredient(buf.readIdentifier());
    }
}
