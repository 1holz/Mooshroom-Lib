package de.einholz.ehmooshroom.recipe.Ingrediets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//also for BlockEntities etc.?
public class BlockIngredient {
    public final Identifier id;
    public final Identified<Block> ingredient;

    public BlockIngredient(Identifier id) {
        this.id = id;
        this.ingredient = RequiredTagListRegistry.register(Registry.BLOCK_KEY, "tags/blocks").add(id.toString());
        //this.ingredient = TagKey.of(Registry.BLOCK_KEY, id);
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
