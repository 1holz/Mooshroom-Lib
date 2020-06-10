package de.alberteinholz.ehtech.blocks.directionalblocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public abstract class DirectionalBlock extends Block {
    public static final DirectionProperty FACING = Properties.FACING;

    public DirectionalBlock(FabricBlockSettings settings) {
        super(settings.build());
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.SOUTH));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
}