package de.einholz.ehmooshroom.container.types;

import de.einholz.ehmooshroom.container.AdvancedContainerBE;
import de.einholz.ehmooshroom.container.component.item.ItemComponent;
import de.einholz.ehmooshroom.recipes.Ingrediets.ItemIngredient;
import net.minecraft.item.ItemStack;

public interface ContainerWithItems {
    static boolean containsItems(AdvancedContainerBE<?> container, ItemIngredient... ingredients) {
        for (ItemIngredient ingredient : ingredients) {
            int amount = ingredient.amount;
            for (ItemStack stack : ItemComponent.ITEM_INPUT_LOOKUP.find(container.getWorld(), container.getPos(), container.getWorld().getBlockState(container.getPos()), container, null).getStacks()) {
                if (ingredient.matches(stack)) amount -= stack.getCount();
                if (amount <= 0) break;
            }
            if (amount > 0) return false;
        }
        return true;
    }
}
