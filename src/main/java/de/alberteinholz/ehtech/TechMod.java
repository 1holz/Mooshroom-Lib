package de.alberteinholz.ehtech;

import org.apache.logging.log4j.LogManager;

import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.registry.ItemRegistry;
import de.alberteinholz.ehtech.util.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class TechMod implements ModInitializer, ClientModInitializer {
	public static final LoggerHelper LOGGER = new LoggerHelper(LogManager.getLogger());

	@Override
	public void onInitialize() {
        BlockRegistry.registerBlocks();
		ItemRegistry.registerItems();
	}

	@Override
	public void onInitializeClient() {
        BlockRegistry.registerScreens();
    }
}
