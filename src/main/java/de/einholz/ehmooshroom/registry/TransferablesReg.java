package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.BlockApiLookups;
import de.einholz.ehmooshroom.storage.transferable.BlockVariant;
import de.einholz.ehmooshroom.storage.transferable.ElectricityVariant;
import de.einholz.ehmooshroom.storage.transferable.EntityVariant;
import de.einholz.ehmooshroom.storage.transferable.HeatVariant;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class TransferablesReg {
    public static final Identifier TRANSFERABLE_ID = MooshroomLib.HELPER.makeId("transferables");
    @SuppressWarnings("rawtypes") // FIXME is there a way to do this without suppressings warnings?
    public static final Registry<Transferable> TRANSFERABLE = FabricRegistryBuilder.createSimple(Transferable.class, TRANSFERABLE_ID).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final RegistryKey<Registry<Transferable<?, ? extends TransferVariant<?>>>> TRANSFERABLE_KEY = RegistryKey.ofRegistry(TRANSFERABLE_ID);

    public static final Transferable<Item, ItemVariant> ITEMS = registerMooshroom("items", new Transferable<Item, ItemVariant>(ItemVariant.class, TagFactory.ITEM, ItemStorage.SIDED));
    public static final Transferable<Fluid, FluidVariant> FLUIDS = registerMooshroom("fluids", new Transferable<Fluid, FluidVariant>(FluidVariant.class, TagFactory.FLUID, FluidStorage.SIDED));
    public static final Transferable<Block, BlockVariant> BLOCKS = registerMooshroom("blocks", new Transferable<Block, BlockVariant>(BlockVariant.class, TagFactory.BLOCK, BlockApiLookups.BLOCKS));
    public static final Transferable<EntityType<?>, EntityVariant> ENTITIES = registerMooshroom("entities", new Transferable<EntityType<?>, EntityVariant>(EntityVariant.class, TagFactory.ENTITY_TYPE, BlockApiLookups.ENTITIES));
    public static final Transferable<Void, ElectricityVariant> ELECTRICITY = registerMooshroom("electricity", new Transferable<Void, ElectricityVariant>(ElectricityVariant.class, null, BlockApiLookups.ELECTRICITY));
    public static final Transferable<Void, HeatVariant> HEAT = registerMooshroom("heat", new Transferable<Void, HeatVariant>(HeatVariant.class, null, BlockApiLookups.HEAT));

    private static <T, U extends TransferVariant<T>> Transferable<T, U> registerMooshroom(String str, Transferable<T, U> trans) {
        return register(MooshroomLib.HELPER.makeId(str), trans);
    }

    public static <T, U extends TransferVariant<T>> Transferable<T, U> register(Identifier id, Transferable<T, U> trans) {
        trans = Registry.register(TRANSFERABLE, id, trans);
        trans.setId(id);
        return trans;
    }
}
