package de.einholz.ehmooshroom.registry.rebs;

import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import de.einholz.ehmooshroom.storage.StorageProv;
import de.einholz.ehmooshroom.storage.Transferable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;

public interface BlocksREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract RegEntryBuilder<B, G, S, R> withBlockRaw(Function<RegEntryBuilder<B, G, S, R>, Block> blockFunc);
    abstract RegEntryBuilder<B, G, S, R> withBlockStorageProvFunc(Transferable<?, ?> trans, Function<RegEntryBuilder<B, G, S, R>, BlockApiProvider<Storage<?>, Direction>> storageProvFunc);
    abstract RegEntryBuilder<B, G, S, R> withoutBlockStorageProvFunc(Transferable<?, ?> trans);

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

    default RegEntryBuilder<B, G, S, R> withBlockStorageProvBuild(Transferable<?, ?> trans) {
        return withBlockStorageProvFunc(trans, entry -> (world, pos, state, be, dir) -> ((StorageProv) world.getBlockState(pos).getBlock()).getStorage(trans, dir));
    }
}
