package de.einholz.ehmooshroom.container.component.energy;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.BarComponent;
import de.einholz.ehmooshroom.container.component.util.TransportingBarComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface EnergyComponent extends TransportingBarComponent<EnergyComponent> {
    public static final Identifier ENERGY_ID = MooshroomLib.HELPER.makeId("energy");
    public static final ComponentKey<EnergyComponent> ENERGY = ComponentRegistry.getOrCreate(ENERGY_ID, EnergyComponent.class);
    //TODO: use cache!!!
    public static final BlockApiLookup<EnergyComponent, Direction> ENERGY_LOOKUP = BlockApiLookup.get(ENERGY_ID, EnergyComponent.class, Direction.class);
    
    @Override
    default Identifier getId() {
        return ENERGY_ID;
    }

    @Override
    default float getMin() {
        return BarComponent.ZERO;
    }

    @Override
    default float getContent() {
        return getCur();
    }

    @Override
    default float getSpace() {
        return getMax() - getCur();
    }

    /*
    @Override
    default Number change(Number amount, Action action) {
        if (action.perfrom()) return change(amount.floatValue());
        return amount.floatValue() < 0.0F ? Math.min(amount.floatValue(), getContent(type).floatValue()) : Math.min(amount.floatValue(), getSpace(type).floatValue());
    }
    */
}
