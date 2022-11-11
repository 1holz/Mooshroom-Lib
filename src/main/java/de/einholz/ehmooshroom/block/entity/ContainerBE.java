package de.einholz.ehmooshroom.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerBE extends BlockEntity implements BlockEntityClientSerializable {
    public ContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // TODO Auto-generated method stub
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // TODO Auto-generated method stub
        super.readNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        // TODO Auto-generated method stub
        return null;
    }
}
