package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.mixin.InventoryStorageImplA;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeMatcher;

// TODO maybe redo? so you dont have to extend this all the time
public class AdvInv extends SimpleInventory {
    public static final int SIZE = 0;

    public AdvInv(int size) {
        super(size);
    }

    public static Inventory itemStorageToInv(StorageEntry<?, ?> entry) {
        if (entry.storage instanceof InventoryStorageImpl impl)
            return ((InventoryStorageImplA) impl).getInventory();
        MooshroomLib.LOGGER.bigBug(new IllegalStateException("ItemStorage must be of the type InventoryStorageImpl"));
        return new SimpleInventory(0);
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
