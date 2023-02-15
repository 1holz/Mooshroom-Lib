package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.ElectricityVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class ElectricityStorage implements SingleSlotStorage<ElectricityVariant> {
    private long cur = 0;
    private long max = 160000;

    @Override
    public long insert(ElectricityVariant arg0, long arg1, TransactionContext arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long extract(ElectricityVariant arg0, long arg1, TransactionContext arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getAmount() {
        return cur;
    }

    @Override
    public long getCapacity() {
        return max;
    }

    @Override
    public ElectricityVariant getResource() {
        return ElectricityVariant.INSTANCE;
    }

    @Override
    public boolean isResourceBlank() {
        return cur == 0;
    }
}
