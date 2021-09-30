package de.einholz.ehmooshroom.container.component.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
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
    public void writeNbt(NbtCompound tag) {
        for (Entry<Identifier,char[][]> entry : configs.entrySet()) if (!DEFAULT_CHARS.equals(entry.getValue())) tag.putString(entry.getKey().toString(), SideConfigComponent.configArrayToString(entry.getValue()));
    }

    @Override
    public void readNbt(NbtCompound tag) {
        for (Identifier id : configs.keySet()) {
            if (!tag.contains(id.toString(), NbtType.STRING)) continue;
            configs.put(id, SideConfigComponent.configStringToArray(tag.getString(id.toString())));
        }
    }

    @Override
    public char getState(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return configs.get(id)[dir.ordinal()][behavior.ordinal()];
    }

    @Override
    public void setState(Identifier id, Direction dir, SideConfigBehavior behavior, char state) {
        configs.get(id)[dir.ordinal()][behavior.ordinal()] = state;
    }

    @Override
    public void addSideConfig(Identifier id) {
        configs.put(id, SideConfigComponent.getDefault());
    }

    @Override
    public void removeSideConfig(Identifier id) {
        configs.remove(id);
    }

    @Override
    public void setAvailability(Identifier id, Direction dir, SideConfigBehavior behavior) {
        setState(id, dir, behavior, getState(id, dir, behavior));
    }

    @Override
    public boolean isAvailable(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return Character.isUpperCase(getState(id, dir, behavior));
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
