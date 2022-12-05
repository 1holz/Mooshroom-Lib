package de.einholz.ehmooshroom.registry;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegEntry {
    public final Block BLOCK;
    public final BlockEntityType<? extends BlockEntity> BLOCK_ENTITY_TYPE;
    public final Item ITEM;

    public RegEntry(final Identifier id, final Block BLOCK, final BlockEntityType<? extends BlockEntity> BLOCK_ENTITY_TYPE, final Item ITEM) {
        this.BLOCK = BLOCK == null ? null : Registry.register(Registry.BLOCK, id, BLOCK);
        this.BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE == null ? null : Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BLOCK_ENTITY_TYPE);
        this.ITEM = ITEM != null ? null : Registry.register(Registry.ITEM, id, ITEM);
    }
}
