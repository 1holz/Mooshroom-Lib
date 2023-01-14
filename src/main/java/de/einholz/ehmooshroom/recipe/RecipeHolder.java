package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.recipe.deprecated.BlockIngredient;
import de.einholz.ehmooshroom.recipe.deprecated.DataIngredient;
import de.einholz.ehmooshroom.recipe.deprecated.EntityIngredient;
import de.einholz.ehmooshroom.recipe.deprecated.FluidIngredient;
import de.einholz.ehmooshroom.recipe.deprecated.ItemIngredient;

@Deprecated
public interface RecipeHolder {
    public boolean containsIngredients(Ingredient<?>... ingredients);

    @Deprecated
    default boolean containsItemIngredients(ItemIngredient... ingredients) {
        return true;
    }

    @Deprecated
    default boolean containsFluidIngredients(FluidIngredient... ingredients) {
        return true;
    }

    @Deprecated
    default boolean containsBlockIngredients(BlockIngredient... ingredients) {
        return true;
    }

    @Deprecated
    default boolean containsEntityIngredients(EntityIngredient... ingredients) {
        return true;
    }

    @Deprecated
    default boolean containsDataIngredients(DataIngredient... ingredients) {
        return true;
    }
}
