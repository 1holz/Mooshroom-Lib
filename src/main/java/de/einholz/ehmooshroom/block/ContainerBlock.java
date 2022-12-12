package de.einholz.ehmooshroom.block;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import de.einholz.ehmooshroom.storage.providers.FluidStorageProv;
import de.einholz.ehmooshroom.storage.providers.ItemStorageProv;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ContainerBlock extends DirectionalBlock implements BlockEntityProvider {
    private Identifier id;
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

    public BlockApiProvider<Storage<ItemVariant>, Direction> getItemStorageProv(RegEntryBuilder entry) {
        return (world, pos, state, null_be, dir) -> ((ItemStorageProv) blockEntityType.get(world, pos)).getItemStorage(dir);
    }

    public BlockApiProvider<Storage<FluidVariant>, Direction> getFluidStorageProv(RegEntryBuilder entry) {
        return (world, pos, state, null_be, dir) -> ((FluidStorageProv) blockEntityType.get(world, pos)).getFluidStorage(dir);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().instantiate(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    //FIXME better way then the ugly casting?
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (getBlockEntityType() != type) return null;
        return (@Nullable BlockEntityTicker<T>) ticker;
    }

    protected BlockEntityType<? extends BlockEntity> getBlockEntityType() {
        if (blockEntityType == null) blockEntityType = Registry.BLOCK_ENTITY_TYPE.get(id);
        return blockEntityType;
    }
}
