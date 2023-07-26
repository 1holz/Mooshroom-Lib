package de.einholz.ehmooshroom.storage;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.math.Direction;

public enum SideConfigType {
    IN_GUI('t', false, false, SideConfigAccessor.GUI),
    OUT_GUI('t', false, true, SideConfigAccessor.GUI),
    IN_PROC('t', false, false, SideConfigAccessor.PROCESS),
    OUT_PROC('t', false, true, SideConfigAccessor.PROCESS),
    SELF_IN_D('F', false, false, SideConfigAccessor.DOWN),
    SELF_IN_U('F', false, false, SideConfigAccessor.UP),
    SELF_IN_N('F', false, false, SideConfigAccessor.NORTH),
    SELF_IN_S('F', false, false, SideConfigAccessor.SOUTH),
    SELF_IN_W('F', false, false, SideConfigAccessor.WEST),
    SELF_IN_E('F', false, false, SideConfigAccessor.EAST),
    SELF_OUT_D('F', false, true, SideConfigAccessor.DOWN),
    SELF_OUT_U('F', false, true, SideConfigAccessor.UP),
    SELF_OUT_N('F', false, true, SideConfigAccessor.NORTH),
    SELF_OUT_S('F', false, true, SideConfigAccessor.SOUTH),
    SELF_OUT_W('F', false, true, SideConfigAccessor.WEST),
    SELF_OUT_E('F', false, true, SideConfigAccessor.EAST),
    FOREIGN_IN_D('T', true, false, SideConfigAccessor.DOWN),
    FOREIGN_IN_U('T', true, false, SideConfigAccessor.UP),
    FOREIGN_IN_N('T', true, false, SideConfigAccessor.NORTH),
    FOREIGN_IN_S('T', true, false, SideConfigAccessor.SOUTH),
    FOREIGN_IN_W('T', true, false, SideConfigAccessor.WEST),
    FOREIGN_IN_E('T', true, false, SideConfigAccessor.EAST),
    FOREIGN_OUT_D('T', true, true, SideConfigAccessor.DOWN),
    FOREIGN_OUT_U('T', true, true, SideConfigAccessor.UP),
    FOREIGN_OUT_N('T', true, true, SideConfigAccessor.NORTH),
    FOREIGN_OUT_S('T', true, true, SideConfigAccessor.SOUTH),
    FOREIGN_OUT_W('T', true, true, SideConfigAccessor.WEST),
    FOREIGN_OUT_E('T', true, true, SideConfigAccessor.EAST);

    public static final char[] CHARS = new char[] {
            'T', // 00 AVAILABLE_TRUE
            'F', // 01 AVAILABLE_FALSE
            't', // 10 UNAVIALABLE_TRUE
            'f' // 11 UNAVIALABLE_FALSE
    };
    private final char DEF;
    public final boolean FOREIGN;
    public final boolean OUTPUT;
    public final SideConfigAccessor ACC;

    private SideConfigType(final char DEF, final boolean FOREIGN, final boolean OUTPUT, final SideConfigAccessor ACC) {
        this.DEF = DEF;
        this.FOREIGN = FOREIGN;
        this.OUTPUT = OUTPUT;
        this.ACC = ACC;
    }

    public static char[] getDefaultArray() {
        final SideConfigType[] values = SideConfigType.values();
        char[] array = new char[values.length];
        for (int i = 0; i < array.length; i++)
            array[i] = values[i].getDefaultChar();
        return array;
    }

    public char getDefaultChar() {
        return DEF;
    }

    public static SideConfigType getFromParams(boolean foreign, boolean output, Direction dir) {
        return getFromParams(foreign, output, SideConfigAccessor.getFromDir(dir));
    }

    public static SideConfigType getFromParams(boolean foreign, boolean output, SideConfigAccessor acc) {
        int dirLen = Direction.values().length;
        SideConfigType[] values = SideConfigType.values();
        if (SideConfigAccessor.GUI.equals(acc))
            return values[output ? 1 : 0];
        if (SideConfigAccessor.PROCESS.equals(acc))
            return values[output ? 3 : 2];
        return values[(foreign ? 2 * dirLen : 0) + (output ? dirLen : 0) + acc.ordinal() + 2];
    }

    public static SideConfigType[] valuesWithout(SideConfigType... withouts) {
        List<SideConfigType> values = Arrays.asList(values());
        for (SideConfigType without : withouts)
            values.remove(without);
        return values.toArray(new SideConfigType[0]);
    }

    public static enum SideConfigAccessor {
        GUI,
        PROCESS,
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST;

        public static SideConfigAccessor getFromDir(Direction dir) {
            return dir == null ? PROCESS : SideConfigAccessor.values()[dir.ordinal() + 2];
        }
    }
}
