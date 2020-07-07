package de.alberteinholz.ehtech.blocks.guis.guis.machines;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import de.alberteinholz.ehtech.blocks.directionals.containers.machines.MachineBlock;
import de.alberteinholz.ehtech.blocks.guis.guis.ContainerGui;
import de.alberteinholz.ehtech.blocks.guis.widgets.Button;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class MachineConfigGui extends ContainerGui {
    protected WLabel down;
    protected WLabel up;
    protected WLabel north;
    protected WLabel south;
    protected WLabel west;
    protected WLabel east;
    protected WLabel item;
    protected WLabel fluid;
    protected WLabel power;
    protected Map<Integer, ConfigButton> configButtons;
    protected Button cancel;

    public MachineConfigGui(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(BlockRegistry.MACHINE_CONFIG.screenHandlerType, syncId, playerInventory, buf);
    }

    public MachineConfigGui(ScreenHandlerType<SyncedGuiDescription> type, int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(type, syncId, playerInventory, buf);
    }

    @Override
    protected void initWidgetsDependencies() {
        root = new WGridPanel(9);
        configButtons = new HashMap<Integer, ConfigButton>();
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        down = new WLabel(new TranslatableText("block.ehtech.machine_config.down"));
        up = new WLabel(new TranslatableText("block.ehtech.machine_config.up"));
        north = new WLabel(new TranslatableText("block.ehtech.machine_config.north"));
        south = new WLabel(new TranslatableText("block.ehtech.machine_config.south"));
        west = new WLabel(new TranslatableText("block.ehtech.machine_config.west"));
        east = new WLabel(new TranslatableText("block.ehtech.machine_config.east"));
        item = new WLabel(new TranslatableText("block.ehtech.machine_config.item"));
        fluid = new WLabel(new TranslatableText("block.ehtech.machine_config.fluid"));
        power = new WLabel(new TranslatableText("block.ehtech.machine_config.power"));
        for (ConfigType type : ConfigType.values()) {
            for (Direction dir : Direction.values()) {
                for (ConfigBehavior behavior : ConfigBehavior.values()) {
                    ConfigButton button = new ConfigButton(type, dir, behavior);
                    buttonIds.add(button);
                    configButtons.put(buttonIds.indexOf(button), button);
                    button.setOnClick(getDefaultOnButtonClick(button));
                }
            }
        }
        cancel = (Button) new Button().setLabel(new LiteralText("X"));
        cancel.tooltips.add("tooltip.ehtech.cancel_button");
        buttonIds.add(cancel);
        cancel.setOnClick(getDefaultOnButtonClick(cancel));
    }

    @Override
    protected void drawDefault() {
        ((WGridPanel) root).add(createPlayerInventoryPanel(), 0, 14);
        ((WGridPanel) root).add(down, 4, 2, 2, 2);
        ((WGridPanel) root).add(up, 6, 2, 2, 2);
        ((WGridPanel) root).add(north, 8, 2, 2, 2);
        ((WGridPanel) root).add(south, 10, 2, 2, 2);
        ((WGridPanel) root).add(west, 12, 2, 2, 2);
        ((WGridPanel) root).add(east, 14, 2, 2, 2);
        ((WGridPanel) root).add(item, 0, 4, 4, 2);
        ((WGridPanel) root).add(fluid, 0, 6, 4, 2);
        ((WGridPanel) root).add(power, 0, 8, 4, 2);
        configButtons.forEach((id, button) -> {
            ((WGridPanel) root).add(button, button.dir.ordinal() * 2 + 4 + (int) Math.floor((double) button.behavior.ordinal() / 2.0), button.type.ordinal() * 2 + 4 + (button.behavior.ordinal() + 1) % 2);
        });
        ((WGridPanel) root).add(cancel, 18, 10, 2, 2);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (configButtons.containsKey(id)) {
            ConfigButton button = configButtons.get(id);
            ((MachineDataProviderComponent) getDataProviderComponent()).changeConfig(button.type, button.behavior, button.dir);
            world.getBlockEntity(pos).markDirty();
            return true;
        } else if (id == buttonIds.indexOf(cancel)) {
            if (!world.isClient) {
                Identifier entryId = null;
                for (BlockRegistry entry : BlockRegistry.values()) {
                    if (entry.block != null && entry.block == world.getBlockState(pos).getBlock()) {
                        entryId = BlockRegistry.getId(entry);
                        break;
                    }
                }
                if (entryId == null) {
                    close(player);
                } else {
                    player.openHandledScreen((MachineBlockEntity) world.getBlockEntity(pos));
                    //FIXME
                    //ContainerProviderRegistry.INSTANCE.openContainer(entryId, player, buf -> buf.writeBlockPos(pos));

                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected MachineCapacitorComponent getCapacitorComponent() {
        return (MachineCapacitorComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null);
    }

    protected MachineDataProviderComponent getDataProviderComponent() {
        return (MachineDataProviderComponent) super.getDataProviderComponent();
    }

    protected class ConfigButton extends Button {
        public ConfigType type;
        public Direction dir;
        public ConfigBehavior behavior;

        @SuppressWarnings("unchecked")
        public ConfigButton(ConfigType type, Direction dir, ConfigBehavior behavior) {
            this.type = type;
            this.dir = dir;
            this.behavior = behavior;
            setSize(8, 8);
            resizeability = false;
            Supplier<?>[] suppliers = {
                () -> {
                    return behavior.name().toLowerCase();
                },
                () -> {
                    return dir.getName();
                },
                () -> {
                    return String.valueOf(((MachineDataProviderComponent) ((MachineBlock) world.getBlockState(pos).getBlock()).getComponent(world, pos, UniversalComponents.DATA_PROVIDER_COMPONENT, null)).getConfig(type, behavior, dir));
                },
                () -> {
                    return type.name().toLowerCase();
                }
            };
            advancedTooltips.put("tooltip.ehtech.config_button", (Supplier<Object>[]) suppliers);
        }

        @Override
        public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            withTint(getDataProviderComponent().getConfig(type, behavior, dir) ? 0xFFFFFF00 : 0xFFFF0000);
            super.draw(matrices, x, y, mouseX, mouseY);
        }
    }
}