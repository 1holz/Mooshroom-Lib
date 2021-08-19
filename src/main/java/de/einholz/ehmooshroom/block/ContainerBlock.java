package de.einholz.ehmooshroom.block;

import java.util.HashSet;
import java.util.Set;

import de.einholz.ehmooshroom.container.AdvancedContainerBlockEntity;
import de.einholz.ehmooshroom.container.component.data.CombinedDataComponent;
import de.einholz.ehmooshroom.container.component.item.CombinedInventoryComponent;
import io.github.cottonmc.component.UniversalComponents;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class ContainerBlock extends DirectionalBlock implements BlockComponentProvider, InventoryProvider {
    public Identifier id;

    public ContainerBlock(FabricBlockSettings settings, Identifier id) {
        super(settings);
        this.id = id;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) player.openHandledScreen((AdvancedContainerBlockEntity) world.getBlockEntity(pos));
        return ActionResult.SUCCESS;
    }

    //FIXME: two times super method???
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) super.onBreak(world, pos, state, player);
        ItemStack itemStack = new ItemStack(asItem());
        CompoundTag compoundTag = world.getBlockEntity(pos).toTag(new CompoundTag());
        compoundTag.remove("x");
        compoundTag.remove("y");
        compoundTag.remove("z");
        compoundTag.remove("id");
        if (!compoundTag.isEmpty()) itemStack.putSubTag("BlockEntityTag", compoundTag);
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
        itemEntity.setToDefaultPickupDelay();
        world.spawnEntity(itemEntity);
        super.onBreak(world, pos, state, player);
    }

    @Override
    public <T extends Component> boolean hasComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        return getComponentTypes(blockView, pos, side).contains(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    //TODO: check wether additional components are needed
    public <T extends Component> T getComponent(BlockView blockView, BlockPos pos, ComponentType<T> type, Direction side) {
        if (UniversalComponents.INVENTORY_COMPONENT.equals(type)) return (T) ((AdvancedContainerBlockEntity) blockView.getBlockEntity(pos)).getCombinedComp(new CombinedInventoryComponent());
        if (UniversalComponents.DATA_PROVIDER_COMPONENT.equals(type)) return (T) ((AdvancedContainerBlockEntity) blockView.getBlockEntity(pos)).getCombinedComp(new CombinedDataComponent());
        return null;
    }

    @Override
    public Set<ComponentType<?>> getComponentTypes(BlockView blockView, BlockPos pos, Direction side) {
        Set<ComponentType<?>> set = new HashSet<>();
        set.add(UniversalComponents.INVENTORY_COMPONENT);
        set.add(UniversalComponents.DATA_PROVIDER_COMPONENT);
        return set;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return getComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, null).asLocalInventory(world, pos);
    }
}