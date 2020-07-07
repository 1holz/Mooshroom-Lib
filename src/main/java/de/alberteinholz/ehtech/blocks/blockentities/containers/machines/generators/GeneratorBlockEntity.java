package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.generators;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.registry.BlockRegistry;

public abstract class GeneratorBlockEntity extends MachineBlockEntity {
    public GeneratorBlockEntity(BlockRegistry registryEntry) {
        super(registryEntry);
    }
}