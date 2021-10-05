/*
package de.einholz.ehmooshroom.container.component;

import de.einholz.ehmooshroom.container.component.data.ConfigDataComponent;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
import dev.onyxstudios.cca.api.v3.component.Component;
import io.github.cottonmc.component.api.ActionType;
import net.minecraft.util.math.Direction;

public interface TransportingComponent<C extends Component> extends CustomComponent {
    void setConfig(ConfigDataComponent config);
    //should make use of config for transporting

    //check getConfigComp().allowsConfig(id, ConfigBehavior.SELF_INPUT, dir) first
    Number pull(C from, Direction dir, ActionType action);

    //check getConfigComp().allowsConfig(id, ConfigBehavior.SELF_OUTPUT, dir) first
    Number push(C to, Direction dir, ActionType action);

    //should also have
    //canInsert(..., Direction dir)
    //canExtract(..., Direction dir)

    static enum TransferType {
        PUSH,
        PULL;
    }
}
*/