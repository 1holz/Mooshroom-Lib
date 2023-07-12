package de.einholz.ehmooshroom.storage.transferable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class BlockVariant extends NbtlessVariant<Block> {
    private final Block block;

    public BlockVariant(Block block) {
        super(block);
        this.block = block;
    }

    @Override
    public boolean isBlank() {
        return Blocks.AIR.equals(getObject()) || block == null;
    }

    @Override
    public Block getObject() {
        return block;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound to = new NbtCompound();
        to.putString("Block", Registry.BLOCK.getId(getObject()).toString());
        return to;
    }

    public static BlockVariant fromNbt(final NbtCompound from) {
        final Block block = Registry.BLOCK.get(new Identifier(from.getString("Block")));
        return new BlockVariant(block);
    }

    @Override
    public void toPacket(final PacketByteBuf buf) {
        buf.writeIdentifier(Registry.BLOCK.getId(getObject()));
    }

    public static BlockVariant fromPacket(final PacketByteBuf buf) {
        return new BlockVariant(Registry.BLOCK.get(buf.readIdentifier()));
    }

    public static BlockVariant blank() {
        return new BlockVariant(Blocks.AIR);
    }
}
