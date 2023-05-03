package de.einholz.ehmooshroom.storage.transferable;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class BlockVariant extends NbtVariant<Block> {
    private final Block block;

    public BlockVariant(Block block, @Nullable NbtCompound nbt) {
        super(nbt);
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
		to.putString("block", Registry.BLOCK.getId(getObject()).toString());
		if (getNbt() != null) to.put("nbt", copyNbt());
		return to;
	}

    public static BlockVariant fromNbt(final NbtCompound from) {
        final Block block = Registry.BLOCK.get(new Identifier(from.getString("block")));
        return new BlockVariant(block, from.getCompound("nbt"));
    }

    @Override
    public void toPacket(final PacketByteBuf buf) {
        buf.writeIdentifier(Registry.BLOCK.getId(getObject()));
        buf.writeNbt(copyNbt());
    }

    public static BlockVariant fromPacket(final PacketByteBuf buf) {
        return new BlockVariant(Registry.BLOCK.get(buf.readIdentifier()), buf.readNbt());
    }
}
