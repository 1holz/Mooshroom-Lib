package de.einholz.ehmooshroom.recipe;

public interface RecipeHolder {
    default boolean containsIngredients(Ingredient<?>... ingredients) {
        return false;
    }
}
