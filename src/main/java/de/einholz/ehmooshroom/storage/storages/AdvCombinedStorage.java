package de.einholz.ehmooshroom.storage.storages;

import java.util.ArrayList;
import java.util.List;

import de.einholz.ehmooshroom.registry.TransferableRegistry;
import de.einholz.ehmooshroom.storage.SideConfigType;
import de.einholz.ehmooshroom.storage.SideConfigType.SideConfigAccessor;
import de.einholz.ehmooshroom.storage.StorageEntry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class AdvCombinedStorage<T, V extends TransferVariant<T>, S extends Storage<V>> extends CombinedStorage<V, S> {
    private final List<StorageEntry<T, V>> entries;
    private final SideConfigAccessor acc;
    private final boolean blockInsertion;
    private final boolean blockExtraction;

    @SuppressWarnings("unchecked")
    public AdvCombinedStorage(boolean blockInsertion, boolean blockExtraction, SideConfigAccessor acc,
            List<StorageEntry<T, V>> parts) {
        super((List<S>) extractStorages(parts));
        this.entries = parts;
        this.acc = acc;
        this.blockInsertion = blockInsertion;
        this.blockExtraction = blockExtraction;
    }

    public AdvCombinedStorage(SideConfigAccessor acc, List<StorageEntry<T, V>> parts) {
        this(false, false, acc, parts);
    }

    @Override
    public boolean supportsInsertion() {
        if (blockInsertion)
            return false;
        for (StorageEntry<T, V> entry : entries)
            if (entry.allows(SideConfigType.getFromParams(true, false, acc)))
                return entry.storage.supportsInsertion();
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        if (blockExtraction)
            return false;
        for (StorageEntry<T, V> entry : entries)
            if (entry.allows(SideConfigType.getFromParams(true, true, acc)))
                return entry.storage.supportsExtraction();
        return false;
    }

    /**
     * @return An immutable, merged representation of all Inventories
     */
    public Inventory getAsInv() {
        List<ItemStack> stacks = new ArrayList<>();
        for (StorageEntry<T, V> entry : entries) {
            if (!TransferableRegistry.ITEMS.equals(entry.trans))
                continue;
            try (Transaction trans = Transaction.openOuter()) {
                var iter = entry.storage.iterator(trans);
                while (iter.hasNext()) {
                    StorageView<V> view = iter.next();
                    if (view.getResource() instanceof ItemVariant variant)
                        stacks.add(itemVariantToStack(variant, view.getAmount()));
                }
            }
        }
        return new SimpleInventory(stacks.toArray(new ItemStack[0]));
    }

    public static ItemStack itemVariantToStack(ItemVariant variant, long amount) {
        ItemStack stack = new ItemStack(variant.getItem(), (int) amount);
        stack.setNbt(variant.copyNbt());
        return stack;
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends TransferVariant<T>, S extends Storage<V>> List<S> extractStorages(
            List<StorageEntry<T, V>> list) {
        List<S> newList = new ArrayList<>();
        for (StorageEntry<T, V> entry : list)
            newList.add((S) entry.storage);
        return newList;
    }
}
