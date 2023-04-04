package de.einholz.ehmooshroom.gui.widget;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import net.minecraft.text.TranslatableText;

// TODO only for client?
public interface AdvancedTooltip {
    List<String> getTooltips();
    Map<String, Supplier<Object>[]> getAdvancedTooltips();

    //Use this in the widget class:
    /*
    @Override
	public void addTooltip(TooltipBuilder info) {
		AdvancedTooltip.super.addTooltip(info);
    }
    */
    
	default void addTooltip(TooltipBuilder info) {
		if (!getTooltips().isEmpty()) for (String tooltip : getTooltips()) info.add(new TranslatableText(tooltip));
		if (getAdvancedTooltips().isEmpty()) return;
        getAdvancedTooltips().forEach(new BiConsumer<String, Supplier<Object>[]>() {
            @Override
            public void accept(String label, Supplier<Object>[] values) {
                Object[] args = new Object[values.length];
                for (int i = 0; i < values.length; i++) args[i] = values[i].get();
                info.add(new TranslatableText(label, args));
            }
        });
	}
}
