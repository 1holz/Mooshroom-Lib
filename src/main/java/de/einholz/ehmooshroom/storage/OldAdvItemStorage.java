package de.einholz.ehmooshroom.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MapMaker;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

@Deprecated // TODO del
public class OldAdvItemStorage extends CombinedStorage<ItemVariant, SingleStackStorage> implements InventoryStorage {
    private static final Map<Inventory, OldAdvItemStorage> WRAPPERS = (new MapMaker()).weakValues().makeMap();
    protected final Inventory inventory;
    private final List<InventorySlotWrapper> backingList;
    protected final MarkDirtyParticipant markDirtyParticipant = new MarkDirtyParticipant();

    public OldAdvItemStorage(List<SingleStackStorage> parts) {
        super(parts);
        inventory = null;
        backingList = null;
        //TODO Auto-generated constructor stub
    }

    @Override
    public long extract(ItemVariant arg0, long arg1, TransactionContext arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long insert(ItemVariant arg0, long arg1, TransactionContext arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
    private AdvItemStorage(Inventory inventory) {
        super(inventory);
    }

    public static AdvItemStorage of() {
        return new AdvItemStorage() {

        };
    }
    */
 
    public static InventoryStorage of(Inventory inventory, /*@Nullable*/ Direction direction) {
        OldAdvItemStorage storage = WRAPPERS.computeIfAbsent(inventory, (inv) -> new OldAdvItemStorage(inv));
        int inventorySize = inventory.size();
        if (inventorySize != storage.parts.size()) {
            while(true) {
                if (storage.backingList.size() >= inventorySize) {
                    storage.parts = Collections.unmodifiableList(storage.backingList.subList(0, inventorySize));
                    break;
                }
                storage.backingList.add(new InventorySlotWrapper(storage, storage.backingList.size()));
            }
        }
        return storage.getSidedWrapper(direction);
    }
 
    OldAdvItemStorage(Inventory inventory) {
        super(Collections.emptyList());
        this.inventory = inventory;
        backingList = new ArrayList<>();
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots() {
        return new ArrayList<>(parts);
    }
 
    @Deprecated
    private InventoryStorage getSidedWrapper(/*@Nullable*/ Direction direction) {
        return null; // (InventoryStorage) (inventory instanceof SidedInventory && direction != null ? new SidedAdvItemStorage(this, direction) : this);
    }

    protected class MarkDirtyParticipant extends SnapshotParticipant<Boolean> {
		@Override
		protected Boolean createSnapshot() {
			return Boolean.TRUE;
		}

		@Override
		protected void readSnapshot(Boolean snapshot) {}

		@Override
		protected void onFinalCommit() {
			inventory.markDirty();
		}
	}
}
