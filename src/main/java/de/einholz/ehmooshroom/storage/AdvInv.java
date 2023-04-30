package de.einholz.ehmooshroom.storage;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeMatcher;

public class AdvInv extends SimpleInventory {
    public static final int SIZE = 0;

    public AdvInv(int size) {
        super(size);
    }

    @Deprecated
    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        super.provideRecipeInputs(finder);
    }

    /* TODO del?
    public static class Wrapper extends InventoryStorageImpl {

        Wrapper(Inventory inventory) {
            super(inventory);
            // todo Auto-generated constructor stub
        }

    }
    */
}
