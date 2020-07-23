package de.alberteinholz.ehmooshroom;

import de.alberteinholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer, ClientModInitializer {
	public static final LoggerHelper LOGGER = new LoggerHelper("wip");
	public static final String MOD_ID = "ehmooshroom";

    @Override
	public void onInitialize() {
        //BlockRegistry.registerMain();
		//ItemRegistry.registerMain();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
        //BlockRegistry.registerClient();
    }
}