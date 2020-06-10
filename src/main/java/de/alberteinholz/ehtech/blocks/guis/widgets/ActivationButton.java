package de.alberteinholz.ehtech.blocks.guis.widgets;

import java.util.List;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components.MachineDataProviderComponent.ActivationState;
import de.alberteinholz.ehtech.util.Ref;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import net.minecraft.util.Identifier;

public class ActivationButton extends WButton {
    public int id;
    public MachineDataProviderComponent component;
    public ActivationState state;
    public Identifier texture;

    public ActivationButton(MachineDataProviderComponent component, int id) {
        super();
        this.id = id;
        this.component = component;
    }

    @Override
    public void paintBackground(int x, int y, int mouseX, int mouseY) {
        setTexture(mouseX, mouseY);
        draw(x, y);
    }

    public void setTexture(int mouseX, int mouseY) {
        state = component.getActivationState();
        if(mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height) {
            if (state == ActivationState.ALWAYS_ON) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_always_on_hovered.png");
            } else if(state == ActivationState.REDSTONE_ON) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_redstone_on_hovered.png");
            } else if(state == ActivationState.REDSTONE_OFF) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_redstone_off_hovered.png");
            } else {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_always_off_hovered.png");
            }
        } else {
            if (state == ActivationState.ALWAYS_ON) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_always_on_regular.png");
            } else if(state == ActivationState.REDSTONE_ON) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_redstone_on_regular.png");
            } else if(state == ActivationState.REDSTONE_OFF) {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_redstone_off_regular.png");
            } else {
                texture = new Identifier(Ref.MOD_ID, "textures/gui/widget/activation_button_always_off_regular.png");
            }
        }
    }

    public void draw(int x, int y) {
        ScreenDrawing.texturedRect(x, y, width, height, texture, 0, 0, 1, 1, 0xFFFFFFFF);
    }

    @Override
    public void addInformation(List<String> information) {
        super.addInformation(information);
        information.add(ActivationState.toString(component.getActivationState()));
    }

    @Override
	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
    }
}