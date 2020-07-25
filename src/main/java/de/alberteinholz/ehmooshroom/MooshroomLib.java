package de.alberteinholz.ehmooshroom;

import de.alberteinholz.ehmooshroom.registry.BlockRegistryEntry;
import de.alberteinholz.ehmooshroom.registry.BlockRegistryHelper;
import de.alberteinholz.ehmooshroom.registry.ItemRegistryEntry;
import de.alberteinholz.ehmooshroom.registry.ItemRegistryHelper;
import de.alberteinholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer/*, ClientModInitializer*/ {
	public static final String MOD_ID = "ehmooshroom";
	public static final LoggerHelper LOGGER = new LoggerHelper("https://github.com/Albert-Einholz/Mooshroom-Lib/issues");
	
    @Override
	public void onInitialize() {
		//Which order here?
		for (BlockRegistryEntry entry : BlockRegistryHelper.BLOCKS.values()) entry.register();
		for (ItemRegistryEntry entry : ItemRegistryHelper.ITEMS.values()) entry.register();
	}

	//Is this needed?
	/*
	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
        //BlockRegistry.registerClient();
	}
	*/
}