package de.alberteinholz.ehtech.blocks.components.container.machine;

import java.util.List;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.minecraft.nbt.CompoundTag;

public class MachineDataProviderComponent extends ContainerDataProviderComponent {
    public SimpleDataElement activationState = new SimpleDataElement(ActivationState.ALWAYS_ON.name());
    public SimpleDataElement efficiency = new SimpleDataElement(String.valueOf(1.0));
    public SimpleDataElement powerPerTick = new SimpleDataElement(String.valueOf(0));
    public SimpleDataElement progress = new SimpleDataElement().withBar(0.0, 0.0, 100.0, UnitManager.PERCENT);
    //in percent per tick * fuelSpeed
    public SimpleDataElement speed = new SimpleDataElement(String.valueOf(1.0));

    public MachineDataProviderComponent(String name) {
        super(name);
    }

    @Override
    public void provideData(List<DataElement> data) {
        data.add(activationState);
        data.add(efficiency);
        data.add(powerPerTick);
        data.add(progress);
        data.add(speed);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        if (unit == progress.getBarUnit()) {
            return progress;
        } else if (unit == UnitManager.WU_PER_TICK) {
            return powerPerTick;
        } else {
            return super.getElementFor(unit);
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        setActivationState((tag.getString("ActivationState")));
        setEfficiency(tag.getDouble("Efficiency"));
        setPowerPerTick(tag.getInt("PowerPerTick"));
        setProgress(tag.getCompound("Progress").getDouble("Current"));
        setSpeed(tag.getDouble("Speed"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("ActivationState", String.valueOf(getActivationState()));
        tag.putDouble("Efficiency", getEfficiency());
        tag.putInt("PowerPerTick", getPowerPerTick());
        CompoundTag progressTag = new CompoundTag();
        progressTag.putDouble("Current", progress.getBarCurrent());
        tag.put("Progress", progressTag);
        tag.putDouble("Speed", getSpeed());
        return tag;
    }

    public ActivationState getActivationState() {
        return ActivationState.valueOf(activationState.getLabel().getString());
    }

    public void setActivationState(String string) {
        assert ActivationState.isValid(string);
        activationState.withLabel(string);
    }

    public void nextActivationState() {
        setActivationState(String.valueOf(getActivationState().next(1)));
    }
    
    public double getEfficiency() {
        return Double.valueOf(efficiency.getLabel().getString());
    }

    public void setEfficiency(double value) {
        efficiency.withLabel(String.valueOf(value));
    }
        
    public int getPowerPerTick() {
        return Integer.valueOf(powerPerTick.getLabel().getString());
    }

    public void setPowerPerTick(int value) {
        powerPerTick.withLabel(String.valueOf(value));
    }

    public void setProgress(double current) {
        progress.withBar(progress.getBarMinimum(), current, progress.getBarMaximum(), progress.getBarUnit());
    }

    public double getSpeed() {
        return Double.valueOf(speed.getLabel().getString());
    }

    public void setSpeed(double value) {
        speed.withLabel(String.valueOf(value));
    }
    
    public enum ActivationState {
        ALWAYS_ON,
        REDSTONE_ON,
        REDSTONE_OFF,
        ALWAYS_OFF;

        private static ActivationState[] values = values();

        public static ActivationState value(int value) {
            return values[value];
        }

        public ActivationState next(int amount) {
            return value((ordinal() + amount) % values.length);
        }

        public static boolean isValid(String string) {
            try {
                toActivationState(string);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public static String toString(ActivationState state) {
            return state.name();
        }

        public static ActivationState toActivationState(String string) {
            return valueOf(string);
        }
    }
}