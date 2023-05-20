package de.einholz.ehmooshroom.gui.widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import de.einholz.ehmooshroom.MooshroomLib;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Bar extends WBar implements AdvTooltip {
    /*
    @Deprecated
	public SimpleDataElement element;
    @Deprecated
	public SimpleCapacitorComponent capacitor;
    */
    private final int color;
    private final long max;
    private final LongSupplier cur;
    private final long min;
	private final List<String> tooltips = new ArrayList<String>();
	private final Map<String, Supplier<Object>[]> advancedTooltips = new LinkedHashMap<String, Supplier<Object>[]>();

    public Bar(Identifier bg, Identifier fg, int color, long min, LongSupplier cur, long max, Direction dir) {
        super(bg, fg, (int) cur.getAsLong(), (int) (max <= min ? min + 1 : max), dir);
        if (max <= min) {
            MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("Max value of " + max + " of bar with textures " + fg + " and " + bg + " has to be larger than the min of " + min + ". Max will be set to " + (min + 1)));
            max = min + 1;
        }
        this.color = color;
        this.max = max;
        this.cur = cur;
        this.min = min;
	}

    /* TODO del
    @Deprecated
    public Bar(Identifier bg, Identifier fg, SimpleDataElement element, Direction dir) {
        super(bg, fg, 0, 0, dir);
		this.element = element;
	}
	
    @Deprecated
	public Bar(Identifier bg, Identifier fg, SimpleCapacitorComponent capacitor, Direction dir) {
		super(bg, fg, 0, 0, dir);
		this.capacitor = capacitor;
	}
    */

	@Deprecated
	@Override
	public WBar withTooltip(String label) {
		tooltips.add(label);
		return this;
	}
	
	@Deprecated
	@Override
	public WBar withTooltip(Text label) {
		return withTooltip(label.getString());
	}

	@SuppressWarnings("unchecked")
	public void addDefaultTooltip(String label) {
		Supplier<?>[] suppliers = {
			() -> getMin(),
			() -> getCur(),
			() -> getMax(),
		};
		advancedTooltips.put(label, (Supplier<Object>[]) suppliers);
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (bg!=null) ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), bg, 0xFFFFFFFF);
		else ScreenDrawing.coloredRect(matrices, x, y, getWidth(), getHeight(), ScreenDrawing.colorAtOpacity(0x000000, 0.25f));
        float percent = (cur.getAsLong() - min) / (max - min);
		int barMax;
		if (Direction.RIGHT.equals(direction) || Direction.LEFT.equals(direction)) barMax = getWidth();
        else barMax = getHeight();
        int barSize = (int) (barMax * percent);
		if (Direction.UP.equals(direction)) {
			int left = x;
			int top = y + getHeight() - barSize;
			if (bar != null) ScreenDrawing.texturedRect(matrices, left, top, getWidth(), barSize, bar.image(), 0F, (float) (1 - percent), 1F, 1F, 0xFFFFFFFF);
			else ScreenDrawing.coloredRect(matrices, left, top, getWidth(), barSize,  ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
		} else if (Direction.RIGHT.equals(direction)) {
			if (bar != null) ScreenDrawing.texturedRect(matrices, x, y, barSize, getHeight(), bar.image(), 0F, 0F, (float) percent, 1F, 0xFFFFFFFF);
			else ScreenDrawing.coloredRect(matrices, x, y, barSize, getHeight(), ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
		} else if (Direction.DOWN.equals(direction)) {
			if (bar != null) ScreenDrawing.texturedRect(matrices, x, y, getWidth(), barSize, bar.image(), 0F, 0F, 1F, (float) percent, 0xFFFFFFFF);
			else ScreenDrawing.coloredRect(matrices, x, y, getWidth(), barSize, ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
		} else if (Direction.LEFT.equals(direction)) {
			int left = x + getWidth() - barSize;
			int top = y;
			if (bar != null) ScreenDrawing.texturedRect(matrices, left, top, barSize, getHeight(), bar.image(), (float) (1 - percent), 0F, 1F, 1F, 0xFFFFFFFF);
			else ScreenDrawing.coloredRect(matrices, left, top, barSize, getHeight(), ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
		}
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

    @Deprecated
	protected String getMin() {
		return String.valueOf(min);
        //return element != null ? element.getBarUnit().format(element.getBarMinimum()) : UnitManager.WORK_UNITS.format(0.0);
	}

    @Deprecated
	protected String getCur() {
		return String.valueOf(cur.getAsLong());
        //return element != null ? element.getBarUnit().format(element.getBarCurrent()) : UnitManager.WORK_UNITS.format(capacitor.getCurrentEnergy());
	}

    @Deprecated
	protected String getMax() {
		return String.valueOf(max);
        //return element != null ? element.getBarUnit().format(element.getBarMaximum()) : UnitManager.WORK_UNITS.format(capacitor.getMaxEnergy());
	}

	protected int getColor() {
        return color;
        /*
		if (element != null) return element.getBarUnit().getBarColor();
		else if (capacitor != null) return UnitManager.WORK_UNITS.getBarColor();
		else return 0;
        */
	}
}
