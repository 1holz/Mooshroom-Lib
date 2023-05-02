package de.einholz.ehmooshroom.gui.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.gui.widget.Button;
import de.einholz.ehmooshroom.storage.SidedStorageMgr.SideConfigType;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SideConfigGui extends ContainerGui {
    protected WLabel acc;
    /* TODO del
    protected WLabel gui;
    protected WLabel process;
    protected WLabel down;
    protected WLabel up;
    protected WLabel north;
    protected WLabel south;
    protected WLabel west;
    protected WLabel east;
    */
    protected WListPanel<Identifier, ConfigEntry> configPanel;
    // XXX WListPanel might already have a scroll bar included
    //protected WScrollPanel scrollPanel;
    protected List<Identifier> configIds;
    /* FIXME check this class for content that can be removed
    protected WLabel item;
    protected WLabel fluid;
    protected WLabel power;
    */
    protected Map<Integer, ConfigButton> configButtons;
    protected Button cancel;

    // FIXME buttons and/or labels are misaligned
    protected SideConfigGui(ScreenHandlerType<? extends SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv, buf);
    }

    @SuppressWarnings("unchecked")
    public static SideConfigGui init(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        return init(new SideConfigGui((ScreenHandlerType<SyncedGuiDescription>) Registry.SCREEN_HANDLER.get(MooshroomLib.HELPER.makeId("side_config")), syncId, playerInventory, buf));
    }

    public static SideConfigGui init(SideConfigGui gui) {
        gui.acc = new WLabel(new TranslatableText("block.ehmooshroom.side_config.acc"));
        /* TODO del
        gui.gui = new WLabel(new TranslatableText("block.ehmooshroom.side_config.gui"));
        gui.process = new WLabel(new TranslatableText("block.ehmooshroom.side_config.process"));
        gui.down = new WLabel(new TranslatableText("block.ehmooshroom.side_config.down"));
        gui.up = new WLabel(new TranslatableText("block.ehmooshroom.side_config.up"));
        gui.north = new WLabel(new TranslatableText("block.ehmooshroom.side_config.north"));
        gui.south = new WLabel(new TranslatableText("block.ehmooshroom.side_config.south"));
        gui.west = new WLabel(new TranslatableText("block.ehmooshroom.side_config.west"));
        gui.east = new WLabel(new TranslatableText("block.ehmooshroom.side_config.east"));
        */
        gui.configIds = gui.getBE().getStorageMgr().getIds();
        // XXX: WHY DOES ConfigEntry::gui.new NOT WORK??? THIS REALLY SHOULD WORK!!!
        gui.configPanel = new WListPanel<>(gui.configIds, () -> gui.new ConfigEntry(), (id, entry) -> entry.build(id));
        //gui.scrollPanel = new WScrollPanel(gui.configPanel);
        gui.configButtons = new HashMap<Integer, ConfigButton>();
        gui.cancel = (Button) new Button().setLabel(new LiteralText("X"));
        return (SideConfigGui) ContainerGui.init(gui);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        /* TODO del
        item = new WLabel(new TranslatableText("block.ehmooshroom.side_config.item"));
        fluid = new WLabel(new TranslatableText("block.ehmooshroom.side_config.fluid"));
        power = new WLabel(new TranslatableText("block.ehmooshroom.side_config.power"));
        for (Identifier id : getStorageMgr().getIds()) for (SideConfigType type : SideConfigType.values()) {
            ConfigButton button = new ConfigButton(buttonIds.size(), id, type);
            buttonIds.add(button);
            configButtons.put(button.buttonId, button);
            if (!getStorageMgr().getStorageEntry(id).available(type)) button.setEnabled(false);
            else button.setOnClick(getDefaultOnButtonClick(button));
        }
        */
        configPanel.setSize(9, 5);
        // TODO uncomment scrollPanel.setScrollingHorizontally(TriState.FALSE);
        cancel.tooltips.add("tooltip.ehmooshroom.cancel_button");
        buttonIds.add(cancel);
        cancel.setOnClick(getDefaultOnButtonClick(cancel));
    }

    @Override
    protected void drawDefault() {
        super.drawDefault();
        ((WGridPanel) rootPanel).add(acc, 2, 1, 7, 1);
        /* TODO del
        ((WGridPanel) rootPanel).add(gui, 2, 1, 1, 1);
        ((WGridPanel) rootPanel).add(process, 3, 1, 1, 1);
        ((WGridPanel) rootPanel).add(down, 4, 1, 1, 1);
        ((WGridPanel) rootPanel).add(up, 5, 1, 1, 1);
        ((WGridPanel) rootPanel).add(north, 6, 1, 1, 1);
        ((WGridPanel) rootPanel).add(south, 7, 1, 1, 1);
        ((WGridPanel) rootPanel).add(west, 8, 1, 1, 1);
        ((WGridPanel) rootPanel).add(east, 9, 1, 1, 1);
        ((WGridPanel) rootPanel).add(item, 0, 4, 4, 2);
        ((WGridPanel) rootPanel).add(fluid, 0, 6, 4, 2);
        ((WGridPanel) rootPanel).add(power, 0, 8, 4, 2);
        configButtons.forEach((id, button) -> {
            ((WGridPanel) rootPanel).add(button, button.type.ACC.ordinal() * 2 + 4 + (button.type.OUTPUT ? 1 : 0), button.TYPE.ordinal() * 2 + 4 + (button.behavoir.ordinal() + 1) % 2);
        });
        */
        ((WGridPanel) rootPanel).add(configPanel, 0, 2, 9, 5);
        ((WGridPanel) rootPanel).add(cancel, 9, 5, 1, 1);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (configButtons.containsKey(id)) {
            ConfigButton button = configButtons.get(id);
            if (button.isEnabled()) {
                getBE().getStorageMgr().getStorageEntry(button.storageId).change(button.type);
                return true;
            }
        } else if (id == buttonIds.indexOf(cancel) && world.getBlockEntity(POS) instanceof NamedScreenHandlerFactory screenFactory) {
            if (!world.isClient) player.openHandledScreen(screenFactory);
            return true;
        }
        return false;
    }

    /*
    @Deprecated
    protected MachineDataComponent getMachineDataComp() {
        return (MachineDataComponent) getDataComp().getComp(MooshroomLib.HELPER.makeId("data_machine"));
    }

    @Deprecated
    protected AdvancedCapacitorComponent getCapacitorComp() {
        return (AdvancedCapacitorComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null);
    }

    @Deprecated
    protected AdvancedInventoryComponent getMachineInvComp() {
        return (AdvancedInventoryComponent) getInvComp().getComp(MooshroomLib.HELPER.makeId("inventory_machine"));
    }
    */

    protected class ConfigEntry extends WGridPanel {
        public ConfigEntry() {
            super(9);
        }

        public void build(Identifier id) {
            add(new WLabel(new TranslatableText("block." + id.getNamespace() + ".config." + id.getPath())), 0, 0, 4, 2);
            for (SideConfigType type : SideConfigType.values()) {
                ConfigButton button = new ConfigButton(buttonIds.size(), id, type);
                buttonIds.add(button);
                configButtons.put(buttonIds.indexOf(button), button);
                if (getStorageMgr().getStorageEntry(id).available(type));
                else button.setOnClick(getDefaultOnButtonClick(button));
                final int ACC_NO = button.type.ACC.ordinal();
                add(button, (ACC_NO > 1 ? 2 * (ACC_NO - 1) : ACC_NO) + 4 + (button.type.FOREIGN ? 1 : 0), button.type.OUTPUT ? 1 : 0);
            }
        }
    }

    protected class ConfigButton extends Button {
        public final int buttonId;
        public final Identifier storageId;
        public final SideConfigType type;

        @SuppressWarnings("unchecked")
        public ConfigButton(int buttonId, Identifier storageId, SideConfigType type) {
            this.buttonId = buttonId;
            this.storageId = storageId;
            this.type = type;
            setSize(8, 8);
            if (!isEnabled()) return;
            Supplier<?>[] suppliers = {
                () -> {
                    return type.name().toLowerCase();
                }, () -> {
                    return type.ACC.toString();
                }, () -> {
                    return String.valueOf(getStorageMgr().getStorageEntry(storageId).allows(type));
                }, () -> {
                    return String.valueOf(buttonId);
                }
            };
            advancedTooltips.put("tooltip.ehmooshroom.config_button", (Supplier<Object>[]) suppliers);
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (isEnabled())
                withTint(getStorageMgr().getStorageEntry(storageId).allows(type) ? 0xFFFFFF00 : 0xFFFF0000);
            else advancedTooltips.remove("tooltip.ehmooshroom.config_button");
            super.draw(matrices, x, y, mouseX, mouseY);
        }

        @Override
        public boolean canResize() {
            return false;
        }
    }
}
