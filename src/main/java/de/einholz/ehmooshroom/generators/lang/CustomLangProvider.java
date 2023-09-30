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

package de.einholz.ehmooshroom.generators.lang;

import java.nio.file.Path;
import java.util.NoSuchElementException;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public abstract class CustomLangProvider extends FabricLanguageProvider {
    protected final String code;

    public CustomLangProvider(FabricDataGenerator generator, String code) {
        super(generator, code);
        this.code = code;
    }

    protected Path getPath() throws NoSuchElementException {
        String path = "assets/" + dataGenerator.getModId() + "/lang/" + code + ".json";
        try {
            return dataGenerator.getModContainer().findPath(path).orElseThrow();
        } catch (NoSuchElementException e) {
            MooshroomLib.LOGGER.errorBug(path + " could not be found", e);
            throw e;
        }
    }

    protected void add(TranslationBuilder builder, String type, String path, String translation) {
        builder.add(type + "." + dataGenerator.getModId() + "." + path, translation);
    }
}
