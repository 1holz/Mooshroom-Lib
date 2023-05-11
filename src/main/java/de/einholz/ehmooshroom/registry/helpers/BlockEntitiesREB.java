package de.einholz.ehmooshroom.registry.helpers;

import java.util.function.BiFunction;
import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import de.einholz.ehmooshroom.storage.providers.FluidStorageProv;
import de.einholz.ehmooshroom.storage.providers.ItemStorageProv;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;

public interface BlockEntitiesREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract Block getBlock();
    abstract RegEntryBuilder<B, G, S, R> withBlockEntityRaw(Function<RegEntryBuilder<B, G, S, R>, BlockEntityType<B>> blockEntityTypeFunc);
    abstract RegEntryBuilder<B, G, S, R> withBlockEntityItemStorageProvRaw(Function<RegEntryBuilder<B, G, S, R>, BiFunction<B, Direction, Storage<ItemVariant>>> blockEntityItemStorageProvFunc);
    abstract RegEntryBuilder<B, G, S, R> withBlockEntityFluidStorageProvRaw(Function<RegEntryBuilder<B, G, S, R>, BiFunction<B, Direction, Storage<FluidVariant>>> blockEntityFluidStorageProvFunc);

    default RegEntryBuilder<B, G, S, R> withBlockEntityNull() {
        return withBlockEntityRaw((entry) -> null);
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityCustomBlocksBuild(Factory<B> blockEntityTypeFactory, Block... blocks) {
        return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, blocks).build());
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityBuild(Factory<B> blockEntityTypeFactory) {
        return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, getBlock()).build());
        //return withBlockEntityCustomBlocksBuild(blockEntityTypeFactory, getBlock());
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityItemStorageProvNull() {
        return withBlockEntityItemStorageProvRaw(entry -> null);
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityItemStorageProvBuild() {
        return withBlockEntityItemStorageProvRaw(entry -> (be, dir) -> ((ItemStorageProv) be).getItemStorage(dir));
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityFluidStorageProvNull() {
        return withBlockEntityFluidStorageProvRaw(entry -> null);
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityFluidStorageProvBuild() {
        return withBlockEntityFluidStorageProvRaw(entry -> (be, dir) -> ((FluidStorageProv) be).getFluidStorage(dir));
    }
}
