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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class MachineCapacitorComponent extends SimpleCapacitorComponent {
    protected MachineDataProviderComponent dataProvider;

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
        energyType = UniversalComponents.ENERGY_TYPES.get(new Identifier(tag.getString("EnergyType")));
        super.fromTag(tag);
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("EnergyType", UniversalComponents.ENERGY_TYPES.getId(energyType).toString());
        return super.toTag(tag);
    }
}