package de.alberteinholz.ehtech.blocks.guis.guis;

import java.util.ArrayList;
import java.util.List;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.guis.screens.ContainerScreen;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public abstract class ContainerGui extends SyncedGuiDescription {
    protected BlockPos pos;
    protected WPanel root;
    protected List<WButton> buttonIds = new ArrayList<WButton>();
    protected WLabel containerTitle;
    protected WLabel playerInventoryTitle;
    public ContainerScreen screen;

    public ContainerGui(int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        this(null, syncId, playerInv, buf);
    }

    public ContainerGui(ScreenHandlerType<SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv);
        pos = buf.readBlockPos();
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
        playerInventoryTitle = new WLabel(playerInventory.getDisplayName().getString());
    }

    protected void drawDefault() {
        ((WGridPanel) root).add(containerTitle, 0, 0);
        ((WGridPanel) root).add(playerInventoryTitle, 0, 6);
        ((WGridPanel) root).add(createPlayerInventoryPanel(), 0, 7);
    }

    public void finish() {
        rootPanel.validate(this);
    }

    protected Runnable getDefaultOnButtonClick(WButton button) {
        return () -> {
            MinecraftClient minecraft = screen.getMinecraftClient();
            minecraft.interactionManager.clickButton(syncId, buttonIds.indexOf(button));
            onButtonClick(playerInventory.player, buttonIds.indexOf(button));
        };
    }

    protected ContainerInventoryComponent getInventoryComponent() {
        return (ContainerInventoryComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.INVENTORY_COMPONENT, null);
    }

    protected ContainerDataProviderComponent getDataProviderComponent() {
        return (ContainerDataProviderComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.DATA_PROVIDER_COMPONENT, null);
    }
}