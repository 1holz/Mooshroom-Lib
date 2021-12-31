package de.einholz.ehmooshroom.container.component.util;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent.SideConfigBehavior;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

//add type stuff? maybe for fluids? or should multi fluid container be default?
//T may be null if not applicable
public interface TransportingBarComponent<C extends TransportingBarComponent<C>> extends BarComponent, TransportingComponent<C> {
    float getContent();
    float getSpace();

    //here Direction is always from the perspective of the block performing the action
    @SuppressWarnings("unchecked")
    default Number pull(C from, Direction dir) {
        if (!getSideConfig().allows(getId(), dir, SideConfigBehavior.SELF_INPUT) || !from.getSideConfig().allows(from.getId(), dir, SideConfigBehavior.FOREIGN_INPUT)) return 0;
        return transport(from, (C) this);
    }

    @SuppressWarnings("unchecked")
    default Number push(C to, Direction dir) {
        if (!getSideConfig().allows(getId(), dir, SideConfigBehavior.SELF_OUTPUT) || !to.getSideConfig().allows(to.getId(), dir, SideConfigBehavior.FOREIGN_OUTPUT)) return 0;
        return transport((C) this, to);
    }

    @SuppressWarnings("unchecked")
    default Number pull(C from) {
        return transport(from, (C) this);
    }

    @SuppressWarnings("unchecked")
    default Number push(C to) {
        return transport((C) this, to);
    }

    default Number transport(C from, C to) {
        if (from.getMaxTransfer().floatValue() <= 0.0 || to.getMaxTransfer().floatValue() <= 0.0) return 0;
        float transfer = Math.min(Math.min(from.getMaxTransfer().floatValue(), to.getMaxTransfer().floatValue()), Math.min(from.getContent(), to.getSpace()));
        float check = to.increase(from.increase(transfer));
        if (Math.abs(transfer - check) >= 0.01) MooshroomLib.LOGGER.smallBug(new Exception("Bigger transfer discrepancy of " + (transfer - check)));
        return check;
    }

    @Override
    default void writeNbt(NbtCompound nbt) {
        BarComponent.super.writeNbt(nbt);
        TransportingComponent.super.writeNbt(nbt);
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        BarComponent.super.readNbt(nbt);
        TransportingComponent.super.readNbt(nbt);
    }
}
