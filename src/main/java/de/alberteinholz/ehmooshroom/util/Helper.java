package de.alberteinholz.ehmooshroom.util;

import java.util.stream.IntStream;

import net.minecraft.util.Identifier;

public class Helper {
    public final String MOD_ID;

    public Helper(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    public static int[] countingArray(int size) {
        return IntStream.range(0, size).toArray();
    }

    //TODO: expand this
    public Identifier makeId(String name) {
        return new Identifier(MOD_ID, name);
    }
}