package de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.InventoryWrapper;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

public class OreGrowerController extends MachineController {
    protected WItemSlot oreInputSlot;

    public OreGrowerController(int syncId, PlayerInventory playerInv, BlockContext context) {
        super(BlockRegistry.ORE_GROWER.recipeType, syncId, playerInv, context);
    }

    @Override
    protected void initWidgetDependencies() {
        super.initWidgetDependencies();
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