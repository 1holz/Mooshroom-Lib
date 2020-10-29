package de.alberteinholz.ehmooshroom.container.component;

import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent;

public interface TransportingComponent {
    public void setConfig(ConfigDataComponent config);
    //should make use of config for transporting
    //should also have
    //move(Component.class from, Component.class to, ..., Direction dir, ActionType action)
    //canInsert(..., Direction dir)
    //canExtract(..., Direction dir)
}
