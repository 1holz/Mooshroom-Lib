package de.einholz.ehmooshroom.container.component.energy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.einholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CombinedCapacitorComponent extends CombinedComponent<CapacitorComponent> implements CapacitorComponent {
    protected final List<Runnable> listeners = new ArrayList<>();

    @Override
    public CombinedCapacitorComponent of(Map<Identifier, CapacitorComponent> childComps) {
        Iterator<CapacitorComponent> iter = childComps.values().iterator();
        while (iter.hasNext()) if (!(iter.next() instanceof CapacitorComponent)) iter.remove();
        return (CombinedCapacitorComponent) super.of(childComps);
    }

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    //XXX: is this actually needed?
	@Override
    public int generateEnergy(World world, BlockPos pos, int amount) {
        for (CapacitorComponent comp : getComps().values()) {
            amount = comp.generateEnergy(world, pos, amount);
            if (amount <= 0) break;
        }
        return amount;
	}

    //FIXME: how to do this better?
	@Override
    public void emp(int strength) {
		getComps().values().iterator().next().emp(strength);
	}

    @Override
    public boolean canInsertEnergy() {
        for (CapacitorComponent comp : getComps().values()) if (comp.canInsertEnergy()) return true;
        return false;
    }

    @Override
    public boolean canExtractEnergy() {
        for (CapacitorComponent comp : getComps().values()) if (comp.canExtractEnergy()) return true;
        return false;
    }

    @Override
    public int insertEnergy(EnergyType type, int amount, ActionType action) {
        for (CapacitorComponent comp : getComps().values()) {
            amount -= comp.insertEnergy(type, amount, action);
            if (amount <= 0) break;
        }
        return amount;
    }

    @Override
    public int extractEnergy(EnergyType type, int amount, ActionType action) {
        int ret = 0;
        for (CapacitorComponent comp : getComps().values()) {
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
        for (CapacitorComponent comp : getComps().values()) amount += comp.getCurrentEnergy();
        return amount;
    }

    @Override
    public int getHarm() {
        int amount = 0;
        for (CapacitorComponent comp : getComps().values()) amount += comp.getHarm();
        return amount;
    }

    @Override
    public int getMaxEnergy() {
        int amount = 0;
        for (CapacitorComponent comp : getComps().values()) amount += comp.getMaxEnergy();
        return amount;
    }

    //FIXME: how to do this better?
    @Override
    public EnergyType getPreferredType() {
        return getComps().values().iterator().next().getPreferredType();
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        CombinedComponent.writeNbt(nbt, "CombinedCapacitorComponent", getComps());
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        CombinedComponent.readNbt(nbt, "CombinedCapacitorComponent", getComps());
    }
}
