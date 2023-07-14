package de.einholz.ehmooshroom.storage.storages;

import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
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
