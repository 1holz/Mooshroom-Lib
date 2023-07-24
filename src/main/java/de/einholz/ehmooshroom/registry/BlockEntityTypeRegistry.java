package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.storage.StorageProv;
import de.einholz.ehmooshroom.storage.Transferable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockEntityApiProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypeRegistry<T extends BlockEntity>
        extends RegistryBuilder<BlockEntityType<?>> {
    public BlockEntityTypeRegistry<T> register(String name, Factory<T> factory) {
        return register(name, factory, Registry.BLOCK.get(getId()));
    }

    @SuppressWarnings("unchecked")
    public BlockEntityTypeRegistry<T> register(String name, Factory<T> factory, Block... blocks) {
        return (BlockEntityTypeRegistry<T>) register(name,
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    protected BlockEntityTypeRegistry() {
    }

    public BlockEntityTypeRegistry<T> withBlockApiLookup(Transferable<?, ?>... trans) {
        for (Transferable<?, ?> t : trans)
            withBlockApiLookup((BlockApiLookup<Storage<?>, Direction>) t.getLookup(),
                    (be, dir) -> ((StorageProv) be).getStorage(t, dir));
        return this;
    }

    public BlockEntityTypeRegistry<T> withBlockApiLookup(BlockApiLookup<Storage<?>, Direction> lookup,
            BlockEntityApiProvider<Storage<?>, Direction> provider) {
        lookup.registerForBlockEntities(provider, get());
        return this;
    }

    @Override
    protected Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }
}
