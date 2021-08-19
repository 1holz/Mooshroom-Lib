package de.einholz.ehmooshroom.container.component.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.einholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class CombinedDataComponent extends CombinedComponent<DataProviderComponent> implements DataProviderComponent {
    @Override
    public CombinedDataComponent of(Map<Identifier, DataProviderComponent> childComps) {
        Iterator<DataProviderComponent> iter = childComps.values().iterator();
        while (iter.hasNext()) if (!(iter.next() instanceof DataProviderComponent)) iter.remove();
        return (CombinedDataComponent) super.of(childComps);
    }
    
    @Override
    public DataElement getElementFor(Unit unit) {
        for (DataProviderComponent comp : getComps().values()) {
            DataElement element = comp.getElementFor(unit);
            if (!unit.equals(null)) return element;
        }
        return null;
    }

    //XXX: what if one comp clears the list?
    @Override
    public void provideData(List<DataElement> data) {
        for (DataProviderComponent comp : getComps().values()) comp.provideData(data);
    }

	@Override
	public CompoundTag toTag(CompoundTag tag) {
        return CombinedComponent.toTag(tag, "CombinedDataComponent", getComps());
	}

	@Override
	public void fromTag(CompoundTag tag) {
        CombinedComponent.fromTag(tag, "CombinedDataComponent", getComps());
    }
}
