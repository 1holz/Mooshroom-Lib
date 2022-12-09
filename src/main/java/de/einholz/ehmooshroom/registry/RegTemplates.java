package de.einholz.ehmooshroom.registry;

import java.util.function.Function;

import de.einholz.ehmooshroom.block.ContainerBlock;

public class RegTemplates {
    public static final Function<RegEntryBuilder, RegEntryBuilder> CONTAINER = (entry) -> entry.withBlockItemStorageProvRaw(((ContainerBlock) entry.getBlock())::getItemStorageProv).withBlockFluidStorageProvRaw(((ContainerBlock) entry.getBlock())::getFluidStorageProv);
}
