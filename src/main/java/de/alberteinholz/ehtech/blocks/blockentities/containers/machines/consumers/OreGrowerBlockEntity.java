package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.consumers;

import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionals.DirectionalBlock;
import de.alberteinholz.ehtech.blocks.guis.guis.machines.OreGrowerGui;
import de.alberteinholz.ehtech.blocks.recipes.Input;
import de.alberteinholz.ehtech.blocks.recipes.MachineRecipe;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class OreGrowerBlockEntity extends ConsumerBlockEntity {
    public OreGrowerBlockEntity() {
        this(BlockRegistry.ORE_GROWER.blockEntityType);
    }

    public OreGrowerBlockEntity(BlockEntityType<?> type) {
        super(type);
        inventory.stacks.put("seed_input", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.INPUT));
    }

    @Override
    public boolean process() {
        if (!containsBlockIngredients(((MachineRecipe) ((MachineDataProviderComponent) data).getRecipe(world)).input.blocks)) {
            cancle();
            return false;
        } else {
            return super.process();
        }
    }

    @Override
    public void task() {
        super.task();
        MachineRecipe recipe = (MachineRecipe) ((MachineDataProviderComponent) data).getRecipe(world);
        BlockPos target = pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING));
        //TODO: Make particle amount configurable
        for (int i = 0; i < 4; i++) {
            int side = world.random.nextInt(5);
            double x = side == 0 ? 0 : side == 1 ? 1 : world.random.nextDouble();
            double y = side == 2 ? 0 : side == 3 ? 1 : world.random.nextDouble();
            double z = side == 4 ? 0 : side == 5 ? 1 : world.random.nextDouble();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, recipe.output.blocks[0]), target.getX() + x, target.getY() + y, target.getZ() + z, 0.1, 0.1, 0.1);
        }
    }

    @Override
    public void finish() {
        world.setBlockState(pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING)), ((MachineRecipe) ((MachineDataProviderComponent) data).getRecipe(world)).output.blocks[0]);
        super.finish();
    }

    @Override
    public boolean containsBlockIngredients(Input.BlockIngredient... ingredients) {
        return ingredients[0].ingredient.contains(world.getBlockState(pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING))).getBlock());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        return new OreGrowerGui(syncId, playerInv, buf);
    }

    @Override
    protected MachineDataProviderComponent initializeDataProviderComponent() {
        return new MachineDataProviderComponent("block.ehtech.ore_grower");
    }
}