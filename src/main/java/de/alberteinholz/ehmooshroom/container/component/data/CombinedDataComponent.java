package de.alberteinholz.ehmooshroom.container.component.data;

import java.util.List;
import java.util.Map;

import de.alberteinholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class CombinedDataComponent extends CombinedComponent<DataProviderComponent> implements DataProviderComponent {
    public CombinedDataComponent(Map<Identifier, DataProviderComponent> childComps) {
        super(childComps);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        for (DataProviderComponent comp : childComps.values()) {
            DataElement element = comp.getElementFor(unit);
            if (!unit.equals(null)) return element;
        }
        return null;
    }

    //XXX: what if one comp clears the list?
    @Override
    public void provideData(List<DataElement> data) {
        for (DataProviderComponent comp : childComps.values()) comp.provideData(data);
    }

	@Override
	public CompoundTag toTag(CompoundTag tag) {
        return CombinedComponent.toTag(tag, "CombinedDataComponent", childComps);
	}

	@Override
	public void fromTag(CompoundTag tag) {
        CombinedComponent.fromTag(tag, "CombinedDataComponent", childComps);
    }
}
