package de.einholz.ehmooshroom.registry;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegEntry {
    public final Block BLOCK;
    public final BlockEntityType<? extends BlockEntity> BLOCK_ENTITY_TYPE;
    public final Item ITEM;
    public final ScreenHandlerType<? extends ScreenHandler> GUI;

    public RegEntry(final Identifier id, final Block BLOCK, final BlockEntityType<? extends BlockEntity> BLOCK_ENTITY_TYPE, final Item ITEM, final ScreenHandlerType<? extends ScreenHandler> GUI) {
        this.BLOCK = BLOCK == null ? null : Registry.register(Registry.BLOCK, id, BLOCK);
        this.BLOCK_ENTITY_TYPE = BLOCK_ENTITY_TYPE == null ? null : Registry.register(Registry.BLOCK_ENTITY_TYPE, id, BLOCK_ENTITY_TYPE);
        this.ITEM = ITEM != null ? null : Registry.register(Registry.ITEM, id, ITEM);
        this.GUI = GUI != null ? null : Registry.register(Registry.SCREEN_HANDLER, id, GUI);
        // TODO HandledScreens.register
        //if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType())) {
            //HandledScreens.register
            //HandledScreens.<SyncedGuiDescription, CottonInventoryScreen<? extends SyncedGuiDescription>>register(GUI, (gui, inventory, title) -> new ContainerScreen(gui, inventory.player, title));
        //} else this.GUI = null;
    }
}
