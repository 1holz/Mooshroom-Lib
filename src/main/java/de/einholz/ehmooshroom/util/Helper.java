package de.einholz.ehmooshroom.util;

import java.util.stream.IntStream;

import net.minecraft.util.Identifier;

public class Helper {
    public final String MOD_ID;

    public Helper(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    public static int[] range(int size) {
        return IntStream.range(0, size).toArray();
    }

    public static int min(int min, int... values) {
        for (int i = 0; i < values.length; i++) if (values[i] < min) min = values[i];
        return min;
    }

    public static int max(int max, int... values) {
        for (int i = 0; i < values.length; i++) if (values[i] > max) max = values[i];
        return max;
    }

    public Identifier makeId(String name) {
        return new Identifier(MOD_ID, name);
    }
}
