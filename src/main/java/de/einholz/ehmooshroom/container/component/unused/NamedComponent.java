package de.einholz.ehmooshroom.container.component.unused;

import net.minecraft.util.Identifier;

//TODO: DELETE!!!
@Deprecated
public interface NamedComponent {
    //should the id be changeable?
    void setId(Identifier id);

    Identifier getId();
}
