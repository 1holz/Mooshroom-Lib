package de.einholz.ehmooshroom.registry.rebs;

import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;

public interface BlocksREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract RegEntryBuilder<B, G, S, R> withBlockRaw(Function<RegEntryBuilder<B, G, S, R>, Block> blockFunc);
    abstract RegEntryBuilder<B, G, S, R> withBlockItemStorageProvRaw(Function<RegEntryBuilder<B, G, S, R>, BlockApiProvider<Storage<ItemVariant>, Direction>> blockItemStorageProvFunc);
    abstract RegEntryBuilder<B, G, S, R> withBlockFluidStorageProvRaw(Function<RegEntryBuilder<B, G, S, R>, BlockApiProvider<Storage<FluidVariant>, Direction>> blockFluidStorageProvFunc);
    
    default RegEntryBuilder<B, G, S, R> withBlockNull() {
        return withBlockRaw(entry -> null);
    }

    @FunctionalInterface
    public static interface BlockFactory<B extends Block> {
        B create(Settings settings);
    }

    default RegEntryBuilder<B, G, S, R> withBlockBuild(BlockFactory<? extends Block> factory, Settings settings) {
        return withBlockRaw((entry) -> factory.create(settings));
    }

    default RegEntryBuilder<B, G, S, R> withBlockItemStorageProvNull() {
        return withBlockItemStorageProvRaw(entry -> null);
    }

    default RegEntryBuilder<B, G, S, R> withBlockFluidStorageProvNull() {
        return withBlockFluidStorageProvRaw(entry -> null);
    }
}
