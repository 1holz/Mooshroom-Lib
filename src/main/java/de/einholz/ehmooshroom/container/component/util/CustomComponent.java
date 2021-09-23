package de.einholz.ehmooshroom.container.component.util;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public interface CustomComponent extends Component {
    <P extends Object> CustomComponent of(P provider);
    Identifier getId();
    void writeNbt(NbtCompound tag);
    void readNbt(NbtCompound tag);

    //Don't remove just dont use -> for internal management only
    @Deprecated
    @Override
    default void writeToNbt(NbtCompound tag) {
        NbtCompound comp = new NbtCompound();
        writeNbt(comp);
        if (!comp.isEmpty()) tag.put(getId().toString(), comp);
    }

    @Deprecated
    @Override
    default void readFromNbt(NbtCompound tag) {
        NbtCompound comp = tag.getCompound(getId().toString());
        if (!comp.isEmpty()) readNbt(comp);
    }
}
