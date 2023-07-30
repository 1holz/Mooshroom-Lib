package de.einholz.ehmooshroom.gui.widget;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import net.minecraft.text.TranslatableText;

// TODO only for client?
public interface DynTooltip {
    abstract List<String> getTooltips();

    abstract Map<String, Supplier<Object>[]> getDynTooltips();

    // Use this in the widget class:
    /*
     * @Override
     * public void addTooltip(TooltipBuilder info) {
     * AdvancedTooltip.super.addTooltip(info);
     * }
     */

    default void addTooltip(TooltipBuilder info) {
        if (!getTooltips().isEmpty())
            for (String tooltip : getTooltips())
                info.add(new TranslatableText(tooltip));
        if (getDynTooltips().isEmpty())
            return;
        getDynTooltips().forEach((label, values) -> {
            Object[] args = new Object[values.length];
            for (int i = 0; i < values.length; i++)
                args[i] = values[i].get();
            info.add(new TranslatableText(label, args));
        });
    }
}
