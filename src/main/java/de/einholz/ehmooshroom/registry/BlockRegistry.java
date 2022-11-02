package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.block.ContainerBlock;
import de.einholz.ehmooshroom.block.DirectionalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {
    public static void load() {}

    protected static Block registerRaw(Identifier id, Block block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    protected static Block registerBlock(Identifier id, Settings settings) {
        return registerRaw(id, new Block(settings));
    }

    protected static DirectionalBlock registerDirectionalBlock(Identifier id, Settings settings) {
        return (DirectionalBlock) registerRaw(id, new DirectionalBlock(settings));
    }

    protected static ContainerBlock registerContainerBlock(Identifier id, Settings settings) {
        return (ContainerBlock) registerRaw(id, new ContainerBlock(settings, id));
    }
}
