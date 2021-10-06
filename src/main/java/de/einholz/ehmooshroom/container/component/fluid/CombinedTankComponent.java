/*
package de.einholz.ehmooshroom.container.component.fluid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.einholz.ehmooshroom.container.component.CombinedComponent;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CombinedTankComponent extends CombinedComponent<TankComponent> implements TankComponent {
    protected final List<Runnable> listeners = new ArrayList<>();
    protected int tempTank = 0;

    @Override
    public CombinedTankComponent of(Map<Identifier, TankComponent> childComps) {
        Iterator<TankComponent> iter = childComps.values().iterator();
        while (iter.hasNext()) if (!(iter.next() instanceof TankComponent)) iter.remove();
        return (CombinedTankComponent) super.of(childComps);
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
        for (TankComponent comp : getComps().values()) ret.addAll(comp.getAllContents());
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
        for (TankComponent comp : getComps().values()) tanks += comp.getTanks();
        return tanks;
    }

    @Override
    public FluidVolume insertFluid(FluidVolume tank, ActionType action) {
        for (TankComponent comp : getComps().values()) tank = comp.insertFluid(tank, action);
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
		for (TankComponent comp : getComps().values()) comp.clear();
	}

	@Override
    public boolean isAcceptableFluid(int tank) {
		for (TankComponent comp : getComps().values()) if (comp.isAcceptableFluid(tank)) return true;
        return false;
	}

	@Override
    public Fraction amountOf(Set<Fluid> fluids) {
		Fraction amount = Fraction.ZERO;
        for (TankComponent comp : getComps().values()) amount.add(comp.amountOf(fluids));
		return amount;
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
        CombinedComponent.writeNbt(nbt, "CombinedTankComponent", getComps());
	}
    
    @Override
	public void readFromNbt(NbtCompound nbt) {
        CombinedComponent.readNbt(nbt, "CombinedTankComponent", getComps());
	}

    protected TankComponent getCompFromTank(int tank) {
        for (TankComponent comp : getComps().values()) if (comp.getTanks() <= tank) tank -= comp.getTanks() - 1;
        else {
            tempTank = tank;
            return comp;
        }
        return null;
    }
}
*/