package de.einholz.ehmooshroom.recipes;

import de.einholz.ehmooshroom.recipes.Ingrediets.BlockIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.DataIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.EntityIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.FluidIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.ItemIngredient;

public interface RecipeHolder {

    public boolean containsItemIngredients(ItemIngredient... ingredients);

    public boolean containsFluidIngredients(FluidIngredient... ingredients);

    public boolean containsBlockIngredients(BlockIngredient... ingredients);

    public boolean containsEntityIngredients(EntityIngredient... ingredients);

    public boolean containsDataIngredients(DataIngredient... ingredients);
    
}
