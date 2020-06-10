package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks;

import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys.CoalGeneratorBlockEntity;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;

public class CoalGeneratorBlock extends MachineBlock {
    public CoalGeneratorBlock() {
        this(BlockRegistry.getId(BlockRegistry.COAL_GENERATOR));
    }

    public CoalGeneratorBlock(Identifier id) {
        this(getStandardFabricBlockSettings(), id);
    }

    public CoalGeneratorBlock(FabricBlockSettings settings, Identifier id) {
        super(settings, id);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CoalGeneratorBlockEntity();
    }
}