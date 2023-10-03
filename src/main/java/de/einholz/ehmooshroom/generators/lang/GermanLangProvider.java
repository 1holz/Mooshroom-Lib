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

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GermanLangProvider extends CustomLangProvider {
    public GermanLangProvider(FabricDataGenerator generator, String code) {
        super(generator, code);
    }

    public void generateTranslations(TranslationBuilder builder) {
        add(builder, "block", "cancel_button", "X");
        add(builder, "block", "side_config.acc", " |G|P|↓   |↑  |N   |S  |W   |O  |");
        add(builder, "block", "side_config.item", "Gegenstände");
        add(builder, "block", "side_config.fluid", "Flüssigkeiten");
        add(builder, "block", "side_config.power", "Strom");
        add(builder, "chat", "wip", "An diesem Feature wird noch gearbeitet");
        add(builder, "tooltip", "cancel_button", "Zurück zum Hauptfenster");
        add(builder, "tooltip", "config_button", "%s mit Zugriff von %s ist %s (%s)");
        // try {
        // builder.add(getPath());
        // } catch (NoSuchElementException | IOException e) {
        // MooshroomLib.LOGGER.errorBug("Failed to add existing language file for " +
        // code, e);
        // }
    }
}
