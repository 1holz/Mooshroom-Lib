package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components;

import java.util.List;

import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.minecraft.nbt.CompoundTag;

public class CoalGeneratorDataProviderComponent extends MachineDataProviderComponent {
    @Deprecated
    public SimpleDataElement fuelHeating = new SimpleDataElement(String.valueOf(0.0));
    //in percent per tick * speed
    @Deprecated
    public SimpleDataElement fuelSpeed = new SimpleDataElement(String.valueOf(1.0));
    public SimpleDataElement heat = new SimpleDataElement().withBar(273.15, 273.15, 1773.15, UnitManager.KELVIN);

    public CoalGeneratorDataProviderComponent() {
        super("block.ehtech.coal_generator");
    }

    public CoalGeneratorDataProviderComponent(String name) {
        super(name);
    }

    @Override
    public void provideData(List<DataElement> data) {
        data.add(fuelHeating);
        data.add(fuelSpeed);
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

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        setFuelHeating(tag.getCompound("FuelData").getDouble("FuelHeating"));
        setFuelSpeed(tag.getCompound("FuelData").getDouble("FuelSpeed"));
        CompoundTag heatTag = tag.getCompound("Heat");
        heat.withBar(heat.getBarMinimum(), heatTag.getDouble("Current"), heatTag.getDouble("Max"), heat.getBarUnit());
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        CompoundTag fuelData = new CompoundTag();
        fuelData.putDouble("FuelHeating", getFuelHeating());
        fuelData.putDouble("FuelSpeed", getFuelSpeed());
        tag.put("FuelData", fuelData);
        CompoundTag heatTag = new CompoundTag();
        heatTag.putDouble("Current", heat.getBarCurrent());
        heatTag.putDouble("Max", heat.getBarMaximum());
        tag.put("Heat", heatTag);
        return tag;
    }

    @Deprecated
    public double getFuelHeating() {
        return Double.valueOf(fuelHeating.getLabel().getString());
    }

    @Deprecated
    public void setFuelHeating(double value) {
        fuelHeating.withLabel(String.valueOf(value));
    }

    @Deprecated
    public double getFuelSpeed() {
        return Double.valueOf(fuelSpeed.getLabel().getString());
    }

    @Deprecated
    public void setFuelSpeed(double value) {
        fuelSpeed.withLabel(String.valueOf(value));
    }

    public void setHeat(double current) {
        heat.withBar(heat.getBarMinimum(), current, heat.getBarMaximum(), heat.getBarUnit());
    }
}