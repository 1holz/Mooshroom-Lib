package de.einholz.ehmooshroom.storage;

import java.util.Iterator;
import java.util.List;

import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class AdvItemStorage implements InventoryStorage, NbtSerializable {
    private InventoryStorage storage;
    private final Inventory inv;

    public AdvItemStorage(final int size) {
        this(new AdvInv(size));
    }

    public AdvItemStorage(final Identifier... ids) {
        this(new AdvInv(ids));
    }

    private AdvItemStorage(Inventory inventory) {
        storage = InventoryStorage.of(inventory, null);
        this.inv = inventory;
    }

    @Override
    public List<SingleSlotStorage<ItemVariant>> getSlots() {
        return storage.getSlots();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return storage.iterator(transaction);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (int i = 0; i < getInv().size(); i++) {
            ItemStack stack = getInv().getStack(i);
            if (stack.isEmpty()) continue;
            NbtCompound stackNbt = stack.writeNbt(new NbtCompound());
            list.add(i, stackNbt);
        }
        if (!list.isEmpty()) nbt.put("Inv", list);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        getInv().clear();
        NbtList list = nbt.getList("Inv", NbtType.COMPOUND);
        if (!list.isEmpty())
            for (int i = 0; i < list.size(); i++) {
                NbtCompound stackNbt = list.getCompound(i);
                if (stackNbt.isEmpty()) continue;
                getInv().setStack(i, ItemStack.fromNbt(stackNbt));
            }
        storage = InventoryStorage.of(inv, null);
    }

    public Inventory getInv() {
        return inv;
    }
}
