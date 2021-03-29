package de.alberteinholz.ehmooshroom.container.component.fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class CombinedTankComponent implements TankComponent {
    protected final List<Runnable> listeners = new ArrayList<>();
    protected final TankComponent[] childComps;
    protected int tempTank = 0;

    public CombinedTankComponent(TankComponent... comps) {
        childComps = comps;
    }

    @Override
    public List<Runnable> getListeners() {
        return listeners;
    }

    @Override
    public boolean canExtract(int tank) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? false : comp.canExtract(tempTank);
    }

    @Override
    public boolean canInsert(int tank) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? false : comp.canInsert(tempTank);
    }

    @Override
    public List<FluidVolume> getAllContents() {
        List<FluidVolume> ret = new ArrayList<>();
        for (TankComponent comp : childComps) ret.addAll(comp.getAllContents());
        return ret;
    }

    @Override
    public FluidVolume getContents(int tank) {
        TankComponent comp = getCompFromTank(tank);
        return comp.equals(null) ? FluidVolume.EMPTY : comp.getContents(tempTank);
    }

    @Override
    public Fraction getMaxCapacity(int tank) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? Fraction.ZERO : comp.getMaxCapacity(tank);
    }

    @Override
    public int getTanks() {
        int tanks = 0;
        for (TankComponent comp : childComps) tanks += comp.getTanks();
        return tanks;
    }

    @Override
    public FluidVolume insertFluid(FluidVolume tank, ActionType action) {
        for (TankComponent comp : childComps) tank = comp.insertFluid(tank, action);
        return tank;
    }

    @Override
    public FluidVolume insertFluid(int tank, FluidVolume fluid, ActionType action) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? fluid : comp.insertFluid(tempTank, fluid, action);
    }

    //XXX: why not also make search for a fluid possible?
    @Override
    public FluidVolume removeFluid(int tank, ActionType action) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? FluidVolume.EMPTY : comp.removeFluid(tempTank, action);
    }

    @Override
    public FluidVolume removeFluid(int tank, Fraction amount, ActionType action) {
        TankComponent comp = getCompFromTank(tank);
        return (comp.equals(null)) ? FluidVolume.EMPTY : comp.removeFluid(tempTank, amount, action);
    }

    @Override
    public void setFluid(int tank, FluidVolume fluid) {
        TankComponent comp = getCompFromTank(tank);
        if (!(comp.equals(null))) comp.setFluid(tempTank, fluid);
    }

    @Override
	public void clear() {
		for (TankComponent comp : childComps) comp.clear();
	}

	@Override
    public boolean isAcceptableFluid(int tank) {
		for (TankComponent comp : childComps) if (comp.isAcceptableFluid(tank)) return true;
        return false;
	}

	@Override
    public Fraction amountOf(Set<Fluid> fluids) {
		Fraction amount = Fraction.ZERO;
        for (TankComponent comp : childComps) amount.add(comp.amountOf(fluids));
		return amount;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag compTag = new ListTag();
		for (TankComponent comp : childComps) compTag.add(comp.toTag(new CompoundTag()));
        tag.put("TankComponents", compTag);
        return tag;
	}
    
    @Override
	public void fromTag(CompoundTag tag) {
        ListTag compTag = tag.getList("TankComponents", NbtType.LIST);
        for (int i = 0; i < childComps.length; i++) {
            CompoundTag invTag = compTag.getCompound(i);
            childComps[i].fromTag(invTag);
        }
	}

    protected TankComponent getCompFromTank(int tank) {
        for (TankComponent comp : childComps) if (comp.getTanks() <= tank) tank -= comp.getTanks() - 1;
        else {
            tempTank = tank;
            return comp;
        }
        return null;
    }
}
