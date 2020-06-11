package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.CoalGeneratorDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineCapacitorComponent;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.github.cottonmc.component.energy.type.EnergyTypes;
import net.minecraft.block.entity.BlockEntityType;

public class CoalGeneratorBlockEntity extends MachineBlockEntity {
    public CoalGeneratorBlockEntity() {
        this(BlockRegistry.COAL_GENERATOR.blockEntityType);
    }

    public CoalGeneratorBlockEntity(BlockEntityType<?> type) {
        super(type);
        inventory.stacks.put("coal_input", new ContainerInventoryComponent.Slot(ContainerInventoryComponent.Slot.Type.INPUT));
    }

    @Override
    public void start() {
        super.start();
        inventory.getItemStack("coal_input").decrement(recipe.input.items[0].amount);
    }

    @Override
    public void process() {
        super.process();
        CoalGeneratorDataProviderComponent data = (CoalGeneratorDataProviderComponent) this.data;
        if (capacitor.getCurrentEnergy() < capacitor.getMaxEnergy()) {
            data.setProgress(data.progress.getBarCurrent() + recipe.timeModifier * data.getSpeed());
            data.setHeat(data.heat.getBarCurrent() + recipe.generates * data.getSpeed() * data.getEfficiency());
            data.setPowerPerTick((int) (data.getEfficiency() * data.getSpeed() * (data.heat.getBarCurrent() - data.heat.getBarMinimum()) / (data.heat.getBarMaximum() - data.heat.getBarMinimum()) * 3 + 1));
            capacitor.generateEnergy(world, pos, data.getPowerPerTick());
        }
    }

    @Override
    public void task() {
        super.task();
    }

    @Override
    public void finish() {
        super.finish();

    }

    @Override
    public void cancle() {
        super.cancle();
    }

    @Override
    public void idle() {
        super.idle();
        CoalGeneratorDataProviderComponent data = (CoalGeneratorDataProviderComponent) this.data;
        if (data.heat.getBarCurrent() > data.heat.getBarMinimum()) {
            data.setHeat(data.heat.getBarCurrent() - 0.1);
        }
        data.setPowerPerTick(0);
    }

    @Override
    public void correct() {
        super.correct();
        CoalGeneratorDataProviderComponent data = (CoalGeneratorDataProviderComponent) this.data;
        if (data.heat.getBarCurrent() > data.heat.getBarMaximum()) {
            data.setHeat(data.heat.getBarMaximum());
        } else if (data.heat.getBarCurrent() < data.heat.getBarMinimum()) {
            data.setHeat(data.heat.getBarMinimum());
        }
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
    protected CoalGeneratorDataProviderComponent initializeDataProviderComponent() {
        return new CoalGeneratorDataProviderComponent();
    }
}