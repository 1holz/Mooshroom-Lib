package de.einholz.ehmooshroom.storage;

import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

// XXX is this needed?
@Deprecated
public interface AdvItemStorage extends InventoryStorage {
    public static AdvItemStorage of() {
        return new AdvItemStorage() {

        };
    }

    @Override
    default long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    default long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    default Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    default List<SingleSlotStorage<ItemVariant>> getSlots() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
