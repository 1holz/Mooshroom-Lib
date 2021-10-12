package de.einholz.ehmooshroom.container.component.heat;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.BarComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;

public interface HeatComponent extends BarComponent {
    public static final Identifier HEAT_ID = MooshroomLib.HELPER.makeId("heat");
    public static final ComponentKey<HeatComponent> HEAT = ComponentRegistry.getOrCreate(HEAT_ID, HeatComponent.class);
    //TODO: use cache!!!
    public static final BlockApiLookup<HeatComponent, Void> HEAT_LOOKUP = BlockApiLookup.get(HEAT_ID, HeatComponent.class, Void.class);
    public static final float NORMAL = 273.15F;

    @Override
    default Identifier getId() {
        return HEAT_ID;
    }

    @Override
    default float getOff() {
        return NORMAL;
    }
}
