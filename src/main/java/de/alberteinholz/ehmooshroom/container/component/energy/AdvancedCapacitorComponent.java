package de.alberteinholz.ehmooshroom.container.component.energy;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import de.alberteinholz.ehmooshroom.container.component.TransportingComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent.ConfigBehavior;
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

public class AdvancedCapacitorComponent extends SimpleCapacitorComponent implements TransportingComponent {
    protected Identifier id;
    protected ConfigDataComponent config;

    public AdvancedCapacitorComponent(Identifier id, EnergyType type) {
        this(id, getDefaultMaxFromType(type), type);
    }

    public AdvancedCapacitorComponent(Identifier id, int max, EnergyType type) {
        super(max, type);
        this.id = id;
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

    //TODO: check wether this has to be optimized/rewritten
    //check ((ConfigDataComponent) data).allowsConfig(MooshroomLib.HELPER.makeId("power_1"), configBehavior, dir) first
    public static int move(CapacitorComponent from, CapacitorComponent to, EnergyType type, Direction dir, ActionType action) {
        int transfer = 0;
        if (from.canExtractEnergy() && !(from instanceof AdvancedCapacitorComponent && !((AdvancedCapacitorComponent) from).canExtract(dir)) && to.canInsertEnergy() && !(to instanceof AdvancedCapacitorComponent && !((AdvancedCapacitorComponent) to).canInsert(dir))) {
            int extractionTest = from.extractEnergy(type, type.getMaximumTransferSize(), ActionType.TEST);
            int insertionCount = extractionTest - to.insertEnergy(type, extractionTest, action);
            int extractionCount = from.extractEnergy(type, insertionCount, action);
            transfer += extractionTest;
            if (insertionCount != extractionCount) MooshroomLib.LOGGER.smallBug(new IllegalStateException("Power moving wasn't performed correctly. This could lead to power deletion."));
        }
        return transfer;
    }

    public boolean canInsert(Direction dir) {
        return config.allowsConfig(id, ConfigBehavior.FOREIGN_INPUT, dir);
    }

    public boolean canExtract(Direction dir) {
        return config.allowsConfig(id, ConfigBehavior.FOREIGN_OUTPUT, dir);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("EnergyType", NbtType.STRING)) energyType = UniversalComponents.ENERGY_TYPES.get(new Identifier(tag.getString("EnergyType")));
        if (tag.contains("Energy", NbtType.NUMBER)) currentEnergy = tag.getInt("Energy");
        if (tag.contains("MaxEnergy", NbtType.NUMBER)) maxEnergy = tag.getInt("MaxEnergy");
        else maxEnergy = getDefaultMaxFromType(energyType);
        if (tag.contains("Harm", NbtType.NUMBER)) harm = tag.getInt("Harm");
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (energyType != EnergyTypes.ULTRA_LOW_VOLTAGE) tag.putString("EnergyType", UniversalComponents.ENERGY_TYPES.getId(energyType).toString());
        if (currentEnergy > 0) tag.putInt("Energy", currentEnergy);
        if (maxEnergy != getDefaultMaxFromType(energyType)) tag.putInt("MaxEnergy", maxEnergy);
        if (harm != 0) tag.putInt("Harm", harm);
        return tag;
    }
}