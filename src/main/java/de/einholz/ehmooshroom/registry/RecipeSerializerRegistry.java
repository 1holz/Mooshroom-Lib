package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.recipe.AdvRecipeSerializer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public class RecipeSerializerRegistry<T extends Recipe<?>> extends RegistryBuilder<RecipeSerializer<?>> {
    @SuppressWarnings("unchecked")
    public RecipeSerializerRegistry<T> register(String name) {
        return (RecipeSerializerRegistry<T>) register(name, (RecipeSerializer<T>) new AdvRecipeSerializer());
    }

    protected RecipeSerializerRegistry() {
    }

    @Override
    protected Registry<RecipeSerializer<?>> getRegistry() {
        return Registry.RECIPE_SERIALIZER;
    }
}
