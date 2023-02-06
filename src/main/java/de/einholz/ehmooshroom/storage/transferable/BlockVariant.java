package de.einholz.ehmooshroom.storage.transferable;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class BlockVariant extends CustomVariant<Block> {
    private final Block block;

    public BlockVariant(Block block, @Nullable NbtCompound nbt) {
        super(nbt);
        this.block = block;
    }

    @Override
	public boolean isBlank() {
        return getObject() == Blocks.AIR;
    }

    @Override
	public Block getObject() {
        return block;
    }

    @Override
	public boolean isOf(Block block) {
		return getObject() == block;
	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound result = new NbtCompound();
		result.putString("block", Registry.BLOCK.getId(getObject()).toString());
		if (getNbt() != null) result.put("nbt", copyNbt());
		return result;
	}

    @Override
    public void toPacket(PacketByteBuf buf) {
        
    }
}
