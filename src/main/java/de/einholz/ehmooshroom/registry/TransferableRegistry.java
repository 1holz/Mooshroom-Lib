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

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.BlockApiLookups;
import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.storage.variants.BlockVariant;
import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
import de.einholz.ehmooshroom.storage.variants.EntityVariant;
import de.einholz.ehmooshroom.storage.variants.HeatVariant;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class TransferableRegistry<T, U extends TransferVariant<T>> extends RegistryBuilder<Transferable<T, U>> {
    public static final Identifier TRANSFERABLE_ID = MooshroomLib.HELPER.makeId("transferables");
    // FIXME for >1.19.3 there is a solution to this mess see:
    // https://github.com/FabricMC/fabric/issues/846
    @SuppressWarnings("rawtypes") // FIXME is there a way to do this without suppressings warnings?
    public static final Registry TRANSFERABLE = FabricRegistryBuilder
            .createSimple(Transferable.class, TRANSFERABLE_ID)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final RegistryKey<Registry<Transferable<?, ? extends TransferVariant<?>>>> TRANSFERABLE_KEY = RegistryKey
            .ofRegistry(TRANSFERABLE_ID);

    public static final Transferable<Item, ItemVariant> ITEMS = new TransferableRegistry<Item, ItemVariant>()
            .register(Registry.ITEM_KEY, ItemStorage.SIDED)
            .get();
    public static final Transferable<Fluid, FluidVariant> FLUIDS = new TransferableRegistry<Fluid, FluidVariant>()
            .register(Registry.FLUID_KEY, FluidStorage.SIDED)
            .get();
    public static final Transferable<Block, BlockVariant> BLOCKS = new TransferableRegistry<Block, BlockVariant>()
            .register(Registry.BLOCK_KEY, BlockApiLookups.BLOCKS)
            .get();
    public static final Transferable<EntityType<?>, EntityVariant> ENTITIES = new TransferableRegistry<EntityType<?>, EntityVariant>()
            .register(Registry.ENTITY_TYPE_KEY, BlockApiLookups.ENTITIES)
            .get();
    public static final Transferable<Void, ElectricityVariant> ELECTRICITY = new TransferableRegistry<Void, ElectricityVariant>()
            .register(Transferable.ELECTRICITY_ID, BlockApiLookups.ELECTRICITY)
            .get();
    public static final Transferable<Void, HeatVariant> HEAT = new TransferableRegistry<Void, HeatVariant>()
            .register(Transferable.HEAT_ID, BlockApiLookups.HEAT)
            .get();

    public TransferableRegistry<T, U> register(RegistryKey<? extends Registry<T>> key, @Nullable BlockApiLookup<? extends Storage<U>, Direction> lookup) {
        return (TransferableRegistry<T, U>) register(key.getValue(), new Transferable<>((RegistryKey<Registry<T>>) key, lookup));
    }

    public TransferableRegistry<T, U> register(Identifier id, @Nullable BlockApiLookup<? extends Storage<U>, Direction> lookup) {
        return (TransferableRegistry<T, U>) register(id, new Transferable<>(id, lookup));
    }

    protected TransferableRegistry() {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Registry<Transferable<T, U>> getRegistry() {
        return TRANSFERABLE;
    }

    public static void registerAll() {
    }
}
