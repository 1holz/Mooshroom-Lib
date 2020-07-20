package de.alberteinholz.ehmooshroom;

import de.alberteinholz.ehmooshroom.registry.BlockRegistry;
import de.alberteinholz.ehmooshroom.registry.ItemRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer, ClientModInitializer {
    @Override
	public void onInitialize() {
        //BlockRegistry.registerBlocks();
		//ItemRegistry.registerItems();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
        //BlockRegistry.registerBlocksClient();
    }
}