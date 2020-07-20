package de.alberteinholz.ehtech.util;

import java.util.Arrays;

import net.minecraft.util.Identifier;

public class Helper {
    public static Integer[] countingArray(int size) {
        Integer[] array = new Integer[size];
        Arrays.setAll(array, i -> i);
        return array;
    }

    //TODO: expand this
    public static Identifier translationKey(String name) {
        return new Identifier(Ref.MOD_ID, name);
    }
}