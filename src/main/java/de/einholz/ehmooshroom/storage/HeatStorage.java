package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.HeatVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class HeatStorage implements SingleSlotStorage<HeatVariant> {
    private long cur = 0;
    private long max = 160000;

    @Override
    public long insert(HeatVariant arg0, long arg1, TransactionContext arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long extract(HeatVariant arg0, long arg1, TransactionContext arg2) {
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
    public HeatVariant getResource() {
        return HeatVariant.INSTANCE;
    }

    @Override
    public boolean isResourceBlank() {
        return cur == 0;
    }
}
