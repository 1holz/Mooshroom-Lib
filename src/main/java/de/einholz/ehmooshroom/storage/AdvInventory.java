package de.einholz.ehmooshroom.storage;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeMatcher;

public class AdvInventory extends SimpleInventory {
    public AdvInventory(int size) {
        super(size);
    }

    @Deprecated
    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        super.provideRecipeInputs(finder);
    }

    /*
    public static class Wrapper extends InventoryStorageImpl {

        Wrapper(Inventory inventory) {
            super(inventory);
            // todo Auto-generated constructor stub
        }

    }
    */
}
