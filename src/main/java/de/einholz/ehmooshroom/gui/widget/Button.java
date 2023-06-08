package de.einholz.ehmooshroom.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import de.einholz.ehmooshroom.MooshroomLib;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Button extends WButton implements AdvTooltip {
    public Identifier texture;
    public WSprite overlay;
    public int tint = 0xFFFFFFFF;
    private Function<PlayerEntity, Boolean> exe;
	public List<String> tooltips = new ArrayList<String>();
	public Map<String, Supplier<Object>[]> advancedTooltips = new HashMap<String, Supplier<Object>[]>();

    public Button(Function<PlayerEntity, Boolean> exe) {
        super();
        this.exe = exe;
        setSize(18, 18);
    }

    public Button() {
        this((player) -> false);
    }

    public Button withTint(int tint) {
        this.tint = tint;
        return this;
    }

    public Button withWSprite(WSprite sprite) {
        overlay = sprite;
        overlay.setLocation(x, y);
        overlay.setSize(width, height);
        return this;
    }

    public Button withTexture(Identifier id) {
        return withWSprite(new WSprite(id));
    }

    @Override
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        texture = setTexture(mouseX, mouseY);
        draw(matrices, x, y, mouseX, mouseY);
    }

    public Identifier setTexture(int mouseX, int mouseY) {
        String state = !isEnabled() ? "disabled" : (mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height) ? "hovered" : "regular";
        return MooshroomLib.HELPER.makeId("textures/gui/widget/button/" + state + ".png");
    }

    public void draw(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        float max = 256;
        if (width > max || height > max) MooshroomLib.LOGGER.smallBug(new Exception("Maximum size for a widget is 256"));
        int widthFloor = (int) Math.floor((double) width / 2.0);
        int widthCeil = (int) Math.ceil((double) width / 2.0);
        int heightFloor = (int) Math.floor((double) height / 2.0);
        int heightCeil = (int) Math.ceil((double) height / 2.0);
        ScreenDrawing.texturedRect(matrices, x, y, widthFloor, heightFloor, texture, 0F, 0F, (float) widthFloor / max, (float) heightFloor / max, tint);
        ScreenDrawing.texturedRect(matrices, x + widthFloor, y, widthCeil, heightFloor, texture, 1F - (float) heightCeil / max, 0F, 1F, (float) widthFloor / max, tint);
        ScreenDrawing.texturedRect(matrices, x, y + heightFloor, widthFloor, heightCeil, texture, 0F, 1F - (float) widthCeil / max,  (float) heightFloor / max, 1F, tint);
        ScreenDrawing.texturedRect(matrices, x + widthFloor, y + heightFloor, widthCeil, heightCeil, texture, 1F - (float) heightCeil / max, 1F - (float) widthCeil / max, 1F, 1F, tint);
        if (overlay != null) overlay.paint(matrices, x, y, mouseX, mouseY);
        if (getLabel() != null) {
		    int color = isEnabled() ? 0xE0E0E0 : 0xA0A0A0;
		    ScreenDrawing.drawStringWithShadow(matrices, getLabel().getString(), alignment, x, y + ((20 - 8) / 2), width, color);
        }
    }

    public void setExe(Function<PlayerEntity, Boolean> exe) {
        this.exe = exe;
    }

    public boolean execute(PlayerEntity player) {
        return exe.apply(player);
    }
	
	@Override
	public List<String> getTooltips() {
		return tooltips;
	}

	@Override
	public Map<String, Supplier<Object>[]> getAdvancedTooltips() {
		return advancedTooltips;
	}

    @Override
	public void addTooltip(TooltipBuilder info) {
		AdvTooltip.super.addTooltip(info);
    }

    @Override
    public boolean canResize() {
        return true;
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (overlay != null) overlay.setLocation(x, y);
    }

    @Override
	public void setSize(int width, int height) {
		this.width = width;
        this.height = height;
        if (overlay != null) overlay.setSize(width, height);
    }
}
