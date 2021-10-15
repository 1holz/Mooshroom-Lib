package de.einholz.ehmooshroom.container.types;

import de.einholz.ehmooshroom.container.AdvancedContainerBE;
import de.einholz.ehmooshroom.recipes.Ingrediets.FluidIngredient;

public interface ContainerWithFluids {
    static boolean containsFluids(AdvancedContainerBE<?> container, FluidIngredient... ingredients) {
        for (FluidIngredient ingredient : ingredients) {
            //TODO
            if (!ingredient.matches(null)) return false;
        }
        return true;
    }
}
