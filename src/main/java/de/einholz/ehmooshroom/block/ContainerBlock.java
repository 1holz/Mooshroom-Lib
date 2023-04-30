package de.einholz.ehmooshroom.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ContainerBlock extends DirectionalBlock implements BlockEntityProvider {
    private Identifier id;
    @Deprecated // TODO del if unused
    private BlockEntityType<? extends BlockEntity> blockEntityType;
    private BlockEntityTicker<? extends BlockEntity> ticker;

    public ContainerBlock(Settings settings, Identifier id, BlockEntityTicker<? extends BlockEntity> ticker) {
        super(settings);
        this.id = id;
        this.ticker = ticker;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof NamedScreenHandlerFactory screenFactory) { 
            if (!world.isClient()) player.openHandledScreen(screenFactory);
            return ActionResult.SUCCESS;
        }
        // TODO both needed?
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    //FIXME: two times super method???
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //if (world.isClient) super.onBreak(world, pos, state, player);
        ItemStack itemStack = new ItemStack(asItem());
        NbtCompound nbtCompound = new NbtCompound();
        world.getBlockEntity(pos).writeNbt(nbtCompound);
        nbtCompound.remove("x");
        nbtCompound.remove("y");
        nbtCompound.remove("z");
        nbtCompound.remove("id");
        if (!nbtCompound.isEmpty()) itemStack.setSubNbt("BlockEntityNbt", nbtCompound);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().instantiate(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    // FIXME better way then the ugly casting?
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (getBlockEntityType() != type) return null;
        return (@Nullable BlockEntityTicker<T>) ticker;
    }

    @Deprecated // TODO del if unused
    protected BlockEntityType<? extends BlockEntity> getBlockEntityType() {
        if (blockEntityType == null) blockEntityType = Registry.BLOCK_ENTITY_TYPE.get(id);
        return blockEntityType;
    }
}
