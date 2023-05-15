package de.einholz.ehmooshroom.gui.gui;

import java.util.ArrayList;
import java.util.List;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.block.entity.ContainerBE;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import de.einholz.ehmooshroom.storage.SidedStorageMgr;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
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
    public List<WButton> buttonIds;
    private ContainerScreen<? extends ContainerGui> screen;
    
    protected ContainerGui(ScreenHandlerType<? extends SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv);
        POS = buf.readBlockPos();

        // TODO something else needed here?
        // if (blockInventory != null) blockInventory.onOpen(playerInventory.player);
    }

    public static ContainerGui init(ContainerGui gui) {
        gui.buttonIds = new ArrayList<WButton>();
        gui.initWidgets();
        gui.drawDefault();
        gui.finish();
        return gui;
    }

    protected void initWidgets() {}

    protected void drawDefault() {
        ((WGridPanel) rootPanel).add(createPlayerInventoryPanel(), 0, 7);
    }

    public void finish() {
        rootPanel.validate(this);
    }

    protected Runnable getDefaultOnButtonClick(WButton button) {
        return () -> {
            MinecraftClient minecraft = getScreen().getMinecraftClient();
            minecraft.interactionManager.clickButton(syncId, buttonIds.indexOf(button));
            onButtonClick(playerInventory.player, buttonIds.indexOf(button));
        };
    }

    //@Nullable
    protected ContainerBE getBE() {
        BlockEntity be = world.getBlockEntity(POS);
        if (be instanceof ContainerBE container) return container;
        MooshroomLib.LOGGER.smallBug(new IllegalStateException("Attempted to use a ContainerGUI on a " + be.getClass().toString()));
        return null;
    }

    //@Nullable
    protected SidedStorageMgr getStorageMgr() {
        SidedStorageMgr mgr = getBE().getStorageMgr();
        if (mgr != null) return mgr;
        MooshroomLib.LOGGER.smallBug(new IllegalStateException("Can only retrieve StorageMgr from ContainerBE"));
        return null;
    }

    // TODO make faster using mixins
    /*
    @Override
    public void onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        if (!SlotActionType.QUICK_MOVE.equals(action)) super.onSlotClick(slotNumber, button, action, player);
        if (slotNumber < 0 || slotNumber >= slots.size()) return;
        Slot slot = slots.get(slotNumber);
        if (slot == null || !slot.canTakeItems(player)) return;
        ItemStack remaining = ItemStack.EMPTY;
        if (slot == null || !slot.hasStack()) return;
        ItemStack trans = slot.getStack();
        remaining = trans.copy();

        if (blockInventory == null) return;
        // TODO implement properly
        // if (slot.inventory == blockInventory) {
        //     if (!insertItem(trans, playerInventory, true, player)) return;
        //     else if (!insertItem(trans, blockInventory, false, player)) return;
        // } else if (!swapHotbar(trans, slotNumber, playerInventory, player)) return;
        if (trans.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();
    }
    */

    /*
    private boolean insertItem(ItemStack toInsert, Inventory inventory, boolean walkBackwards, PlayerEntity player) {
        ArrayList<Slot> inventorySlots = new ArrayList<>();
        Iterator<Slot> iter = slots.iterator();
        while(iter.hasNext()) {
            Slot slot = iter.next();
            if (slot.inventory == inventory) inventorySlots.add(slot);
        }
        if (inventorySlots.isEmpty()) return false;
        else {
            boolean inserted = false;
            Slot curSlot;
            int i;
            if (walkBackwards) {
               for(i = inventorySlots.size() - 1; i >= 0; --i) {
                  curSlot = (Slot)inventorySlots.get(i);
                  if (this.insertIntoExisting(toInsert, curSlot, player)) inserted = true;
                  if (toInsert.isEmpty()) break;
               }
            } else {
               for(i = 0; i < inventorySlots.size(); ++i) {
                  curSlot = (Slot)inventorySlots.get(i);
                  if (this.insertIntoExisting(toInsert, curSlot, player)) inserted = true;
                  if (toInsert.isEmpty()) break;
               }
            }
            if (!toInsert.isEmpty()) {
               if (walkBackwards) {
                  for(i = inventorySlots.size() - 1; i >= 0; --i) {
                     curSlot = (Slot)inventorySlots.get(i);
                     if (this.insertIntoEmpty(toInsert, curSlot)) inserted = true;
                     if (toInsert.isEmpty()) break;
                  }
               } else {
                  for(i = 0; i < inventorySlots.size(); ++i) {
                     curSlot = (Slot)inventorySlots.get(i);
                     if (this.insertIntoEmpty(toInsert, curSlot)) inserted = true;
                     if (toInsert.isEmpty()) break;
                  }
               }
            }
            return inserted;
        }
    }
    */

    @Override
    public boolean canUse(PlayerEntity entity) {
        // TODO something else needed here?
        return true; // blockInventory != null ? blockInventory.canPlayerUse(entity) : true;
    }

    @Override
    public void close(PlayerEntity player) {
        // TODO something else needed here?
        return;
        // super.close(player);
        // if (blockInventory != null) blockInventory.onClose(player);
    }

    public ContainerScreen<? extends ContainerGui> getScreen() {
        return screen;
    }

    public void setScreen(ContainerScreen<? extends ContainerGui> screen) {
        this.screen = screen;
    }
}
