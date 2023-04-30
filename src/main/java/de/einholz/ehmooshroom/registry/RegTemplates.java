package de.einholz.ehmooshroom.registry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;

public class RegTemplates {
    public static <B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>> RegEntryBuilder<B, G, S> container(RegEntryBuilder<B, G, S> entry) {
        //entry.withBlockItemStorageProvRaw(((ContainerBlock) entry.getBlock())::getItemStorageProv);
        //entry.withBlockFluidStorageProvRaw(((ContainerBlock) entry.getBlock())::getFluidStorageProv);
        entry.withBlockEntityItemStorageProvBuild();
        entry.withBlockEntityItemStorageProvBuild();
        return entry;
    }
}
