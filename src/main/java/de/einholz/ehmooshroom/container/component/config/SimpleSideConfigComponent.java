package de.einholz.ehmooshroom.container.component.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class SimpleSideConfigComponent implements SideConfigComponent {
    protected final Map<Identifier, char[]> configs = new HashMap<>();

    @Override
    public <P> SimpleSideConfigComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }
}
