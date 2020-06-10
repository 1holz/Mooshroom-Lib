package de.alberteinholz.ehtech.blocks.guis.controllers.machinecontrollers;

import java.util.function.Supplier;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.InventoryWrapper;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineCapacitorComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.guis.controllers.ContainerCraftingController;
import de.alberteinholz.ehtech.blocks.guis.widgets.ActivationButton;
import de.alberteinholz.ehtech.blocks.guis.widgets.Bar;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WBar.Direction;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public abstract class MachineController extends ContainerCraftingController {
    protected int buttonNumber = 0;
    private int buttonAmount = 0;
    protected WItemSlot powerInputSlot;
    protected WItemSlot powerOutputSlot;
    protected WItemSlot upgradeSlot;
    protected WItemSlot networkSlot;
    protected Identifier powerBarBG;
    protected Identifier powerBarFG;
    protected Bar powerBar;
    protected ActivationButton activationButton;
    protected Identifier progressBarBG;
    protected Identifier progressBarFG;
    protected Bar progressBar;

    public MachineController(int syncId, PlayerInventory playerInv, BlockContext context) {
        this(null, syncId, playerInv, context);
    }

    public MachineController(RecipeType<?> type, int syncId, PlayerInventory playerInv, BlockContext context) {
        super(type, syncId, playerInv, context);
    }

    @Override
    protected void initWidgetDependencies() {
        super.initWidgetDependencies();
        powerBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar_bg.png");
        powerBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/power_bar_fg.png");
        progressBarBG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar_bg.png");
        progressBarFG = new Identifier(Ref.MOD_ID, "textures/gui/container/machine/elements/progress_bar_fg.png");
    }

    @Override
    protected void initWidgets() {
        super.initWidgets();
        powerInputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("power_input"));
        powerOutputSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("power_output"));
        upgradeSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("upgrade"));
        networkSlot = WItemSlot.of(blockInventory, ((InventoryWrapper) blockInventory).component.getNumber("network"));
        powerBar = new Bar(powerBarBG, powerBarFG, getCapacitorComponent(), Direction.UP);
        activationButton = new ActivationButton((MachineDataProviderComponent) getDataProviderComponent(), buttonNumber);
        buttonNumber++;
        progressBar = new Bar(progressBarBG, progressBarFG, ((MachineDataProviderComponent) getDataProviderComponent()).progress, Direction.RIGHT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void drawDefault() {
        super.drawDefault();
        ((WGridPanel) root).add(powerInputSlot, 8, 1);
        ((WGridPanel) root).add(powerOutputSlot, 8, 5);
        ((WGridPanel) root).add(upgradeSlot, 9, 1);
        ((WGridPanel) root).add(networkSlot, 9, 3);
        powerBar.tooltips.add("tooltip.ehtech.maschine.power_bar_amount");
        Supplier<?>[] powerBarTrendSuppliers = {
            () -> {
                return UnitManager.WU_PER_TICK.format(((MachineDataProviderComponent) getDataProviderComponent()).getPowerPerTick());
            }
        };
        powerBar.specialTooltips.put("tooltip.ehtech.machine.power_bar_trend", (Supplier<Object>[]) powerBarTrendSuppliers);
        ((WGridPanel) root).add(powerBar, 8, 2, 1, 3);
        activationButton.setOnClick(() -> {
            MinecraftClient minecraft = screen.getMinecraftClient();
            minecraft.interactionManager.clickButton(syncId, buttonNumber);
            onButtonClick(playerInventory.player, activationButton.id);
        });
        ((WGridPanel) root).add(activationButton, 9, 2);
        progressBar.tooltips.add("tooltip.ehtech.maschine.progress_bar");
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        ((MachineDataProviderComponent) getDataProviderComponent()).nextActivationState();
        world.getBlockEntity(pos).markDirty();
        return true;
    }

    public int getButtonAmount() {
        return buttonAmount + 1;
    }

    protected MachineCapacitorComponent getCapacitorComponent() {
        return (MachineCapacitorComponent) BlockComponentProvider.get(world.getBlockState(pos)).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null);
    }
}