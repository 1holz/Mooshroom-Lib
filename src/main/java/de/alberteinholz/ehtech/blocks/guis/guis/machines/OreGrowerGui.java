package de.alberteinholz.ehtech.blocks.guis.guis.machines;

import de.alberteinholz.ehtech.blocks.components.container.InventoryWrapper;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class OreGrowerGui extends MachineGui {
    protected WItemSlot oreInputSlot;

    public OreGrowerGui(int syncId, PlayerInventory playerInv, ScreenHandlerContext context) {
        this(BlockRegistry.ORE_GROWER.screenHandlerType, syncId, playerInv, context);
    }

    public OreGrowerGui(ScreenHandlerType<SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, ScreenHandlerContext context) {
        super(type, syncId, playerInv, context);
    }

    @Override
    protected void initWidgetsDependencies() {
        super.initWidgetsDependencies();
        progressBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/oregrower/elements/progress_bar_bg.png");
        progressBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/oregrower/elements/progress_bar_fg.png");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        oreInputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("seed_input"));
    }

    @Override
    public void drawDefault() {
        super.drawDefault();
        ((WGridPanel) root).add(oreInputSlot, 2, 3);
        ((WGridPanel) root).add(progressBar, 3, 3, 2, 1);
    }
}