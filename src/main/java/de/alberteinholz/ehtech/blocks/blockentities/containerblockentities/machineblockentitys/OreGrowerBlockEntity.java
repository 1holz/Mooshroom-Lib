package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys;

import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.DirectionalBlock;
import de.alberteinholz.ehtech.blocks.recipes.Input;
import de.alberteinholz.ehtech.blocks.recipes.MachineRecipe;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.github.cottonmc.component.api.ActionType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class OreGrowerBlockEntity extends MachineBlockEntity {
    public OreGrowerBlockEntity() {
        this(BlockRegistry.ORE_GROWER.blockEntityType);
    }

    public OreGrowerBlockEntity(BlockEntityType<?> type) {
        super(type);
        inventory.stacks.put("seed_input", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.INPUT));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void process() {
        super.process();
    }

    @Override
    public void task() {
        super.task();
        MachineDataProviderComponent data = (MachineDataProviderComponent) this.data;
        MachineRecipe recipe = (MachineRecipe) data.getRecipe(world);
        int consum = (int) (data.getEfficiency() * data.getSpeed() * recipe.consumes);
        if (!containsBlockIngredients(recipe.input.blocks)) {
            cancle();
        } else if (capacitor.extractEnergy(capacitor.getPreferredType(), consum, ActionType.TEST) == consum) {
            if (data.progress.getBarCurrent() == data.progress.getBarMinimum()) {
                inventory.getItemStack("seed_input").decrement(recipe.input.items[0].amount);
            }
            data.addProgress(recipe.timeModifier * data.getSpeed());
            data.setPowerPerTick(-consum);
            capacitor.extractEnergy(capacitor.getPreferredType(), consum, ActionType.PERFORM);
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
    }

    @Override
    public void finish() {
        world.setBlockState(pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING)), ((MachineRecipe) ((MachineDataProviderComponent) data).getRecipe(world)).output.blocks[0]);
        super.finish();
    }

    @Override
    public void cancle() {
        super.cancle();
    }

    @Override
    public void idle() {
        super.idle();
    }

    @Override
    public void correct() {
        super.correct();
    }

    @Override
    public boolean containsBlockIngredients(Input.BlockIngredient... ingredients) {
        return ingredients[0].ingredient.contains(world.getBlockState(pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING))).getBlock());
    }

    @Override
    protected MachineDataProviderComponent initializeDataProviderComponent() {
        return new MachineDataProviderComponent("block.ehtech.ore_grower");
    }
}