package de.alberteinholz.ehtech.blocks.components.container.machine;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.impl.SimpleCapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import io.github.cottonmc.component.energy.type.EnergyTypes;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class MachineCapacitorComponent extends SimpleCapacitorComponent {
    protected MachineDataProviderComponent dataProvider;

    public MachineCapacitorComponent(EnergyType type) throws IllegalStateException {
        this(getMaxFromType(type), type);
    }

    public MachineCapacitorComponent(int max, EnergyType type) {
        super(max, type);
    }

    protected static int getMaxFromType(EnergyType type) {
        int i = 0;
        if (type == EnergyTypes.ULTRA_LOW_VOLTAGE) {
            i = 1;
        } else if (type == EnergyTypes.LOW_VOLTAGE) {
            i = 2;
        } else if (type == EnergyTypes.MEDIUM_VOLTAGE) {
            i = 3;
        } else if (type == EnergyTypes.HIGH_VOLTAGE) {
            i = 4;
        } else if (type == EnergyTypes.ULTRA_HIGH_VOLTAGE) {
            i = 5;
        }
        return 10000 * 16 ^ i;
    }
    
    public void setEnergyType(EnergyType type) {
        energyType = type;
    }

    public void setDataProvider(MachineDataProviderComponent dataProvider) {
        this.dataProvider = dataProvider;
    }

    public int pull(CapacitorComponent target, ActionType action, Direction dir) {
        int transfer = 0;
        if (target.canExtractEnergy() && dataProvider != null && dataProvider.getConfig(ConfigType.POWER, ConfigBehavior.SELF_INPUT, dir) && !(target instanceof MachineCapacitorComponent && !((MachineCapacitorComponent) target).canExtract(dir))) {
            int extractTarget = target.extractEnergy(getPreferredType(), getPreferredType().getMaximumTransferSize(), action);
            int insert = insertEnergy(getPreferredType(), extractTarget, action);
            int insertTarget = target.insertEnergy(getPreferredType(), insert, action);
            transfer += extractTarget - insert;
            if (insertTarget != 0) {
                TechMod.LOGGER.smallBug(new Exception("Unable to insert power back to the origin. Power will be deleted!"));
            }
        }
        return transfer;
    }

    public int push(CapacitorComponent target, ActionType action, Direction dir) {
        int transfer = 0;
        if (target.canInsertEnergy() && dataProvider != null && dataProvider.getConfig(ConfigType.POWER, ConfigBehavior.SELF_OUTPUT, dir) && !(target instanceof MachineCapacitorComponent && !((MachineCapacitorComponent) target).canInsert(dir))) {
            int extract = extractEnergy(getPreferredType(), getPreferredType().getMaximumTransferSize(), action);
            int insertTarget = target.insertEnergy(getPreferredType(), extract, action);
            int insert = insertEnergy(getPreferredType(), insertTarget, action);
            transfer += extract - insertTarget;
            if (insert != 0) {
                TechMod.LOGGER.smallBug(new Exception("Unable to insert power back to the origin. Power will be deleted!"));
            }
        }
        return transfer;
    }

    public boolean canInsert(Direction dir) {
        return dataProvider.getConfig(ConfigType.POWER, ConfigBehavior.FOREIGN_INPUT, dir);
    }

    public boolean canExtract(Direction dir) {
        return dataProvider.getConfig(ConfigType.POWER, ConfigBehavior.FOREIGN_OUTPUT, dir);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("EnergyType", NbtType.STRING)) {
            energyType = UniversalComponents.ENERGY_TYPES.get(new Identifier(tag.getString("EnergyType")));
        }
        if (tag.contains("Energy", NbtType.NUMBER)) {
            currentEnergy = tag.getInt("Energy");
        }
        if (tag.contains("MaxEnergy", NbtType.NUMBER)) {
            maxEnergy = tag.getInt("MaxEnergy");
        } else {
            maxEnergy = getMaxFromType(energyType);
        }
        if (tag.contains("Harm", NbtType.NUMBER)) {
			harm = tag.getInt("Harm");
		}
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (energyType != EnergyTypes.ULTRA_LOW_VOLTAGE) {
            tag.putString("EnergyType", UniversalComponents.ENERGY_TYPES.getId(energyType).toString());
        }
        if (currentEnergy > 0) {
            tag.putInt("Energy", currentEnergy);
        }
        if (maxEnergy != getMaxFromType(energyType)) {
            tag.putInt("MaxEnergy", maxEnergy);
        }
        if (harm != 0) {
            tag.putInt("Harm", harm);
        }
        return tag;
    }
}