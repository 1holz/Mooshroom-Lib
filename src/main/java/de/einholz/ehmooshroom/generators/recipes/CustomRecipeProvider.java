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

package de.einholz.ehmooshroom.generators.recipes;

import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.InventoryChangedCriterion.Conditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;

public abstract class CustomRecipeProvider extends FabricRecipeProvider {
    public CustomRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    protected void addNormalShapeless(Consumer<RecipeJsonProvider> exporter, ItemConvertible output,
            ItemConvertible... input) {
        ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder
                .create(output);
        // .group("wrench");
        for (ItemConvertible item : input)
            builder = builder.input(item);
        itemPossessionCriterion(builder, ArrayUtils.addAll(input, output)).offerTo(exporter);
    }

    protected void addCompression(Consumer<RecipeJsonProvider> exporter, ItemConvertible output,
            ItemConvertible input) {
        itemPossessionCriterion(ShapelessRecipeJsonBuilder
                .create(output)
                // .group("wrench")
                .input(input, 9), input, output)
                .offerTo(exporter);
    }

    protected ShapelessRecipeJsonBuilder itemPossessionCriterion(ShapelessRecipeJsonBuilder builder,
            ItemConvertible... items) {
        for (ItemConvertible item : items) {
            String name = FabricRecipeProvider.hasItem(item);
            Conditions condition = FabricRecipeProvider.conditionsFromItem(item);
            builder = builder.criterion(name, condition);
        }
        return builder;
    }
}
