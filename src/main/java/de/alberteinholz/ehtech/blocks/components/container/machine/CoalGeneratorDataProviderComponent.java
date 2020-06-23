package de.alberteinholz.ehtech.blocks.components.container.machine;

import java.util.List;

import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;

public class CoalGeneratorDataProviderComponent extends MachineDataProviderComponent {
    public SimpleDataElement heat = new SimpleDataElement().withBar(273.15, 273.15, 1773.15, UnitManager.KELVIN);

    public CoalGeneratorDataProviderComponent() {
        this("block.ehtech.coal_generator");
    }

    public CoalGeneratorDataProviderComponent(String name) {
        super(name);
    }

    @Override
    public void provideData(List<DataElement> data) {
        super.provideData(data);
        data.add(heat);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        if (unit == heat.getBarUnit()) {
            return heat;
        } else {
            return super.getElementFor(unit);
        }
    }

    public void addHeat(double value) {
        setHeat(heat.getBarCurrent() + value);
    }

    public void decreaseHeat() {
        setHeat(heat.getBarCurrent() - 0.1);
    }

    private void setHeat(double value) {
        value = value > heat.getBarMaximum() ? heat.getBarMaximum() : value < heat.getBarMinimum() ? heat.getBarMinimum() : value;
        heat.withBar(heat.getBarMinimum(), value, heat.getBarMaximum(), heat.getBarUnit());
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("Heat", NbtType.NUMBER)) {
            setHeat(tag.getDouble("Heat"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (heat.getBarCurrent() > 273.15) {
            tag.putDouble("Heat", heat.getBarCurrent());
        }
        return tag;
    }
}