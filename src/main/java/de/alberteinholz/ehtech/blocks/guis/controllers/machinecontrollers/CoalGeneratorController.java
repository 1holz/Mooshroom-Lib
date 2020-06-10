package de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.CoalGeneratorDataProviderComponent;
import de.alberteinholz.ehtech.blocks.guis.widgets.Bar;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

public class CoalGeneratorController extends MachineController {
    protected Identifier heatBarBG;
    protected Identifier heatBarFG;
    protected Bar heatBar;
    protected WItemSlot coalInputSlot;

    public CoalGeneratorController(int syncId, PlayerInventory playerInv, BlockContext context) {
        super(BlockRegistry.COAL_GENERATOR.recipeType, syncId, playerInv, context);
    }

    @Override
    protected void initWidgetDependencies() {
        super.initWidgetDependencies();
        heatBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/coalgenerator/elements/heat_bar_bg.png");
        heatBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/coalgenerator/elements/heat_bar_fg.png");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        heatBar = new Bar(heatBarBG, heatBarFG, ((CoalGeneratorDataProviderComponent) getDataProviderComponent()).heat, Direction.UP);
        coalInputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("coal_input"));
    }

    @Override
    public void drawDefault() {
        super.drawDefault();
        heatBar.tooltips.add("tooltip.ehtech.coal_generator.heat_bar");
        ((WGridPanel) root).add(heatBar, 5, 2, 3, 3);
        ((WGridPanel) root).add(coalInputSlot, 2, 3);
        ((WGridPanel) root).add(progressBar, 3, 3, 2, 1);
    }
}