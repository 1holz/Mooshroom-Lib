package de.alberteinholz.ehtech.items;

import net.minecraft.item.Item;

public abstract class Tool extends Item {
    public Tool(Settings settings) {
        super(settings.maxCount(1));
    }
}