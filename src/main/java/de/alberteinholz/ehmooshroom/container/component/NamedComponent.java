package de.alberteinholz.ehmooshroom.container.component;

import net.minecraft.util.Identifier;

public interface NamedComponent {
    //should the id be changeable?
    void setId(Identifier id);

    Identifier getId();
}
