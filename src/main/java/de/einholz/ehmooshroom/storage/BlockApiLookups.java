package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.variants.BlockVariant;
import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
import de.einholz.ehmooshroom.storage.variants.EntityVariant;
import de.einholz.ehmooshroom.storage.variants.HeatVariant;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.util.math.Direction;

public class BlockApiLookups {
    // TODO add caches
    // XXX use ConfigTypeAcc instead of Directions?
    public static final BlockApiLookup<Storage<BlockVariant>, Direction> BLOCKS = BlockApiLookup.get(MooshroomLib.HELPER.makeId(""), Storage.asClass(), Direction.class);
    public static final BlockApiLookup<Storage<EntityVariant>, Direction> ENTITIES = BlockApiLookup.get(MooshroomLib.HELPER.makeId(""), Storage.asClass(), Direction.class);
    public static final BlockApiLookup<Storage<ElectricityVariant>, Direction> ELECTRICITY = BlockApiLookup.get(MooshroomLib.HELPER.makeId(""), Storage.asClass() , Direction.class);
    public static final BlockApiLookup<Storage<HeatVariant>, Direction> HEAT = BlockApiLookup.get(MooshroomLib.HELPER.makeId(""), Storage.asClass(), Direction.class);
}
