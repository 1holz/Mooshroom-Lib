package de.einholz.ehmooshroom;

import de.einholz.ehmooshroom.registry.RecipeTypeRegistry;
import de.einholz.ehmooshroom.registry.ScreenHandlerRegistry;
import de.einholz.ehmooshroom.registry.TransferableRegistry;
import de.einholz.ehmooshroom.util.Helper;
import de.einholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer, ClientModInitializer {
    public static final Helper HELPER = new Helper("ehmooshroom");
    public static final LoggerHelper LOGGER = new LoggerHelper(HELPER.MOD_ID,
            "https://github.com/Albert-Einholz/Mooshroom-Lib/issues");

    @Override
    public void onInitialize() {
        TransferableRegistry.registerAll();
        RecipeTypeRegistry.registerAll();
        ScreenHandlerRegistry.registerAll();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
    }
}
