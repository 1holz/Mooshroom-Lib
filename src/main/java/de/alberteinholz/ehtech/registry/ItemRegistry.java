package de.alberteinholz.ehtech.registry;

import de.alberteinholz.ehtech.itemgroups.ItemGroups;
import de.alberteinholz.ehtech.items.Wrench;
import de.alberteinholz.ehtech.util.Ref;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public enum ItemRegistry {
	WRENCH;

	//static

	private static void setupAll() {
		WRENCH.setup(new Wrench(getStandardItemSettings()));
	}

	private static Item.Settings getStandardItemSettings() {
        return new Item.Settings().group(ItemGroups.EH_TECH);
	}
	
	//non static

	public Item item;

	public Identifier getIdentifier() {
		return new Identifier(Ref.MOD_ID, this.toString().toLowerCase());
	}

	private void setup(Item item) {
		this.item = item;
	}

	public static void registerItems() {
		setupAll();
		for (ItemRegistry entry : ItemRegistry.values()) {
			Registry.register(Registry.ITEM, entry.getIdentifier(), entry.item);
		}
	}
}