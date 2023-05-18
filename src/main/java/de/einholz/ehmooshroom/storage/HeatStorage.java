package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.HeatVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class HeatStorage extends BarStorage<HeatVariant> {
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
    public HeatVariant getResource() {
        return HeatVariant.INSTANCE;
    }

    @Override
    public long getMax() {
        return 15000;
    }

    public void decrease() {
        setAmount(Math.max(BarStorage.MIN, getAmount()));
    }

    public static double toKelvin(long l) {
        return (double) l / 10D + 273.15D;
    }
}
