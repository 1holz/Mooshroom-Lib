/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.storage.BlockApiLookups;
import de.einholz.ehmooshroom.storage.StorageProv;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockEntityApiProvider;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypeRegistry<T extends BlockEntity>
        extends RegistryBuilder<BlockEntityType<?>> {
    public BlockEntityTypeRegistry<T> register(String name, Factory<T> factory) {
        return register(name, factory, Registry.BLOCK.get(idFactory().apply(name)));
    }

    @SuppressWarnings("unchecked")
    public BlockEntityTypeRegistry<T> register(String name, Factory<T> factory, Block... blocks) {
        return (BlockEntityTypeRegistry<T>) register(name,
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    protected BlockEntityTypeRegistry() {
    }

    public BlockEntityTypeRegistry<T> withBlockApiLookup(Identifier... ids) {
        for (Identifier id : ids)
            withBlockApiLookup(BlockApiLookups.getOrMake(id), (be, dir) -> ((StorageProv) be).getStorage(id, dir));
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
