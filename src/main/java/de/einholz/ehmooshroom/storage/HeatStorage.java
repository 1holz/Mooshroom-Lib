package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.HeatVariant;
import net.minecraft.block.entity.BlockEntity;

public class HeatStorage extends BarStorage<HeatVariant> {
    public HeatStorage(BlockEntity dirtyMarker) {
        super(dirtyMarker);
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
