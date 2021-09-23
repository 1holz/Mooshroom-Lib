package de.einholz.ehmooshroom.container.component.config;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface SideConfigComponent extends CustomComponent {
    public static final Identifier SIDE_CONFIG_ID = MooshroomLib.HELPER.makeId("heat");
    public static final ComponentKey<SideConfigComponent> ENERGY = ComponentRegistry.getOrCreate(SIDE_CONFIG_ID, SideConfigComponent.class);

    //IMPL
    SideConfigState getState(Identifier id, Direction dir, SideConfigBehavior behavior);

    //API
    void addSideConfig(Identifier id);
    void removeSideConfig(Identifier id);

    //intersection mode
    void setAvailability(Identifier id, Direction dir, SideConfigBehavior behavior);
    boolean isAvailable(Identifier id, Direction dir,  SideConfigBehavior behavior);

    default boolean allows(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return SideConfigState.AVAILABLE_TRUE.equals(getState(id, dir, behavior));
    }

    @Override
    default Identifier getId() {
        return SIDE_CONFIG_ID;
    }

    public static enum SideConfigBehavior {
        SELF_INPUT(SideConfigState.AVAILABLE_FALSE),
        SELF_OUTPUT(SideConfigState.AVAILABLE_FALSE),
        FOREIGN_INPUT(SideConfigState.AVAILABLE_TRUE),
        FOREIGN_OUTPUT(SideConfigState.AVAILABLE_TRUE);

        private final SideConfigState def;

        private SideConfigBehavior(SideConfigState def) {
            this.def = def;
        }

        public SideConfigState getForChar(char c) {
            for (SideConfigState state : SideConfigState.values()) if (c == state.getChar()) return state;
            return def;
        }

        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return def.getChar();
        }
    }

    public static enum SideConfigState {
        AVAILABLE_TRUE('T'),
        AVAILABLE_FALSE('F'),
        RESTRICTED_TRUE('t'),
        RESTRICTED_FALSE('f');

        private final char c;

        private SideConfigState(char c) {
            this.c = c;
        }

        public char getChar() {
            return c;
        }

        /*XXX
        public ConfigState getAvailability() {

        }

        public ConfigState setAvailability(boolean available) {
            char cTemp = available ? Character.toLowerCase(c) : Character.toUpperCase(c);
            for (ConfigState state : ConfigState.values()) if (cTemp == state.c) return state;
            MooshroomLib.LOGGER.smallBug(new EnumConstantNotPresentException(ConfigState.class, Character.toString(cTemp)));
            return this;
        }
        */
    }
}
