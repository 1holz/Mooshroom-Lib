package de.einholz.ehmooshroom.registry.rebs;

import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import de.einholz.ehmooshroom.storage.StorageProv;
import de.einholz.ehmooshroom.storage.Transferable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockEntityApiProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
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
    abstract RegEntryBuilder<B, G, S, R> withBlockEntityStorageProvFunc(Transferable<?, ?> trans, Function<RegEntryBuilder<B, G, S, R>, BlockEntityApiProvider<Storage<?>, Direction>> storageProvFunc);
    abstract RegEntryBuilder<B, G, S, R> withoutBlockEntityStorageProvFunc(Transferable<?, ?> trans);

    default RegEntryBuilder<B, G, S, R> withBlockEntityNull() {
        return withBlockEntityRaw((entry) -> null);
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityCustomBlocksBuild(Factory<B> blockEntityTypeFactory, Block... blocks) {
        return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, blocks).build());
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityBuild(Factory<B> blockEntityTypeFactory) {
        //return withBlockEntityRaw((entry) -> FabricBlockEntityTypeBuilder.create(blockEntityTypeFactory, getBlock()).build());
        return withBlockEntityCustomBlocksBuild(blockEntityTypeFactory, getBlock());
    }

    default RegEntryBuilder<B, G, S, R> withBlockEntityStorageProvBuild(Transferable<?, ?> trans) {
        return withBlockEntityStorageProvFunc(trans, entry -> (be, dir) -> ((StorageProv) be).getStorage(trans, dir));
    }
}
