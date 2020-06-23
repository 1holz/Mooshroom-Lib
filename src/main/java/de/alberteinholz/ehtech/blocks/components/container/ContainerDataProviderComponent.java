package de.alberteinholz.ehtech.blocks.components.container;

import java.util.List;

import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;

public class ContainerDataProviderComponent implements DataProviderComponent {
    public SimpleDataElement containerName = new SimpleDataElement();
    protected final String defaultName;
    
    public ContainerDataProviderComponent(String name) {
        setContainerName(name);
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
        if (tag.contains("ContainerName", NbtType.STRING)) {
            setContainerName(tag.getString("ContainerName"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (getContainerName() != defaultName) {
            tag.putString("ContainerName", getContainerName());
        }
        return tag;
    }

    public String getContainerName() {
        return containerName.getLabel().asString();
    }

    public void setContainerName(String name) {
        containerName.withLabel(name);
    }
}