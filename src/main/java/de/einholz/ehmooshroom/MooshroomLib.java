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

package de.einholz.ehmooshroom;

import de.einholz.ehmooshroom.registry.RecipeTypeRegistry;
import de.einholz.ehmooshroom.registry.ScreenHandlerRegistry;
import de.einholz.ehmooshroom.util.Helper;
import de.einholz.ehmooshroom.util.LoggerHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

public class MooshroomLib implements ModInitializer, ClientModInitializer {
    public static final Helper HELPER = new Helper("ehmooshroom");
    public static final LoggerHelper LOGGER = new LoggerHelper(HELPER.MOD_ID,
            "https://github.com/Albert-Einholz/Mooshroom-Lib/issues");

    @Override
    public void onInitialize() {
        RecipeTypeRegistry.registerAll();
        ScreenHandlerRegistry.registerAll();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient() {
    }
}
