package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks;

import java.util.HashSet;
import java.util.Set;

import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.ContainerBlockEntity;
import de.alberteinholz.ehtech.blocks.directionalblocks.DirectionalBlock;
import io.github.cottonmc.component.UniversalComponents;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class ContainerBlock extends DirectionalBlock implements BlockComponentProvider, InventoryProvider {
    public Identifier id;

    public ContainerBlock(FabricBlockSettings settings, Identifier id) {
        super(settings);
        this.id = id;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(id, player, buf -> buf.writeBlockPos(pos));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            ItemStack itemStack = new ItemStack(asItem());
            CompoundTag compoundTag = world.getBlockEntity(pos).toTag(new CompoundTag());
            compoundTag.remove("x");
            compoundTag.remove("y");
            compoundTag.remove("z");
            compoundTag.remove("id");
            if (!compoundTag.isEmpty()) {
                itemStack.putSubTag("BlockEntityTag", compoundTag);
            }
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onBlockRemoved(state, world, pos, newState, moved);
        if (state.getBlock() != newState.getBlock()) {
            world.updateHorizontalAdjacent(pos, this);
        }
    }

    @Override
    public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return getComponentTypes(blockView, pos, side).contains(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        if(type == UniversalComponents.INVENTORY_COMPONENT) {
            return (T) ((ContainerBlockEntity) blockView.getBlockEntity(pos)).inventory;
        } else if (type == UniversalComponents.DATA_PROVIDER_COMPONENT) {
            return (T) ((ContainerBlockEntity) blockView.getBlockEntity(pos)).data;
        } else {
            return null;
        }
    }

    @Override
    public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, Direction side) {
        Set<ComponentType<?>> set = new HashSet<>();
        set.add(UniversalComponents.INVENTORY_COMPONENT);
        set.add(UniversalComponents.DATA_PROVIDER_COMPONENT);
        return set;
    }

    public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) {
        return getComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, null).asLocalInventory(world, pos);
    }
}