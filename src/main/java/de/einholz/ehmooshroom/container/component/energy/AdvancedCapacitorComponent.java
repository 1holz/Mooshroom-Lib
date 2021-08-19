package de.einholz.ehmooshroom.container.component.energy;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.container.component.TransportingComponent;
import de.einholz.ehmooshroom.container.component.data.ConfigDataComponent;
import de.einholz.ehmooshroom.container.component.data.ConfigDataComponent.ConfigBehavior;
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

public class AdvancedCapacitorComponent extends SimpleCapacitorComponent implements TransportingComponent<CapacitorComponent> {
    private Identifier id;
    protected ConfigDataComponent config;

    public AdvancedCapacitorComponent(EnergyType type) {
        this(getDefaultMaxFromType(type), type);
    }

    public AdvancedCapacitorComponent(int max, EnergyType type) {
        super(max, type);
    }

    @Override
    public void setId(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    protected static int getDefaultMaxFromType(EnergyType type) {
        int i = 0;
        if (type == EnergyTypes.ULTRA_LOW_VOLTAGE) i = 1;
        else if (type == EnergyTypes.LOW_VOLTAGE) i = 2;
        else if (type == EnergyTypes.MEDIUM_VOLTAGE) i = 3;
        else if (type == EnergyTypes.HIGH_VOLTAGE) i = 4;
        else if (type == EnergyTypes.ULTRA_HIGH_VOLTAGE) i = 5;
        return 10000 * 16 ^ i;
    }
    
    public void setEnergyType(EnergyType type) {
        energyType = type;
    }

    @Override
    public void setConfig(ConfigDataComponent config) {
        this.config = config;
    }

    @Override
    public Number pull(CapacitorComponent from, Direction dir, ActionType action) {
        int transfer = 0;
        if (!canInsertEnergy() && !canInsert(dir) && !from.canExtractEnergy() && (!(from instanceof AdvancedCapacitorComponent) || ((AdvancedCapacitorComponent) from).canExtract(dir.getOpposite()))) return transfer;
        int extractionTest = from.extractEnergy(energyType, energyType.getMaximumTransferSize(), ActionType.TEST);
        int insertionCount = extractionTest - insertEnergy(energyType, extractionTest, action);
        int extractionCount = from.extractEnergy(energyType, insertionCount, action);
        transfer += extractionTest;
        if (insertionCount != extractionCount) MooshroomLib.LOGGER.smallBug(new IllegalStateException("Power pulling wasn't performed correctly. This could lead to power deletion."));
        return transfer;
    }

    @Override
    public Number push(CapacitorComponent to, Direction dir, ActionType action) {
        int transfer = 0;
        if (!canExtractEnergy() && !canExtract(dir) && !to.canInsertEnergy() && (!(to instanceof AdvancedCapacitorComponent) || ((AdvancedCapacitorComponent) to).canInsert(dir.getOpposite()))) return transfer;
        int extractionTest = extractEnergy(energyType, energyType.getMaximumTransferSize(), ActionType.TEST);
        int insertionCount = extractionTest - to.insertEnergy(energyType, extractionTest, action);
        int extractionCount = extractEnergy(energyType, insertionCount, action);
        transfer += extractionTest;
        if (insertionCount != extractionCount) MooshroomLib.LOGGER.smallBug(new IllegalStateException("Power pushing wasn't performed correctly. This could lead to power deletion."));
        return transfer;
    }

    public boolean canInsert(Direction dir) {
        return config.allowsConfig(id, ConfigBehavior.FOREIGN_INPUT, dir);
    }

    public boolean canExtract(Direction dir) {
        return config.allowsConfig(id, ConfigBehavior.FOREIGN_OUTPUT, dir);
    }

    //TODO: IMPORTANT!?! make fromTag and toTag for other comps???
    @Override
    public void fromTag(CompoundTag tag) {
        if (!tag.contains("Capacitor", NbtType.COMPOUND)) return;
        CompoundTag fromTag = tag.getCompound("Capacitor");
        if (fromTag.contains("EnergyType", NbtType.STRING)) energyType = UniversalComponents.ENERGY_TYPES.get(new Identifier(fromTag.getString("EnergyType")));
        if (fromTag.contains("Energy", NbtType.NUMBER)) currentEnergy = fromTag.getInt("Energy");
        if (fromTag.contains("MaxEnergy", NbtType.NUMBER)) maxEnergy = fromTag.getInt("MaxEnergy");
        else maxEnergy = getDefaultMaxFromType(energyType);
        if (fromTag.contains("Harm", NbtType.NUMBER)) harm = fromTag.getInt("Harm");
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag toTag = new CompoundTag();
        if (energyType != EnergyTypes.ULTRA_LOW_VOLTAGE) toTag.putString("EnergyType", UniversalComponents.ENERGY_TYPES.getId(energyType).toString());
        if (currentEnergy > 0) toTag.putInt("Energy", currentEnergy);
        if (maxEnergy != getDefaultMaxFromType(energyType)) toTag.putInt("MaxEnergy", maxEnergy);
        if (harm != 0) toTag.putInt("Harm", harm);
        if (!toTag.isEmpty()) tag.put("Capacitor", toTag);
        return tag;
    }
}