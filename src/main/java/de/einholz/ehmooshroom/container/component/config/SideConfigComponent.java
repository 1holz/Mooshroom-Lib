package de.einholz.ehmooshroom.container.component.config;

import java.util.Set;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface SideConfigComponent extends CustomComponent {
    public static final Identifier SIDE_CONFIG_ID = MooshroomLib.HELPER.makeId("heat");
    public static final ComponentKey<SideConfigComponent> ENERGY = ComponentRegistry.getOrCreate(SIDE_CONFIG_ID, SideConfigComponent.class);
    public static char[][] DEFAULT_CHARS = getDefault();

    //IMPL
    char getState(Identifier id, Direction dir, SideConfigBehavior behavior);
    void setState(Identifier id, Direction dir, SideConfigBehavior behavior, char state);

    //dir first, behavior second
    static char[][] getDefault() {
        char[][] ret = new char[6][4];
        for (Direction dir : Direction.values()) for (SideConfigBehavior behavior : SideConfigBehavior.values()) ret[dir.ordinal()][behavior.ordinal()] = behavior.getDefaultChar();
        return ret;
    }

    static String configArrayToString(char[][] config) {
        String str = "";
        for (char[] dirConfig : config) for (char c : dirConfig) str = str + c;
        return str;
    }

    static char[][] configStringToArray(String config) {
        char[] configArray = config.toCharArray();
        char[][] array = new char[6][4];
        int i = 0;
        for (Direction dir : Direction.values()) for (SideConfigBehavior behavior : SideConfigBehavior.values()) {
            array[dir.ordinal()][behavior.ordinal()] = configArray[i];
            i++;
        }
        return array;
    }

    //API
    void addSideConfig(Identifier id);
    void removeSideConfig(Identifier id);

    //intersection mode
    void setAvailability(Identifier id, Direction dir, SideConfigBehavior behavior);
    boolean isAvailable(Identifier id, Direction dir,  SideConfigBehavior behavior);
    void change(Identifier id, Direction dir,  SideConfigBehavior behavior);

    default boolean allows(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return 'T' == getState(id, dir, behavior);
    }

    Set<Identifier> getIds();

    @Override
    default Identifier getId() {
        return SIDE_CONFIG_ID;
    }

    //AVAILABLE_TRUE -> T
    //AVAILABLE_FALSE -> F
    //RESTRICTED_TRUE -> t
    //RESTRICTED_FALSE -> f
    public static enum SideConfigBehavior {
        SELF_INPUT('F'),
        SELF_OUTPUT('F'),
        FOREIGN_INPUT('T'),
        FOREIGN_OUTPUT('T');

        private final char def;

        private SideConfigBehavior(char def) {
            this.def = def;
        }

        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return def;
        }
    }
}
