package de.einholz.ehmooshroom.registry;

import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegistryBuilder<T> {
    private Identifier id;
    private T type;

    protected abstract Registry<T> getRegistry();

    public RegistryBuilder<T> register(String name, T type) {
        this.id = idFactory().apply(name);
        this.type = Registry.register(getRegistry(), getId(), type);
        return this;
    }

    protected RegistryBuilder() {
    }

    protected Function<String, Identifier> idFactory() {
        return MooshroomLib.HELPER::makeId;
    }

    protected Identifier getId() {
        return id;
    }

    public T get() {
        return type;
    }
}
