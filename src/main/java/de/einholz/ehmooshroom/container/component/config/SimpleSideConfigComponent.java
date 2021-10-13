package de.einholz.ehmooshroom.container.component.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SimpleSideConfigComponent implements SideConfigComponent {
    protected final Map<Identifier, char[][]> configs = new HashMap<>();

    @Override
    public <P> SimpleSideConfigComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public char[][] getConfig(Identifier id) {
        return configs.get(id);
    }

    @Override
    public void setConfig(Identifier id, char[][] config) {
        configs.put(id, config);
    }

    @Override
    public void addSideConfig(Identifier id) {
        configs.put(id, SideConfigComponent.getDefault());
    }

    @Override
    public void removeSideConfig(Identifier id) {
        configs.remove(id);
    }

    //XXX: can this be done better?
    @Override
    public void change(Identifier id, Direction dir, SideConfigBehavior behavior) {
        char c = getState(id, dir, behavior);
        boolean active = Character.isUpperCase(c);
        if (Character.toLowerCase(c) == 'f') c = 't';
        else c = 'f';
        if (active) Character.toUpperCase(c);
        setState(id, dir, behavior, c);
    }

    @Override
    public Set<Identifier> getIds() {
        return configs.keySet();
    }
}
