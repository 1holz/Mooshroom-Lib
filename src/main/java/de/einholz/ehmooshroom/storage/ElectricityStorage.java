package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.ElectricityVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class ElectricityStorage extends BarStorage<ElectricityVariant> {

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
    public ElectricityVariant getResource() {
        return ElectricityVariant.INSTANCE;
    }

    @Override
    public long getMax() {
        return 10000;
    }
}
