package de.einholz.ehmooshroom.registry;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class RecipeTypeRegistry<T extends Recipe<?>> extends RegistryBuilder<RecipeType<?>> {
    public static final RecipeType<?> DUMMY_RECIPE_TYPE = new RecipeTypeRegistry<>()
            .register("dummy_recipe_type")
            .get();

    @SuppressWarnings("unchecked")
    public RecipeTypeRegistry<T> register(String name) {
        return (RecipeTypeRegistry<T>) register(name, new GenericRecipeType<>());
    }

    protected RecipeTypeRegistry() {
    }

    @Override
    protected Registry<RecipeType<?>> getRegistry() {
        return Registry.RECIPE_TYPE;
    }

    public static final class GenericRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        public GenericRecipeType() {
        }
    }

    public static void registerAll() {
    }
}
