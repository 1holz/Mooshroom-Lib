package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.recipe.Ingredients.BlockIngredient;
import de.einholz.ehmooshroom.recipe.Ingredients.DataIngredient;
import de.einholz.ehmooshroom.recipe.Ingredients.EntityIngredient;
import de.einholz.ehmooshroom.recipe.Ingredients.FluidIngredient;
import de.einholz.ehmooshroom.recipe.Ingredients.ItemIngredient;

@Deprecated
public interface RecipeHolder {
    public boolean containsItemIngredients(ItemIngredient... ingredients);
    public boolean containsFluidIngredients(FluidIngredient... ingredients);
    public boolean containsBlockIngredients(BlockIngredient... ingredients);
    public boolean containsEntityIngredients(EntityIngredient... ingredients);
    public boolean containsDataIngredients(DataIngredient... ingredients);
}
