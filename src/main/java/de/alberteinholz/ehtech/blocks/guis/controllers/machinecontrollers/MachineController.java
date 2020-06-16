package de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers;

import java.util.function.Supplier;

import de.alberteinholz.ehtech.blocks.components.container.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.guis.controllers.ContainerCraftingController;
import de.alberteinholz.ehtech.blocks.guis.widgets.Bar;
import de.alberteinholz.ehtech.blocks.guis.widgets.Button;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public abstract class MachineController extends ContainerCraftingController {
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


    public MachineController(int syncId, PlayerInventory playerInv, BlockContext context) {
        this(null, syncId, playerInv, context);
    }

    public MachineController(RecipeType<?> type, int syncId, PlayerInventory playerInv, BlockContext context) {
        super(type, syncId, playerInv, context);
    }

    @Override
    protected void initWidgetsDependencies() {
        super.initWidgetsDependencies();
        powerBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar/background.png");
        powerBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar/foreground.png");
        progressBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar/background.png");
        progressBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar/foreground.png");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        powerInputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("power_input"));
        upgradeSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("upgrade"));
        powerBar = new Bar(powerBarBG, powerBarFG, getCapacitorComponent(), Direction.UP);
        activationButton = new ActivationButton();
        buttonIds.add(activationButton);
        progressBar = new Bar(progressBarBG, progressBarFG, ((MachineDataProviderComponent) getDataProviderComponent()).progress, Direction.RIGHT);
        networkSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("network"));
        powerOutputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("power_output"));
        configurationButton = (Button) new Button().setLabel(new LiteralText("CON"));
        buttonIds.add(configurationButton);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawDefault() {
        super.drawDefault();
        ((WGridPanel) root).add(powerInputSlot, 8, 1);
        ((WGridPanel) root).add(upgradeSlot, 9, 1);
        powerBar.tooltips.add("tooltip.ehtech.maschine.power_bar_amount");
        Supplier<?>[] powerBarTrendSuppliers = {
            () -> {
                return UnitManager.WU_PER_TICK.format(((MachineDataProviderComponent) getDataProviderComponent()).getPowerPerTick());
            }
        };
        powerBar.specialTooltips.put("tooltip.ehtech.machine.power_bar_trend", (Supplier<Object>[]) powerBarTrendSuppliers);
        ((WGridPanel) root).add(powerBar, 8, 2, 1, 3);
        ((WGridPanel) root).add(activationButton, 9, 2);
        activationButton.setOnClick(getDefaultOnButtonClick(activationButton));
        progressBar.tooltips.add("tooltip.ehtech.maschine.progress_bar");
        ((WGridPanel) root).add(networkSlot, 9, 3);
        ((WGridPanel) root).add(powerOutputSlot, 8, 5);
        ((WGridPanel) root).add(configurationButton, 9, 5);
        configurationButton.setOnClick(getDefaultOnButtonClick(configurationButton));
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == buttonIds.indexOf(activationButton)) {
            ((MachineDataProviderComponent) getDataProviderComponent()).nextActivationState();
            world.getBlockEntity(pos).markDirty();
            return true;
        } else if (id == buttonIds.indexOf(configurationButton)) {
            if (!world.isClient) {
                ContainerProviderRegistry.INSTANCE.openContainer(BlockRegistry.getId(BlockRegistry.MACHINE_CONFIG), player, buf -> buf.writeBlockPos(pos));
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