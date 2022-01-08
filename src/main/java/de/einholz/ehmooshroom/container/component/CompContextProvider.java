package de.einholz.ehmooshroom.container.component;

import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import net.minecraft.util.Identifier;

public interface CompContextProvider {
    public Object[] getCompContext(Identifier id);
    public SideConfigComponent getSideConfig();
}
