/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.util;

import net.minecraft.util.Identifier;

public class Helper {
    public final String MOD_ID;

    public Helper(String MOD_ID) {
        this.MOD_ID = MOD_ID;
    }

    public Identifier makeId(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static int[] range(int size) {
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++)
            array[i] = i;
        return array;
    }

    public static int min(int min, int... values) {
        for (int i = 0; i < values.length; i++)
            if (values[i] < min)
                min = values[i];
        return min;
    }

    public static int max(int max, int... values) {
        for (int i = 0; i < values.length; i++)
            if (values[i] > max)
                max = values[i];
        return max;
    }
}
