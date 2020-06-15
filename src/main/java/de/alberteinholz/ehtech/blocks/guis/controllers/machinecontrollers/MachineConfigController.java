package de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers;

import java.util.HashMap;
import java.util.Map;

import de.alberteinholz.ehtech.blocks.components.container.machine.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.guis.controllers.ContainerCraftingController;
import de.alberteinholz.ehtech.blocks.guis.widgets.Button;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;

public class MachineConfigController extends ContainerCraftingController {
    protected WLabel down;
    protected WLabel up;
    protected WLabel north;
    protected WLabel south;
    protected WLabel west;
    protected WLabel east;
    protected WLabel item;
    protected Map<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>> itemConfig;
    protected WLabel fluid;
    protected Map<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>> fluidConfig;
    protected WLabel power;
    protected Map<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>> powerConfig;

    public MachineConfigController(int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(syncId, playerInventory, context);
    }

    @Override
    protected void initWidgetsDependencies() {
        root = new WGridPanel(9);
        itemConfig = new HashMap<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>>();
        fluidConfig = new HashMap<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>>();
        powerConfig = new HashMap<Direction, Map<MachineDataProviderComponent.ConfigBehavior, Button>>();
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
        for (Direction dir : Direction.values()) {
            Map<MachineDataProviderComponent.ConfigBehavior, Button> map = new HashMap<MachineDataProviderComponent.ConfigBehavior, Button>();
            for (MachineDataProviderComponent.ConfigBehavior behavior : MachineDataProviderComponent.ConfigBehavior.values()) {
                Button button = new Button().withTint(getDataProviderComponent().getConfig(MachineDataProviderComponent.ConfigType.ITEM, behavior, dir) ? 0xFFFFFF00 : 0xFFFF0000);
                button.setSize(8, 8);
                button.resizeability = false;
                map.put(behavior, button);
            }
            itemConfig.put(dir, map);
        }
        fluid = new WLabel(new TranslatableText("block.ehtech.machine_config.fluid"));
        for (Direction dir : Direction.values()) {
            Map<MachineDataProviderComponent.ConfigBehavior, Button> map = new HashMap<MachineDataProviderComponent.ConfigBehavior, Button>();
            for (MachineDataProviderComponent.ConfigBehavior behavior : MachineDataProviderComponent.ConfigBehavior.values()) {
                Button button = new Button().withTint(getDataProviderComponent().getConfig(MachineDataProviderComponent.ConfigType.FLUID, behavior, dir) ? 0xFFFFFF00 : 0xFFFF0000);
                button.setSize(8, 8);
                button.resizeability = false;
                map.put(behavior, button);
            }
            fluidConfig.put(dir, map);
        }
        power = new WLabel(new TranslatableText("block.ehtech.machine_config.power"));
        for (Direction dir : Direction.values()) {
            Map<MachineDataProviderComponent.ConfigBehavior, Button> map = new HashMap<MachineDataProviderComponent.ConfigBehavior, Button>();
            for (MachineDataProviderComponent.ConfigBehavior behavior : MachineDataProviderComponent.ConfigBehavior.values()) {
                Button button = new Button().withTint(getDataProviderComponent().getConfig(MachineDataProviderComponent.ConfigType.POWER, behavior, dir) ? 0xFFFFFF00 : 0xFFFF0000);
                button.setSize(8, 8);
                button.resizeability = false;
                map.put(behavior, button);
            }
            powerConfig.put(dir, map);
        }
    }

    @Override
    protected void drawDefault() {
        ((WGridPanel) root).add(containerTitle, 0, 0, 1, 2);
        ((WGridPanel) root).add(playerInventoryTitle, 0, 12, 1, 2);
        ((WGridPanel) root).add(createPlayerInventoryPanel(), 0, 14);
        ((WGridPanel) root).add(down, 4, 2, 2, 2);
        ((WGridPanel) root).add(up, 6, 2, 2, 2);
        ((WGridPanel) root).add(north, 8, 2, 2, 2);
        ((WGridPanel) root).add(south, 10, 2, 2, 2);
        ((WGridPanel) root).add(west, 12, 2, 2, 2);
        ((WGridPanel) root).add(east, 14, 2, 2, 2);
        ((WGridPanel) root).add(item, 0, 4, 4, 2);
        itemConfig.forEach((dir, map) -> {
            map.forEach((behavior, button) -> {
                ((WGridPanel) root).add(button, dir.ordinal() * 2 + 4 + (int) Math.floor((double) behavior.ordinal() / 2.0), 4 + (behavior.ordinal() + 1) % 2);
            });
        });
        ((WGridPanel) root).add(fluid, 0, 6, 4, 2);
        fluidConfig.forEach((dir, map) -> {
            map.forEach((behavior, button) -> {
                ((WGridPanel) root).add(button, dir.ordinal() * 2 + 4 + (int) Math.floor((double) behavior.ordinal() / 2.0), 6 + (behavior.ordinal() + 1) % 2);
            });
        });
        ((WGridPanel) root).add(power, 0, 8, 4, 2);
        powerConfig.forEach((dir, map) -> {
            map.forEach((behavior, button) -> {
                ((WGridPanel) root).add(button, dir.ordinal() * 2 + 4 + (int) Math.floor((double) behavior.ordinal() / 2.0), 8 + (behavior.ordinal() + 1) % 2);
            });
        });
    }

    protected MachineCapacitorComponent getCapacitorComponent() {
        return (MachineCapacitorComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null);
    }

    protected MachineDataProviderComponent getDataProviderComponent() {
        return (MachineDataProviderComponent) super.getDataProviderComponent();
    }
}