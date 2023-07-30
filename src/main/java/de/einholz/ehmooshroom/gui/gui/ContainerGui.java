package de.einholz.ehmooshroom.gui.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.block.entity.ContainerBE;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import de.einholz.ehmooshroom.gui.widget.Button;
import de.einholz.ehmooshroom.storage.SidedStorageMgr;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public abstract class ContainerGui extends SyncedGuiDescription {
    public final BlockPos POS;
    private List<Button> buttonIds = new ArrayList<>();
    private ContainerScreen<? extends ContainerGui> screen;
    private boolean isOpen = true;

    protected ContainerGui(ScreenHandlerType<? extends SyncedGuiDescription> type, int syncId,
            PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv);
        POS = buf.readBlockPos();
        // TODO something else needed here?
        // if (blockInventory != null) blockInventory.onOpen(playerInventory.player);
    }

    public static ContainerGui init(ContainerGui gui) {
        gui.initWidgets();
        gui.drawDefault();
        gui.finish();
        return gui;
    }

    protected void initWidgets() {
    }

    protected void drawDefault() {
        ((WGridPanel) rootPanel).add(createPlayerInventoryPanel(), 0, 7);
    }

    public void finish() {
        rootPanel.validate(this);
    }

    protected int getButtonAmount() {
        return buttonIds.size();
    }

    protected int addButton(Button button) {
        int index = getButtonAmount();
        button.setOnClick(() -> {
            MinecraftClient minecraft = getScreen().getMinecraftClient();
            minecraft.interactionManager.clickButton(syncId, index);
            onButtonClick(playerInventory.player, index);
        });
        buttonIds.add(button);
        return index;
    }

    @Nullable
    protected ContainerBE getBE() {
        BlockEntity be = world.getBlockEntity(POS);
        if (be instanceof ContainerBE container)
            return container;
        MooshroomLib.LOGGER.warnBug("Attempted to use a ContainerGUI on a", be.getClass().toString());
        return null;
    }

    @Nullable
    protected SidedStorageMgr getStorageMgr() {
        ContainerBE be = getBE();
        if (be == null) {
            MooshroomLib.LOGGER.warnBug("BE is null");
            return null;
        }
        SidedStorageMgr mgr = be.getStorageMgr();
        if (mgr != null)
            return mgr;
        MooshroomLib.LOGGER.warnBug("Can only retrieve StorageMgr from a ContainerBE");
        return null;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= getButtonAmount())
            return super.onButtonClick(player, id);
        return buttonIds.get(id).execute(player);
    }

    @Override
    public boolean canUse(PlayerEntity entity) {
        // TODO something else needed here?
        return true; // blockInventory != null ? blockInventory.canPlayerUse(entity) : true;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public ContainerScreen<? extends ContainerGui> getScreen() {
        return screen;
    }

    public void setScreen(ContainerScreen<? extends ContainerGui> screen) {
        this.screen = screen;
    }
}
