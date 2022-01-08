package de.einholz.ehmooshroom.container.component.util;

import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent.SideConfigBehavior;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;

//here Number is used
public interface TransportingComponent<C extends TransportingComponent<C>> extends CustomComponent {
    /**
     * @return the SideConfigComponent or null if not applicable
     */
    SideConfigComponent getSideConfig();
    Number getMaxTransfer();
    void setMaxTransfer(Number maxTransfer);
    /**
     * assumes transportation is allowed
     * @param from
     * @param to
     * @return how much was actually transported
     */
    Number transport(C from, C to);

    default boolean allowsTransport(Direction dir, SideConfigBehavior behavior) {
        return getSideConfig() == null ? true : getSideConfig().allows(getId(), dir, behavior);
    }

    /**
     * @param from
     * @param dir always from the perspective of the block performing the action
     * @return
     */
    @SuppressWarnings("unchecked")
    default Number pull(C from, Direction dir) {
        return !allowsTransport(dir, SideConfigBehavior.SELF_INPUT) || !from.allowsTransport(dir.getOpposite(), SideConfigBehavior.FOREIGN_INPUT) ? 0 : transport(from, (C) this);
    }

    @SuppressWarnings("unchecked")
    default Number push(C to, Direction dir) {
        return !allowsTransport(dir, SideConfigBehavior.SELF_OUTPUT) || !to.allowsTransport(dir.getOpposite(), SideConfigBehavior.FOREIGN_OUTPUT) ? 0 : transport((C) this, to);
    }

    @SuppressWarnings("unchecked")
    default Number pull(C from) {
        return transport(from, (C) this);
    }

    @SuppressWarnings("unchecked")
    default Number push(C to) {
        return transport((C) this, to);
    }

    @Override
    default void writeNbt(NbtCompound tag) {
        tag.putDouble("Max_Transfer", getMaxTransfer().doubleValue());
    }

    @Override
    default void readNbt(NbtCompound tag) {
        setMaxTransfer(tag.getDouble("Max_Transfer"));
    }
}
