package de.alberteinholz.ehtech.blocks.components.container.machine;

import de.alberteinholz.ehtech.TechMod;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
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

    public int pull(CapacitorComponent target, ActionType action) {
        int request = getPreferredType().getMaximumTransferSize();
        if (target.canExtractEnergy()) {
            int inserted = insertEnergy(getPreferredType(), request, ActionType.TEST);
            int extracted = target.extractEnergy(getPreferredType(), inserted, action);
            if (insertEnergy(getPreferredType(), extracted, action) != extracted) {
                TechMod.LOGGER.bigBug(new Exception("Couldn't extract extractable power!"));
            }
            return extracted;
        } else {
            return 0;
        }
    }

    public int push(CapacitorComponent target, ActionType action) {
        int request = getPreferredType().getMaximumTransferSize();
        if (target.canInsertEnergy()) {
            int extracted = extractEnergy(getPreferredType(), request, ActionType.TEST);
            int inserted = target.insertEnergy(getPreferredType(), extracted, action);
            if (extractEnergy(getPreferredType(), inserted, action) != inserted) {
                TechMod.LOGGER.bigBug(new Exception("Couldn't insert insertable power!"));
            }
            return request - extracted + inserted;
        } else {
            return request;
        }
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