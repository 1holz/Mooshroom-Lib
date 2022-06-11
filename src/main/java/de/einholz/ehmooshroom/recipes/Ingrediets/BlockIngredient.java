package de.einholz.ehmooshroom.recipes.Ingrediets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//also for BlockEntities etc.?
public class BlockIngredient {
    public final Identifier id;
    public final TagKey<Block> ingredient;

    public BlockIngredient(Identifier id) {
        this.id = id;
        this.ingredient = TagKey.of(Registry.BLOCK_KEY, id);
    }

    public boolean matches(BlockState state) {
        return state.isIn(ingredient);
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(id);
    }

    public static BlockIngredient read(PacketByteBuf buf) {
        return new BlockIngredient(buf.readIdentifier());
    }
}
