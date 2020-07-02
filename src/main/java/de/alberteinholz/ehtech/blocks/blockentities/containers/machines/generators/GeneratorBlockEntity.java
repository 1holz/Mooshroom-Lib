package de.alberteinholz.ehtech.blocks.blockentities.containers.machines.generators;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class GeneratorBlockEntity extends MachineBlockEntity {
    public GeneratorBlockEntity(BlockEntityType<?> type) {
        super(type);
    }
}