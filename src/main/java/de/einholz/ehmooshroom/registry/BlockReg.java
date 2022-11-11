package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.block.ContainerBlock;
import de.einholz.ehmooshroom.block.DirectionalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface BlockReg extends Reg {
    public static Block registerRaw(Identifier id, Block block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    public static Block registerBlock(Identifier id, Settings settings) {
        return registerRaw(id, new Block(settings));
    }

    public static DirectionalBlock registerDirectionalBlock(Identifier id, Settings settings) {
        return (DirectionalBlock) registerRaw(id, new DirectionalBlock(settings));
    }

    public static ContainerBlock registerContainerBlock(Identifier id, Settings settings, BlockEntityTicker<? extends BlockEntity> ticker) {
        return (ContainerBlock) registerRaw(id, new ContainerBlock(settings, id, ticker));
    }
}
