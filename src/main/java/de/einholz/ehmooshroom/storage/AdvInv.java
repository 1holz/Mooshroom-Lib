package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.Identifier;

public class AdvInv extends SimpleInventory {
    private final List<Identifier> idToInt = new ArrayList<>();
    private final Map<Integer, Function<ItemStack, Boolean>> accepters = new HashMap<>();
    private Function<ItemStack, Boolean> defaultAcceptor = (stack) -> true;

    public AdvInv(int size) {
        super(size);
    }

    public AdvInv(Identifier... ids) {
        this(ids.length);
        for (int i = 0; i < ids.length; i++) mapId(ids[i], i);
    }

    public AdvInv mapId(Identifier id, int i) {
        if (i >= size()) {
            MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("Index for " + id.toString() + "is larger than the size of " + size()));
            return this;
        }
        idToInt.add(i, id);
        return this;
    }

    public int getSlotIndex(Identifier id) {
        return idToInt.indexOf(id);
    }

    public AdvInv setDefaultAcceptor(Function<ItemStack, Boolean> defaultAcceptor) {
        this.defaultAcceptor = defaultAcceptor;
        return this;
    }

    public AdvInv setAccepter(Function<ItemStack, Boolean> accepter, Identifier... ids) {
        for (Identifier id : ids) accepters.put(getSlotIndex(id), accepter);
        return this;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return accepters.getOrDefault(slot, defaultAcceptor).apply(stack);
    }

    @Deprecated
    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        super.provideRecipeInputs(finder);
    }

    // TODO del if not needed
    @Deprecated
    public static Inventory itemStorageToInv(StorageEntry<?, ?> entry) {
        if (entry.storage instanceof AdvItemStorage advStorage)
            return advStorage.getInv();
        MooshroomLib.LOGGER.bigBug(new IllegalStateException("ItemStorage must be of the type InventoryStorageImpl"));
        return new SimpleInventory(0);
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
