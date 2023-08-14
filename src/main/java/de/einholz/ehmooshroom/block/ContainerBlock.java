/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.block;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.block.entity.ContainerBE;
import de.einholz.ehmooshroom.storage.BlockApiLookups;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ContainerBlock extends BlockWithEntity {
    private Identifier id;
    private BlockEntityType<? extends BlockEntity> blockEntityType;
    private BlockEntityTicker<? extends BlockEntity> ticker;

    public ContainerBlock(Settings settings, Identifier id, BlockEntityTicker<? extends BlockEntity> ticker) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(Properties.FACING, Direction.NORTH));
        this.id = id;
        this.ticker = ticker;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        NamedScreenHandlerFactory screenFactory = state.createScreenHandlerFactory(world, pos);
        if (screenFactory != null) {
            player.openHandledScreen(screenFactory);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock()))
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ContainerBE containerBE) {
            ItemScatterer.spawn(world, pos, containerBE.getStorage(BlockApiLookups.ITEM_ID, null).getAsInv());
            // TODO implement comperator output
            // world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().instantiate(pos, state);
    }

    @Override
    @Nullable
    // FIXME better way then the ugly casting?
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
            BlockEntityType<T> type) {
        return checkType(type, (BlockEntityType<T>) getBlockEntityType(), (BlockEntityTicker<T>) ticker);
        // if (getBlockEntityType() != type) return null;
        // return (@Nullable BlockEntityTicker<T>) ticker;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void appendProperties(Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    protected BlockEntityType<? extends BlockEntity> getBlockEntityType() {
        if (blockEntityType == null)
            blockEntityType = Registry.BLOCK_ENTITY_TYPE.get(id);
        return blockEntityType;
    }
}
