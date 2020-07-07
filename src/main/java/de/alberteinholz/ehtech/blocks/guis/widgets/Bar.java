package de.alberteinholz.ehtech.blocks.guis.widgets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WBar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Bar extends WBar implements AdvancedTooltip {
	public SimpleDataElement element;
	public SimpleCapacitorComponent capacitor;
	public List<String> tooltips = new ArrayList<String>();
	public Map<String, Supplier<Object>[]> advancedTooltips = new LinkedHashMap<String, Supplier<Object>[]>();

    public Bar(Identifier bg, Identifier fg, SimpleDataElement element, Direction dir) {
        super(bg, fg, 0, 0, dir);
		this.element = element;
	}
	
	public Bar(Identifier bg, Identifier fg, SimpleCapacitorComponent capacitor, Direction dir) {
		super(bg, fg, 0, 0, dir);
		this.capacitor = capacitor;
	}

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
			() -> {
				return getMin();
			},
			() -> {
				return getCur();
			},
			() -> {
				return getMax();
			}
		};
		advancedTooltips.put(label, (Supplier<Object>[]) suppliers);
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (bg!=null) {
			ScreenDrawing.texturedRect(x, y, getWidth(), getHeight(), bg, 0xFFFFFFFF);
		} else {
			ScreenDrawing.coloredRect(x, y, getWidth(), getHeight(), ScreenDrawing.colorAtOpacity(0x000000, 0.25f));
		}
		double percent = element != null ? (element.getBarCurrent() - element.getBarMinimum()) / (element.getBarMaximum() - element.getBarMinimum()) : (double) capacitor.getCurrentEnergy() / (double) (capacitor.getMaxEnergy());
		int barMax;
		if (direction == Direction.RIGHT || direction == Direction.LEFT) {
            barMax = getWidth();
        } else {
            barMax = getHeight();
        }
        int barSize = (int) (barMax * percent);
		if (direction == Direction.UP) {
			int left = x;
			int top = y + getHeight() - barSize;
			if (bar!=null) {
				ScreenDrawing.texturedRect(left, top, getWidth(), barSize, bar, 0, (float) (1 - percent), 1, 1, 0xFFFFFFFF);
			} else {
				ScreenDrawing.coloredRect(left, top, getWidth(), barSize,  ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
			}
		} else if (direction == Direction.RIGHT) {
			if (bar!=null) {
				ScreenDrawing.texturedRect(x, y, barSize, getHeight(), bar, 0, 0, (float) percent, 1, 0xFFFFFFFF);
			} else {
				ScreenDrawing.coloredRect(x, y, barSize, getHeight(), ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
			}
		} else if (direction == Direction.DOWN) {
			if (bar!=null) {
				ScreenDrawing.texturedRect(x, y, getWidth(), barSize, bar, 0, 0, 1, (float) percent, 0xFFFFFFFF);
			} else {
				ScreenDrawing.coloredRect(x, y, getWidth(), barSize, ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
			}
		} else if (direction == Direction.LEFT) {
			int left = x + getWidth() - barSize;
			int top = y;
			if (bar!=null) {
				ScreenDrawing.texturedRect(left, top, barSize, getHeight(), bar, (float) (1 - percent), 0, 1, 1, 0xFFFFFFFF);
			} else {
				ScreenDrawing.coloredRect(left, top, barSize, getHeight(), ScreenDrawing.colorAtOpacity(getColor(), 0.5f));
			}
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
	public void addTooltip(List<StringRenderable> info) {
		AdvancedTooltip.super.addTooltip(info);
	}

	protected String getMin() {
		return element != null ? element.getBarUnit().format(element.getBarMinimum()) : UnitManager.WORK_UNITS.format(0.0);
	}

	protected String getCur() {
		return element != null ? element.getBarUnit().format(element.getBarCurrent()) : UnitManager.WORK_UNITS.format(capacitor.getCurrentEnergy());
	}

	protected String getMax() {
		return element != null ? element.getBarUnit().format(element.getBarMaximum()) : UnitManager.WORK_UNITS.format(capacitor.getMaxEnergy());
	}

	protected int getColor() {
		if (element != null) {
			return element.getBarUnit().getBarColor();
		} else if (capacitor != null) {
			return UnitManager.WORK_UNITS.getBarColor();
		} else {
			return 0;
		}
	}
}