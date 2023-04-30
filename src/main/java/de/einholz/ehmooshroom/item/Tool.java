package de.einholz.ehmooshroom.item;

import net.minecraft.item.Item;

public class Tool extends Item {
    public Tool(Settings settings) {
        super(settings.maxCount(1));
    }
}
