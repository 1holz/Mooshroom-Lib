package de.einholz.ehmooshroom.container.component;

import net.minecraft.util.Identifier;

public interface CompContextProvider {
    public Object[] getCompContext(Identifier id);
}
