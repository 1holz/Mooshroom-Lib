package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys;

import de.alberteinholz.ehtech.blocks.directionalblocks.DirectionalBlock;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.recipes.Input;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.type.EnergyTypes;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class OreGrowerBlockEntity extends MachineBlockEntity {
    public OreGrowerBlockEntity() {
        this(BlockRegistry.ORE_GROWER.blockEntityType);
    }

    public OreGrowerBlockEntity(BlockEntityType<?> type) {
        super(type);
        inventory.stacks.put("seed_input", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.INPUT));
    }

    @Override
    public void tick() {
        //only for testing TODO: change
        for (Direction neighbor : DirectionalBlock.FACING.getValues()) {
            BlockEntity be = world.getBlockEntity(pos.offset(neighbor));
            if (be instanceof MachineBlockEntity && capacitor.getCurrentEnergy() < capacitor.getMaxEnergy()) {
                MachineCapacitorComponent cap = ((MachineBlockEntity) be).capacitor;
                if (cap.canExtractEnergy()) {
                    int transfered = capacitor.getPreferredType().getMaximumTransferSize() - capacitor.insertEnergy(capacitor.getPreferredType(), capacitor.getPreferredType().getMaximumTransferSize(), ActionType.TEST);
                    capacitor.insertEnergy(capacitor.getPreferredType(), cap.extractEnergy(capacitor.getPreferredType(), transfered, ActionType.PERFORM), ActionType.PERFORM);
                }
            }
        }
        //end
        MachineDataProviderComponent data = (MachineDataProviderComponent) this.data;
        boolean isRunning = data.progress.getBarCurrent() > data.progress.getBarMinimum() && isActivated() ? true : false;
        if (!isRunning && isActivated()) {
            world.getRecipeManager().getFirstMatch(BlockRegistry.ORE_GROWER.recipeType, new InventoryWrapper(pos), world).ifPresent(r -> this.recipe = r);
            if (recipe != null) {
                isRunning = true;
            }
        }
        if (isRunning) {
            int consum = (int) (data.getEfficiency() * data.getSpeed() * recipe.consumes);
            BlockPos target = pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING));
            if (!containsBlockIngredients(recipe.input.blocks)) {
                data.setProgress(0.0);
            } else if (capacitor.extractEnergy(capacitor.getPreferredType(), consum, ActionType.TEST) == consum) {
                if (data.progress.getBarCurrent() == data.progress.getBarMinimum()) {
                    inventory.getItemStack("seed_input").decrement(recipe.input.items[0].amount);
                }
                data.setProgress(data.progress.getBarCurrent() + recipe.timeModifier * data.getSpeed());
                data.setPowerPerTick(consum * -1);
                capacitor.extractEnergy(capacitor.getPreferredType(), consum, ActionType.PERFORM);
                //TODO: Make amount configurable
                for (int i = 0; i < 4; i++) {
                    int side = world.random.nextInt(5);
                    double x = side == 0 ? 0 : side == 1 ? 1 : world.random.nextDouble();
                    double y = side == 2 ? 0 : side == 3 ? 1 : world.random.nextDouble();
                    double z = side == 4 ? 0 : side == 5 ? 1 : world.random.nextDouble();
                    world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, recipe.output.blocks[0]), target.getX() + x, target.getY() + y, target.getZ() + z, 0.1, 0.1, 0.1);
                }
            }
            if (data.progress.getBarCurrent() > data.progress.getBarMaximum()) {
                world.setBlockState(target, recipe.output.blocks[0]);
            }
        }
        if (data.progress.getBarCurrent() > data.progress.getBarMaximum()) {
            data.setProgress(data.progress.getBarMinimum());
            recipe = null;
        }
        super.tick();
    }

    @Override
    public boolean containsBlockIngredients(Input.BlockIngredient... ingredients) {
        return ingredients[0].ingredient.contains(world.getBlockState(pos.offset(world.getBlockState(pos).get(DirectionalBlock.FACING))).getBlock());
    }

    @Override
    protected MachineCapacitorComponent initializeCapacitorComponent() {
        return new MachineCapacitorComponent(EnergyTypes.ULTRA_LOW_VOLTAGE);
    }

    @Override
    protected ContainerInventoryComponent initializeInventoryComponent() {
        return new ContainerInventoryComponent();
    }

    @Override
    protected ContainerDataProviderComponent initializeDataProviderComponent() {
        return new MachineDataProviderComponent("block.ehtech.ore_grower");
    }
}