package de.einholz.ehmooshroom.registry;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class BlockRegistry extends RegistryBuilder<Block> {
    public BlockRegistry register(String name, BlockFactory<Block> factory, Settings settings) {
        return (BlockRegistry) register(name, factory.create(settings));
    }

    protected BlockRegistry() {
    }

    public BlockRegistry withBlockApiLookup(BlockApiLookup<Storage<?>, Direction> lookup,
            BlockApiProvider<Storage<?>, Direction> provider) {
        lookup.registerForBlocks(provider, get());
        return this;
    }

    @Override
    protected Registry<Block> getRegistry() {
        return Registry.BLOCK;
    }

    @FunctionalInterface
    public static interface BlockFactory<T extends Block> {
        T create(Settings settings);
    }
}
