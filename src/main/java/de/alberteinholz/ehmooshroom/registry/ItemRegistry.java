package de.alberteinholz.ehmooshroom.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ItemRegistry {
    public static void registerMain() {
        /*
        BLOCKS.forEach((id, entry) -> {
            entry.registerMain();
        });
        */
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        /*
        BLOCKS.forEach((id, entry) -> {
            entry.registerClient();
        });
        */
    }
}