package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks;

import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys.OreGrowerBlockEntity;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.BlockView;

public class OreGrowerBlock extends MachineBlock {
    public OreGrowerBlock() {
        this(BlockRegistry.getId(BlockRegistry.ORE_GROWER));
    }

    public OreGrowerBlock(Identifier id) {
        this(getStandardFabricBlockSettings(), id);
    }

    public OreGrowerBlock(FabricBlockSettings settings, Identifier id) {
        super(settings, id);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new OreGrowerBlockEntity();
    }
}