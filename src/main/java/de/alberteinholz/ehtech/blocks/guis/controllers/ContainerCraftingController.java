package de.alberteinholz.ehtech.blocks.guis.controllers;

import java.util.ArrayList;
import java.util.List;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.guis.screens.EHContainerScreen;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public abstract class ContainerCraftingController extends CottonCraftingController {
    protected BlockPos pos;
    protected WPanel root;
    protected List<WButton> buttonIds = new ArrayList<WButton>();
    protected WLabel containerTitle;
    protected WLabel playerInventoryTitle;
    public EHContainerScreen screen;

    public ContainerCraftingController(int syncId, PlayerInventory playerInv, BlockContext context) {
        this(null, syncId, playerInv, context);
    }

    public ContainerCraftingController(RecipeType<?> type, int syncId, PlayerInventory playerInv, BlockContext context) {
        super(type, syncId, playerInv);
        context.run((world, pos) -> {
            ContainerCraftingController.this.pos = pos;
        });
        blockInventory = getInventoryComponent().asInventory();
        initWidgetsDependencies();
        setRootPanel(root);
        initWidgets();
        drawDefault();
        finish();
    }

    protected void initWidgetsDependencies() {
        root = new WGridPanel();
    }

    protected void initWidgets() {
        containerTitle = new WLabel(new TranslatableText(getDataProviderComponent().getContainerName()));
        playerInventoryTitle = new WLabel(playerInventory.getDisplayName().asFormattedString());
    }

    protected void drawDefault() {
        ((WGridPanel) root).add(containerTitle, 0, 0);
        ((WGridPanel) root).add(playerInventoryTitle, 0, 6);
        ((WGridPanel) root).add(createPlayerInventoryPanel(), 0, 7);
    }

    public void finish() {
        rootPanel.validate(this);
    }

    protected ContainerInventoryComponent getInventoryComponent() {
        return (ContainerInventoryComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, null);
    }

    protected ContainerDataProviderComponent getDataProviderComponent() {
        return (ContainerDataProviderComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.DATA_PROVIDER_COMPONENT, null);
    }
}