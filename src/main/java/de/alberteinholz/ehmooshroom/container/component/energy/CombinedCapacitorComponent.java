package de.alberteinholz.ehmooshroom.container.component.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.alberteinholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CombinedCapacitorComponent extends CombinedComponent<CapacitorComponent> implements CapacitorComponent {
    protected final List<Runnable> listeners = new ArrayList<>();

    public CombinedCapacitorComponent(Map<Identifier, CapacitorComponent> childComps) {
        super(childComps);
    }

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    //XXX: is this actually needed?
	@Override
    public int generateEnergy(World world, BlockPos pos, int amount) {
        for (CapacitorComponent comp : childComps.values()) {
            amount = comp.generateEnergy(world, pos, amount);
            if (amount <= 0) break;
        }
        return amount;
	}

    //FIXME: how to do this better?
	@Override
    public void emp(int strength) {
		childComps.values().iterator().next().emp(strength);
	}

    @Override
    public boolean canInsertEnergy() {
        for (CapacitorComponent comp : childComps.values()) if (comp.canInsertEnergy()) return true;
        return false;
    }

    @Override
    public boolean canExtractEnergy() {
        for (CapacitorComponent comp : childComps.values()) if (comp.canExtractEnergy()) return true;
        return false;
    }

    @Override
    public int insertEnergy(EnergyType type, int amount, ActionType action) {
        for (CapacitorComponent comp : childComps.values()) {
            amount -= comp.insertEnergy(type, amount, action);
            if (amount <= 0) break;
        }
        return amount;
    }

    @Override
    public int extractEnergy(EnergyType type, int amount, ActionType action) {
        int ret = 0;
        for (CapacitorComponent comp : childComps.values()) {
            int temp = comp.insertEnergy(type, amount, action);
            amount -= temp;
            ret += temp;
            if (amount <= 0) break;
        }
        return ret;
    }

    @Override
    public int getCurrentEnergy() {
        int amount = 0;
        for (CapacitorComponent comp : childComps.values()) amount += comp.getCurrentEnergy();
        return amount;
    }

    @Override
    public int getHarm() {
        int amount = 0;
        for (CapacitorComponent comp : childComps.values()) amount += comp.getHarm();
        return amount;
    }

    @Override
    public int getMaxEnergy() {
        int amount = 0;
        for (CapacitorComponent comp : childComps.values()) amount += comp.getMaxEnergy();
        return amount;
    }

    //FIXME: how to do this better?
    @Override
    public EnergyType getPreferredType() {
        return childComps.values().iterator().next().getPreferredType();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        return CombinedComponent.toTag(tag, "CombinedCapacitorComponent", childComps);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        CombinedComponent.fromTag(tag, "CombinedCapacitorComponent", childComps);
    }
}
