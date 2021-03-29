package de.alberteinholz.ehmooshroom.container.component.energy;

import java.util.ArrayList;
import java.util.List;

import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CombinedCapacitorComponent implements CapacitorComponent {
    protected final List<Runnable> listeners = new ArrayList<>();
    protected final CapacitorComponent[] childComps;

    public CombinedCapacitorComponent(CapacitorComponent... comps) {
        childComps = comps;
    }

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    //XXX: is this actually needed?
	@Override
    public int generateEnergy(World world, BlockPos pos, int amount) {
        for (CapacitorComponent comp : childComps) {
            amount = comp.generateEnergy(world, pos, amount);
            if (amount <= 0) break;
        }
        return amount;
	}

    //FIXME: how to do this better?
	@Override
    public void emp(int strength) {
		childComps[0].emp(strength);
	}

    @Override
    public boolean canInsertEnergy() {
        for (CapacitorComponent comp : childComps) if (comp.canInsertEnergy()) return true;
        return false;
    }

    @Override
    public boolean canExtractEnergy() {
        for (CapacitorComponent comp : childComps) if (comp.canExtractEnergy()) return true;
        return false;
    }

    @Override
    public int insertEnergy(EnergyType type, int amount, ActionType action) {
        for (CapacitorComponent comp : childComps) {
            amount -= comp.insertEnergy(type, amount, action);
            if (amount <= 0) break;
        }
        return amount;
    }

    @Override
    public int extractEnergy(EnergyType type, int amount, ActionType action) {
        int ret = 0;
        for (CapacitorComponent comp : childComps) {
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
        for (CapacitorComponent comp : childComps) amount += comp.getCurrentEnergy();
        return amount;
    }

    @Override
    public int getHarm() {
        int amount = 0;
        for (CapacitorComponent comp : childComps) amount += comp.getHarm();
        return amount;
    }

    @Override
    public int getMaxEnergy() {
        int amount = 0;
        for (CapacitorComponent comp : childComps) amount += comp.getMaxEnergy();
        return amount;
    }

    //FIXME: how to do this better?
    @Override
    public EnergyType getPreferredType() {
        return childComps[0].getPreferredType();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
		ListTag compTag = new ListTag();
		for (CapacitorComponent comp : childComps) compTag.add(comp.toTag(new CompoundTag()));
        tag.put("CapacitorComponents", compTag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        ListTag compTag = tag.getList("CapacitorComponents", NbtType.LIST);
        for (int i = 0; i < childComps.length; i++) {
            CompoundTag invTag = compTag.getCompound(i);
            childComps[i].fromTag(invTag);
        }
    }
}
