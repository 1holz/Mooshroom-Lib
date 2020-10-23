package de.alberteinholz.ehmooshroom.container.component;

import java.util.List;

import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;

public class NameDataComponent implements DataProviderComponent {
    public SimpleDataElement containerName = new SimpleDataElement();
    protected final String defaultName;
    
    public NameDataComponent(String name) {
        setName(name);
        defaultName = name;
    }

    @Override
    public void provideData(List<DataElement> data) {
        data.add(containerName);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        return null;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("Name", NbtType.STRING)) setName(tag.getString("Name"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (getContainerName() != defaultName) tag.putString("Name", getContainerName());
        return tag;
    }

    public String getContainerName() {
        return containerName.getLabel().asString();
    }

    public void setName(String name) {
        containerName.withLabel(name);
    }
}