package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.recipe.Ingrediets.BlockIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.DataIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.EntityIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.FluidIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.ItemIngredient;

public interface RecipeHolder {
    public boolean containsItemIngredients(ItemIngredient... ingredients);
    public boolean containsFluidIngredients(FluidIngredient... ingredients);
    public boolean containsBlockIngredients(BlockIngredient... ingredients);
    public boolean containsEntityIngredients(EntityIngredient... ingredients);
    public boolean containsDataIngredients(DataIngredient... ingredients);
}
