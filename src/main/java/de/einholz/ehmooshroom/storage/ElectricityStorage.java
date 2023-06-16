package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.storage.transferable.ElectricityVariant;
import net.minecraft.block.entity.BlockEntity;

public class ElectricityStorage extends BarStorage<ElectricityVariant> {
    public ElectricityStorage(BlockEntity dirtyMarker) {
        super(dirtyMarker);
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
