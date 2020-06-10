package de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.machineblocks.components;

import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import io.github.cottonmc.component.energy.type.EnergyTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class MachineCapacitorComponent extends SimpleCapacitorComponent {
    public MachineCapacitorComponent(int max) {
        super(max);
    }

    public MachineCapacitorComponent(EnergyType type) throws IllegalStateException {
        this(type == EnergyTypes.ULTRA_LOW_VOLTAGE ? 10000 * 16 ^ 1 : type == EnergyTypes.LOW_VOLTAGE ? 10000 * 16 ^ 2 : type == EnergyTypes.MEDIUM_VOLTAGE ? 10000 * 16 ^ 3 : type == EnergyTypes.HIGH_VOLTAGE ? 10000 * 16 ^ 4 : type == EnergyTypes.ULTRA_HIGH_VOLTAGE ? 10000 * 16 ^ 5 : null, type);
    }

    public MachineCapacitorComponent(int max, EnergyType type) {
        super(max, type);
    }
    
    public void setEnergyType(EnergyType type) {
        energyType = type;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        energyType = UniversalComponents.ENERGY_TYPES.get(new Identifier(tag.getString("EnergyType")));
        super.fromTag(tag);
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("EnergyType", UniversalComponents.ENERGY_TYPES.getId(energyType).toString());
        return super.toTag(tag);
    }
}