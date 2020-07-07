package de.alberteinholz.ehtech.blocks.guis.widgets;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;

public interface AdvancedTooltip {
    List<String> getTooltips();
    Map<String, Supplier<Object>[]> getAdvancedTooltips();

    //Use this in the widget class:
    /*
    @Override
	public void addTooltip(List<String> info) {
		AdvancedTooltip.super.addTooltip(info);
    }
    */
	default void addTooltip(List<StringRenderable> info) {
		if (!getTooltips().isEmpty()) {
			for (String tooltip : getTooltips()) {
				info.add(new TranslatableText(tooltip));
			}
		}
		if (!getAdvancedTooltips().isEmpty()) {
			getAdvancedTooltips().forEach(new BiConsumer<String, Supplier<Object>[]>() {
				@Override
				public void accept(String label, Supplier<Object>[] values) {
					Object[] args = new Object[values.length];
					for (int i = 0; i < values.length; i++) {
						args[i] = values[i].get();
					}
					info.add(new TranslatableText(label, args));
				}
			});
		}
	}
}