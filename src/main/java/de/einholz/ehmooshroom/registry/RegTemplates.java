package de.einholz.ehmooshroom.registry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;

public interface RegTemplates {
    public static <B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> RegEntryBuilder<B, G, S, R> container(RegEntryBuilder<B, G, S, R> entry) {
        entry.withBlockEntityStorageProvBuild(TransferablesReg.ITEMS);
        entry.withBlockEntityStorageProvBuild(TransferablesReg.FLUIDS);
        entry.withBlockEntityStorageProvBuild(TransferablesReg.ELECTRICITY);
        return entry;
    }
}
