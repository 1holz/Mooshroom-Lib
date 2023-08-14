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

package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.variants.BlockVariant;
import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
import de.einholz.ehmooshroom.storage.variants.EntityVariant;
import de.einholz.ehmooshroom.storage.variants.HeatVariant;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public interface BlockApiLookups {
    public static final Identifier BLOCK_ID = Registry.BLOCK_KEY.getValue();
    public static final Identifier ELECTRICITY_ID = MooshroomLib.HELPER.makeId("electricity");
    public static final Identifier ENTITIY_TYPE_ID = Registry.ENTITY_TYPE_KEY.getValue();
    public static final Identifier FLUID_ID = Registry.FLUID_KEY.getValue();
    public static final Identifier HEAT_ID = MooshroomLib.HELPER.makeId("heat");
    public static final Identifier ITEM_ID = Registry.ITEM_KEY.getValue();

    // TODO add caches
    // XXX use ConfigTypeAcc instead of Directions?
    public static final BlockApiLookup<Storage<BlockVariant>, Direction> BLOCKS = getOrMake(BLOCK_ID);
    public static final BlockApiLookup<Storage<ElectricityVariant>, Direction> ELECTRICITY = getOrMake(ELECTRICITY_ID);
    public static final BlockApiLookup<Storage<EntityVariant>, Direction> ENTITY_TYPES = getOrMake(ENTITIY_TYPE_ID);
    public static final BlockApiLookup<Storage<HeatVariant>, Direction> HEAT = getOrMake(HEAT_ID);

    @SuppressWarnings("unchecked")
    public static <S extends Storage<?>> BlockApiLookup<S, Direction> getOrMake(
            Identifier id) {
        id = checkAlias(id);
        return (BlockApiLookup<S, Direction>) BlockApiLookup.get(id, Storage.asClass(), Direction.class);
    }

    private static Identifier checkAlias(Identifier id) {
        if (ITEM_ID.equals(id))
            return new Identifier("fabric", "sided_item_storage");
        if (FLUID_ID.equals(id))
            return new Identifier("fabric", "sided_fluid_storage");
        return id;
    }
}
