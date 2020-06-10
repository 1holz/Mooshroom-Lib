package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components;

import java.util.List;

import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.minecraft.nbt.CompoundTag;

public class ContainerDataProviderComponent implements DataProviderComponent {
    public SimpleDataElement containerName = new SimpleDataElement();
    
    public ContainerDataProviderComponent(String name) {
        setContainerName(name);
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
        containerName.withLabel(tag.getString("ContainerName"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("ContainerName", containerName.getLabel().getString());
        return tag;
    }

    public void setContainerName(String name) {
        containerName.withLabel(name);
    }
}