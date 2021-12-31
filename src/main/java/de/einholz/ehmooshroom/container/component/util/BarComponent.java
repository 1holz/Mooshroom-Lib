package de.einholz.ehmooshroom.container.component.util;

import de.einholz.ehmooshroom.MooshroomLib;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;

//XXX use something different than float?
public interface BarComponent extends CustomComponent, CommonTickingComponent {
    public static final float ZERO = 0.0F;

    //IMPL:
    default void checkInit() {
        if (getMin() > ZERO || getMax() < ZERO) MooshroomLib.LOGGER.bigBug(new IllegalStateException("Minimal value must not be bigger than 0 and maximal value must not be smaller than 0 for " + getId().toString()));
    }

    //uses an offset for more prezise float calculations
    default float getOff() {
        return ZERO;
    };

    float getMin();
    float getCur();
    float getMax();

    void setCur(float cur);

    float getBalance();
    void setBalance(float bal);
    default float balance() {
        float ret = getBalance() - getCur();
        setBalance(getCur());
        return ret;

    }

    //API:
    default float getLow() {
        return getMin() + getOff();
    }

    default float getValue() {
        return getCur() + getOff();
    }

    default float setValue(float value) {
        setCur(value - getOff());
        return check();
    }

    default float getHigh() {
        return getMax() + getOff();
    }

    default float reset() {
        setCur(ZERO);
        return getValue();
    }

    default float decrease(float change) {
        return increase(change * -1) * -1;
    }

    //returns actual change
    default float increase(float change) {
        float cur = getCur();
        setCur(cur + change);
        return check() - cur;
    }

    default float check() {
        if (getCur() < getMin()) setCur(getMin());
        if (getCur() > getMax()) setCur(getMax());
        return getCur();
    }

    @Override
    default void tick() {
        balance();
    }

    @Override
    default void writeNbt(NbtCompound nbt) {
        if (getCur() != 0.0F) nbt.putFloat("Cur", getCur());
    }

    @Override
    default void readNbt(NbtCompound nbt) {
        if (nbt.contains("Cur", NbtType.NUMBER)) setCur(nbt.getFloat("Cur"));
    }
}
