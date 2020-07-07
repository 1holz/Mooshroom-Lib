package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.consumers;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.registry.BlockRegistry;

public abstract class ConsumerBlockEntity extends MachineBlockEntity {
	public ConsumerBlockEntity(BlockRegistry registryEntry) {
		super(registryEntry);
    }
}