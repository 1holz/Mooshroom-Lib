package de.alberteinholz.ehtech.blocks.guis.widgets;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.text.TranslatableText;

public interface AdvancedTooltip {

    List<String> getTooltips();

    Map<String, Supplier<Object>[]> getAdvancedTooltips();

    //Use this:
    /*
    @Override
	public void addInformation(List<String> info) {
		AdvancedTooltip.super.addInformation(info);
    }
    */

	default void addInformation(List<String> information) {
		if (!getTooltips().isEmpty()) {
			for (String tooltip : getTooltips()) {
				information.add(new TranslatableText(tooltip).asFormattedString());
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
					information.add(new TranslatableText(label, args).asFormattedString());
				}
			});
		}
	}
}