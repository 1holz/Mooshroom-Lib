package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.consumers;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class ConsumerBlockEntity extends MachineBlockEntity {
	public ConsumerBlockEntity(BlockEntityType<?> type) {
		super(type);
    }
}