package de.einholz.ehmooshroom.container.component.energy;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.BarComponent;
import de.einholz.ehmooshroom.container.component.util.TransportingComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;

public interface EnergyComponent extends BarComponent, TransportingComponent<EnergyComponent, Void> {
    public static final Identifier ENERGY_ID = MooshroomLib.HELPER.makeId("heat");
    public static final ComponentKey<EnergyComponent> ENERGY = ComponentRegistry.getOrCreate(ENERGY_ID, EnergyComponent.class);

    @Override
    default Identifier getId() {
        return ENERGY_ID;
    }

    @Override
    default float getMin() {
        return BarComponent.ZERO;
    }

    @Override
    default Number getContent(Void type) {
        return getCur();
    }

    @Override
    default Number getSpace(Void type) {
        return getMax() - getCur();
    }

    @Override
    default Number change(Number amount, Action action, Void type) {
        if (action.perfrom()) return change(amount.floatValue());
        return amount.floatValue() < 0.0F ? Math.min(amount.floatValue(), getContent(type).floatValue()) : Math.min(amount.floatValue(), getSpace(type).floatValue());
    }
}
