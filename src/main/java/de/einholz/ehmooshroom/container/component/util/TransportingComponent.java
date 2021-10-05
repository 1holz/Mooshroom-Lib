package de.einholz.ehmooshroom.container.component.util;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent.SideConfigBehavior;
import net.minecraft.util.math.Direction;

//T may be null if not applicable
public interface TransportingComponent<C extends TransportingComponent<?, T>, T> extends CustomComponent {
    //return null if not applicable
    SideConfigComponent getSideConfig();
    Number getContent(T type);
    Number getSpace(T type);
    Number getMaxTransfer();
    Number change(Number amount, ActionType action, T type);

    //here Direction is always from the perspective of the block performing the action
    default Number pull(TransportingComponent<C, T> from, Direction dir, ActionType action, T type) {
        if (!getSideConfig().allows(getId(), dir, SideConfigBehavior.SELF_INPUT) || !from.getSideConfig().allows(from.getId(), dir, SideConfigBehavior.FOREIGN_INPUT)) return 0;
        return TransportingComponent.transport(from, this, action, type);
    }

    default Number push(TransportingComponent<C, T> to, Direction dir, ActionType action, T type) {
        if (!getSideConfig().allows(getId(), dir, SideConfigBehavior.SELF_OUTPUT) || !to.getSideConfig().allows(to.getId(), dir, SideConfigBehavior.FOREIGN_OUTPUT)) return 0;
        return TransportingComponent.transport(this, to, action, type);
    }

    default Number pull(TransportingComponent<C, T> from, ActionType action, T type) {
        return TransportingComponent.transport(from, this, action, type);
    }

    default Number push(TransportingComponent<C, T> to, ActionType action, T type) {
        return TransportingComponent.transport(this, to, action, type);
    }

    //assumes transportation is allowed
    static <C extends TransportingComponent<?, T>, T> Number transport(TransportingComponent<C, T> from, TransportingComponent<C, T> to, ActionType action, T type) {
        if (from.getMaxTransfer().doubleValue() <= 0.0 || to.getMaxTransfer().doubleValue() <= 0.0) return 0;
        double transfer = Math.min(Math.min(from.getMaxTransfer().doubleValue(), to.getMaxTransfer().doubleValue()), Math.min(from.getContent(type).doubleValue(), to.getSpace(type).doubleValue()));
        if (action.perfrom()) {
            double check = to.change(from.change(transfer, ActionType.PERFORM, type), ActionType.PERFORM, type).doubleValue();
            if (Math.abs(transfer - check) >= 0.01) MooshroomLib.LOGGER.smallBug(new Exception("Bigger transfer discrepancy of " + (transfer - check)));
            return check;
        }
        return transfer;
    }

    static enum ActionType {
        PERFORM,
        TEST;

        boolean perfrom() {
            return PERFORM.equals(this);
        }
    }
}
