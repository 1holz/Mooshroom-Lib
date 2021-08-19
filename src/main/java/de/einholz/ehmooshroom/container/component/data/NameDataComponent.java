package de.einholz.ehmooshroom.container.component.data;

import java.util.List;

import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class NameDataComponent implements DataProviderComponent {
    protected SimpleDataElement containerName = new SimpleDataElement();
    protected final String defaultName;
    
    public NameDataComponent(Identifier id) {
        this(id.toString());
    }
    
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
        if (getName() != defaultName) tag.putString("Name", getName());
        return tag;
    }

    public String getName() {
        Identifier id = new Identifier(containerName.getLabel().asString());
        return "block." + id.getNamespace() + "." + id.getPath();
    }

    public void setName(Identifier id) {
        setName(id.toString());
    }

    public void setName(String name) {
        containerName.withLabel(name);
    }
}