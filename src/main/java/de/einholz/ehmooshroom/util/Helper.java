package de.einholz.ehmooshroom.util;

import java.util.stream.IntStream;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.util.Identifier;

public class Helper {
    public final String MOD_ID;

    public Helper(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    //TODO remove if unused
    public static int[] range(int size) {
        MooshroomLib.LOGGER.test("IS THIS USED???");
        return IntStream.range(0, size).toArray();
    }

    public static int min(int min, int... values) {
        for (int i = 0; i < values.length; i++) if (values[i] < min) min = values[i];
        return min;
    }

    //TODO: expand this
    public Identifier makeId(String name) {
        return new Identifier(MOD_ID, name);
    }
}