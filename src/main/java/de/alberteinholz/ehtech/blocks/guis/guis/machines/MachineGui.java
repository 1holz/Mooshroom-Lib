package de.alberteinholz.ehtech.blocks.guis.guis.machines;

import java.util.function.Supplier;

import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.blocks.components.container.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.guis.guis.ContainerGui;
import de.alberteinholz.ehtech.blocks.guis.widgets.Bar;
import de.alberteinholz.ehtech.blocks.guis.widgets.Button;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public abstract class MachineGui extends ContainerGui {
    protected Identifier powerBarBG;
    protected Identifier powerBarFG;
    protected Identifier progressBarBG;
    protected Identifier progressBarFG;
    protected WItemSlot powerInputSlot;
    protected WItemSlot upgradeSlot;
    protected Bar powerBar;
    protected Button activationButton;
    protected Bar progressBar;
    protected WItemSlot networkSlot;
    protected WItemSlot powerOutputSlot;
    protected Button configurationButton;

    public MachineGui(int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        this(null, syncId, playerInv, buf);
    }

    public MachineGui(ScreenHandlerType<SyncedGuiDescription> type, int syncId, PlayerInventory playerInv, PacketByteBuf buf) {
        super(type, syncId, playerInv, buf);
    }

    @Override
    protected void initWidgetsDependencies() {
        super.initWidgetsDependencies();
        powerBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar/background.png");
        powerBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar/foreground.png");
        progressBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar/background.png");
        progressBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar/foreground.png");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initWidgets() {
        super.initWidgets();
        powerInputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).getContainerInventoryComponent().getNumber("power_input"));
        upgradeSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).getContainerInventoryComponent().getNumber("upgrade"));
        powerBar = new Bar(powerBarBG, powerBarFG, getCapacitorComponent(), Direction.UP);
        powerBar.addDefaultTooltip("tooltip.ehtech.maschine.power_bar_amount");
        Supplier<?>[] powerBarTrendSuppliers = {
            () -> {
                return UnitManager.WU_PER_TICK.format(((MachineBlockEntity) world.getBlockEntity(pos)).powerBilanz);
            }
        };
        powerBar.advancedTooltips.put("tooltip.ehtech.machine.power_bar_trend", (Supplier<Object>[]) powerBarTrendSuppliers);
        activationButton = new ActivationButton();
        Supplier<?>[] activationButtonSuppliers = {
            () -> {
                return ((MachineDataProviderComponent) ((BlockComponentProvider) world.getBlockState(pos).getBlock()).getComponent(world, pos, UniversalComponents.DATA_PROVIDER_COMPONENT, null)).getActivationState().name().toLowerCase();
            }
        };
        activationButton.advancedTooltips.put("tooltip.ehtech.activation_button", (Supplier<Object>[]) activationButtonSuppliers);
        activationButton.setOnClick(getDefaultOnButtonClick(activationButton));
        buttonIds.add(activationButton);
        progressBar = new Bar(progressBarBG, progressBarFG, ((MachineDataProviderComponent) getDataProviderComponent()).progress, Direction.RIGHT);
        progressBar.addDefaultTooltip("tooltip.ehtech.maschine.progress_bar");
        networkSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).getContainerInventoryComponent().getNumber("network"));
        powerOutputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).getContainerInventoryComponent().getNumber("power_output"));
        configurationButton = (Button) new Button().setLabel(new LiteralText("CON"));
        configurationButton.tooltips.add("tooltip.ehtech.configuration_button");
        configurationButton.setOnClick(getDefaultOnButtonClick(configurationButton));
        buttonIds.add(configurationButton);
    }

    @Override
    public void drawDefault() {
        super.drawDefault();
        ((WGridPanel) root).add(powerInputSlot, 8, 1);
        ((WGridPanel) root).add(upgradeSlot, 9, 1);
        ((WGridPanel) root).add(powerBar, 8, 2, 1, 3);
        ((WGridPanel) root).add(activationButton, 9, 2);
        ((WGridPanel) root).add(networkSlot, 9, 3);
        ((WGridPanel) root).add(powerOutputSlot, 8, 5);
        ((WGridPanel) root).add(configurationButton, 9, 5);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == buttonIds.indexOf(activationButton)) {
            ((MachineDataProviderComponent) getDataProviderComponent()).nextActivationState();
            world.getBlockEntity(pos).markDirty();
            return true;
        } else if (id == buttonIds.indexOf(configurationButton)) {
            if (!world.isClient) {
                player.openHandledScreen(((MachineBlockEntity) world.getBlockEntity(pos)).getSideConfigScreenHandlerFactory());
            }
            return true;
        } else {
            return false;
        }
    }

    protected MachineCapacitorComponent getCapacitorComponent() {
        return (MachineCapacitorComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null);
    }

    protected class ActivationButton extends Button {
        @Override
        public Identifier setTexture(int mouseX, int mouseY) {
            withTexture(new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/activation_button/" + ((MachineDataProviderComponent) getDataProviderComponent()).getActivationState().toString().toLowerCase() + ".png"));
            return super.setTexture(mouseX, mouseY);
        }
    }
}