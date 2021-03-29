package de.alberteinholz.ehmooshroom.container.component.data;

import java.util.List;

import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CombinedDataComponent implements DataProviderComponent {
    protected final DataProviderComponent[] childComps;

    public CombinedDataComponent(DataProviderComponent... comps) {
        childComps = comps;
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        for (DataProviderComponent comp : childComps) {
            DataElement element = comp.getElementFor(unit);
            if (!unit.equals(null)) return element;
        }
        return null;
    }

    //XXX: what if one comp clears the list?
    @Override
    public void provideData(List<DataElement> data) {
        for (DataProviderComponent comp : childComps) comp.provideData(data);
    }

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag compTag = new ListTag();
		for (DataProviderComponent comp : childComps) compTag.add(comp.toTag(new CompoundTag()));
        tag.put("DataComponents", compTag);
        return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
        ListTag compTag = tag.getList("DataComponents", NbtType.LIST);
        for (int i = 0; i < childComps.length; i++) {
            CompoundTag invTag = compTag.getCompound(i);
            childComps[i].fromTag(invTag);
        }
    }
}
