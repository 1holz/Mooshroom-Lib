package de.einholz.ehmooshroom.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public abstract class BarStorage<T> implements SingleSlotStorage<T> {
    public static final long MIN = 0L;
    private long cur = MIN;
    private long last = cur;
    private long balance;

    /* XXX needed?
    @Override
    public long insert(T resource, long maxAmount, TransactionContext transaction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public long extract(T resource, long maxAmount, TransactionContext transaction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extract'");
    }
    */

    @Override
    public boolean isResourceBlank() {
        return getAmount() <= MIN;
    }

    @Override
    public long getAmount() {
        return cur;
    }

    public void setAmount(long cur) {
        this.cur = cur;
    }

    abstract public long getMax();

    @Override
    public long getCapacity() {
        return getMax() - getAmount();
    }
    
    public long getBal() {
        return balance;
    }

    public void updateBal() {
        balance = getAmount() - last;
        last = getAmount();
    }
}
