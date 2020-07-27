package de.alberteinholz.ehmooshroom;

import de.alberteinholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "ehmooshroom";
	public static final LoggerHelper LOGGER = new LoggerHelper("https://github.com/Albert-Einholz/Mooshroom-Lib/issues");
	
    @Override
	public void onInitialize() {}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {}
}