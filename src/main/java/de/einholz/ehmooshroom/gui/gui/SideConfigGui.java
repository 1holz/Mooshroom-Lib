package de.einholz.ehmooshroom.gui.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.gui.widget.Button;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class SideConfigGui extends ContainerGui {
    private final Supplier<ConfigEntry> CONFIG_SUPPLIER = ConfigEntry::new;
    protected WLabel down, up, north, south, west, east, internal;
    protected WListPanel<Identifier, ConfigEntry> configPanel;
    protected List<Identifier> configIds;
    protected BiConsumer<Identifier, ConfigEntry> configBuilder;
    /* FIXME check this class for content that can be removed
    protected WLabel item;
    protected WLabel fluid;
    protected WLabel power;
    */
    protected Map<Integer, ConfigButton> configButtons;
    protected Button cancel;

    // FIXME buttons and/or labels are misaligned
    protected SideConfigGui(ScreenHandlerType<SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv, buf);
    }

    @SuppressWarnings("unchecked")
    public static SideConfigGui init(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        return init(new SideConfigGui((ScreenHandlerType<SyncedGuiDescription>) Registry.SCREEN_HANDLER.get(MooshroomLib.HELPER.makeId("side_config")), syncId, playerInventory, buf));
    }

    public static SideConfigGui init(SideConfigGui gui) {
        gui.down = new WLabel(new TranslatableText("block.ehtech.machine_config.down"));
        gui.up = new WLabel(new TranslatableText("block.ehtech.machine_config.up"));
        gui.north = new WLabel(new TranslatableText("block.ehtech.machine_config.north"));
        gui.south = new WLabel(new TranslatableText("block.ehtech.machine_config.south"));
        gui.west = new WLabel(new TranslatableText("block.ehtech.machine_config.west"));
        gui.east = new WLabel(new TranslatableText("block.ehtech.machine_config.east"));
        gui.internal = new WLabel(new TranslatableText("block.ehtech.machine_config.internal"));
        gui.configIds = gui.getConfigComp().getIds();
        gui.configBuilder = (id, entry) -> entry.build(id);
        // XXX: WHY DOES ConfigEntry::gui.new NOT WORK INSTEAD OF gui.CONFIG_SUPPLIER??? THIS REALLY SHOULD WORK!!!!!
        gui.configPanel = new WListPanel<>(gui.configIds, gui.CONFIG_SUPPLIER, gui.configBuilder);
        gui.configButtons = new HashMap<Integer, ConfigButton>();
        gui.cancel = (Button) new Button().setLabel(new LiteralText("X"));
        return (SideConfigGui) ContainerGui.init(gui);
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        /*
        item = new WLabel(new TranslatableText("block.ehtech.machine_config.item"));
        fluid = new WLabel(new TranslatableText("block.ehtech.machine_config.fluid"));
        power = new WLabel(new TranslatableText("block.ehtech.machine_config.power"));
        for (Identifier id : ConfigType.values()) for (Direction dir : Direction.values()) for (SideConfigType behavior : SideConfigType.values()) {
            ConfigButton button = new ConfigButton(id, dir, behavior);
            buttonIds.add(button);
            button.id = buttonIds.indexOf(button);
            configButtons.put(button.id, button);
            if (getConfigComp().getConfig(id, behavior, dir) == null) button.setEnabled(false);
            else button.setOnClick(getDefaultOnButtonClick(button));
        }
        */
        cancel.tooltips.add("tooltip.ehtech.cancel_button");
        buttonIds.add(cancel);
        cancel.setOnClick(getDefaultOnButtonClick(cancel));
    }

    @Override
    protected void drawDefault() {
        super.drawDefault();
        ((WGridPanel) rootPanel).add(down, 2, 1, 1, 1);
        ((WGridPanel) rootPanel).add(up, 3, 1, 1, 1);
        ((WGridPanel) rootPanel).add(north, 4, 1, 1, 1);
        ((WGridPanel) rootPanel).add(south, 5, 1, 1, 1);
        ((WGridPanel) rootPanel).add(west, 6, 1, 1, 1);
        ((WGridPanel) rootPanel).add(east, 7, 1, 1, 1);
        // TODO internal
        /*
        ((WGridPanel) rootPanel).add(item, 0, 4, 4, 2);
        ((WGridPanel) rootPanel).add(fluid, 0, 6, 4, 2);
        ((WGridPanel) rootPanel).add(power, 0, 8, 4, 2);
        configButtons.forEach((id, button) -> {
            ((WGridPanel) rootPanel).add(button, button.dir.ordinal() * 2 + 4 + (int) Math.floor((double) button.behavoir.ordinal() / 2.0), button.TYPE.ordinal() * 2 + 4 + (button.behavoir.ordinal() + 1) % 2);
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
                getConfigComp().changeConfig(button.id, button.configType, button.dir);
                return true;
            }
        } else if (id == buttonIds.indexOf(cancel)) {
            if (!world.isClient) player.openHandledScreen((MachineBlockEntity) world.getBlockEntity(pos));
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
        public Identifier id;
        //TODO: is this needed?
        public List<ConfigButton> buttons = new ArrayList<>();

        public ConfigEntry() {
            grid = 9;
        }

        public void build(Identifier id) {
            this.id = id;
            add(new WLabel(new TranslatableText("block." + id.getNamespace() + ".machine_config." + id.getPath())), 0, 0, 4, 2);
            for (Direction dir : Direction.values()) for (SideConfigType behavior : SideConfigType.values()) {
                ConfigButton button = new ConfigButton(id, dir, behavior);
                buttonIds.add(button);
                //FIXME: delete: button.id = buttonIds.indexOf(button);
                configButtons.put(buttonIds.indexOf(button), button);
                if (!getConfigComp().isAvailable(id, behavior, dir)) button.setEnabled(false);
                else button.setOnClick(getDefaultOnButtonClick(button));
                add(button, button.dir.ordinal() * 2 + 4 + (int) Math.floor((double) button.configType.ordinal() / 2.0), (button.configType.ordinal() + 1) % 2);
            }
        }
    }

    protected class ConfigButton extends Button {
        public final Identifier id;
        public final Direction dir;
        public final SideConfigType configType;

        @SuppressWarnings("unchecked")
        public ConfigButton(Identifier id, Direction dir, SideConfigType configType) {
            this.id = id;
            this.dir = dir;
            this.configType = configType;
            setSize(8, 8);
            resizeability = false;
            if (!isEnabled()) return;
            Supplier<?>[] suppliers = {
                () -> {
                    return configType.name().toLowerCase();
                }, () -> {
                    return dir.getName();
                }, () -> {
                    return String.valueOf(getConfigComp().allowsConfig(id, configType, dir));
                }, () -> {
                    return id.toString();
                }
            };
            advancedTooltips.put("tooltip.ehtech.config_button", (Supplier<Object>[]) suppliers);
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            if (isEnabled()) withTint(getConfigComp().allowsConfig(id, configType, dir) ? 0xFFFFFF00 : 0xFFFF0000);
            else advancedTooltips.remove("tooltip.ehtech.config_button");
            super.draw(matrices, x, y, mouseX, mouseY);
        }
    }
}
