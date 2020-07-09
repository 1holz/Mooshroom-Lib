package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.generators;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import de.alberteinholz.ehtech.registry.BlockRegistry;

public abstract class GeneratorBlockEntity extends MachineBlockEntity {
    public GeneratorBlockEntity(BlockRegistry registryEntry) {
        super(registryEntry);
        ((MachineDataProviderComponent) data).setConfigAvailability(new ConfigType[]{ConfigType.POWER}, new ConfigBehavior[]{ConfigBehavior.SELF_OUTPUT, ConfigBehavior.FOREIGN_OUTPUT}, null, true);
    }
}