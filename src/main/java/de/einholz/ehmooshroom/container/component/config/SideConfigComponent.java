package de.einholz.ehmooshroom.container.component.config;

import java.util.Set;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface SideConfigComponent extends CustomComponent {
    public static final Identifier SIDE_CONFIG_ID = MooshroomLib.HELPER.makeId("side_config");
    public static final ComponentKey<SideConfigComponent> SIDE_CONFIG = ComponentRegistry.getOrCreate(SIDE_CONFIG_ID, SideConfigComponent.class);
    //TODO: use cache!!!
    public static final BlockApiLookup<SideConfigComponent, Void> SIDE_CONFIG_LOOKUP = BlockApiLookup.get(SIDE_CONFIG_ID, SideConfigComponent.class, Void.class);
    public static final char[][] DEFAULT_CHARS = getDefault();

    //IMPL
    char[][] getConfig(Identifier id);
    void setConfig(Identifier id, char[][] config);

    default char getState(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return getConfig(id)[dir.ordinal()][behavior.ordinal()];
    }

    default void setState(Identifier id, Direction dir, SideConfigBehavior behavior, char state) {
        char[][] config = getConfig(id);
        config[dir.ordinal()][behavior.ordinal()] = state;
        setConfig(id, config);
    }

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

    //TODO intersection mode
    default void setAvailability(Identifier id, Direction dir, SideConfigBehavior behavior) {
        setState(id, dir, behavior, getState(id, dir, behavior));
    }

    default boolean isAvailable(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return Character.isUpperCase(getState(id, dir, behavior));
    }

    void change(Identifier id, Direction dir,  SideConfigBehavior behavior);

    default boolean allows(Identifier id, Direction dir, SideConfigBehavior behavior) {
        return 'T' == getState(id, dir, behavior);
    }

    Set<Identifier> getIds();

    @Override
    default void writeNbt(NbtCompound nbt) {
        for (Identifier id : getIds()) if (!DEFAULT_CHARS.equals(getConfig(id))) nbt.putString(id.toString(), SideConfigComponent.configArrayToString(getConfig(id)));
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        for (Identifier id : getIds()) {
            if (!nbt.contains(id.toString(), NbtType.STRING)) continue;
            setConfig(id, SideConfigComponent.configStringToArray(nbt.getString(id.toString())));
        }
    }

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

    public static enum SideConfigType {
        SELF_INPUT_DOWN(SideConfigBehavior.SELF_INPUT, Direction.DOWN),
        SELF_INPUT_UP(SideConfigBehavior.SELF_INPUT, Direction.UP),
        SELF_INPUT_NORTH(SideConfigBehavior.SELF_INPUT, Direction.NORTH),
        SELF_INPUT_SOUTH(SideConfigBehavior.SELF_INPUT, Direction.SOUTH),
        SELF_INPUT_WEST(SideConfigBehavior.SELF_INPUT, Direction.WEST),
        SELF_INPUT_EAST(SideConfigBehavior.SELF_INPUT, Direction.EAST),
        SELF_OUTPUT_DOWN(SideConfigBehavior.SELF_OUTPUT, Direction.DOWN),
        SELF_OUTPUT_UP(SideConfigBehavior.SELF_OUTPUT, Direction.UP),
        SELF_OUTPUT_NORTH(SideConfigBehavior.SELF_OUTPUT, Direction.NORTH),
        SELF_OUTPUT_SOUTH(SideConfigBehavior.SELF_OUTPUT, Direction.SOUTH),
        SELF_OUTPUT_WEST(SideConfigBehavior.SELF_OUTPUT, Direction.WEST),
        SELF_OUTPUT_EAST(SideConfigBehavior.SELF_OUTPUT, Direction.EAST),
        FOREIGN_INPUT_DOWN(SideConfigBehavior.FOREIGN_INPUT, Direction.DOWN),
        FOREIGN_INPUT_UP(SideConfigBehavior.FOREIGN_INPUT, Direction.UP),
        FOREIGN_INPUT_NORTH(SideConfigBehavior.FOREIGN_INPUT, Direction.NORTH),
        FOREIGN_INPUT_SOUTH(SideConfigBehavior.FOREIGN_INPUT, Direction.SOUTH),
        FOREIGN_INPUT_WEST(SideConfigBehavior.FOREIGN_INPUT, Direction.WEST),
        FOREIGN_INPUT_EAST(SideConfigBehavior.FOREIGN_INPUT, Direction.EAST),
        FOREIGN_OUTPUT_DOWN(SideConfigBehavior.FOREIGN_OUTPUT, Direction.DOWN),
        FOREIGN_OUTPUT_UP(SideConfigBehavior.FOREIGN_OUTPUT, Direction.UP),
        FOREIGN_OUTPUT_NORTH(SideConfigBehavior.FOREIGN_OUTPUT, Direction.NORTH),
        FOREIGN_OUTPUT_SOUTH(SideConfigBehavior.FOREIGN_OUTPUT, Direction.SOUTH),
        FOREIGN_OUTPUT_WEST(SideConfigBehavior.FOREIGN_OUTPUT, Direction.WEST),
        FOREIGN_OUTPUT_EAST(SideConfigBehavior.FOREIGN_OUTPUT, Direction.EAST);

        public final SideConfigBehavior behavior;
        public final Direction dir;

        private SideConfigType(SideConfigBehavior behavior, Direction dir) {
            this.behavior = behavior;
            this.dir = dir;
        }
    }
}
