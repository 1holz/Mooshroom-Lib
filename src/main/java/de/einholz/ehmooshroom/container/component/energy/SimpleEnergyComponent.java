package de.einholz.ehmooshroom.container.component.energy;

import de.einholz.ehmooshroom.container.component.util.BarComponent;

public class SimpleEnergyComponent implements EnergyComponent {
    private float cur = BarComponent.ZERO;
    private final float max;
    private float bal;

    @Override
    public <P> SimpleEnergyComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleEnergyComponent(float range) {
        this.max = range;
        checkInit();
    }

    @Override
    public float getCur() {
        return cur;
    }

    @Override
    public float getMax() {
        return max;
    }

    @Override
    public void setCur(float cur) {
        this.cur = cur;
    }

    @Override
    public float getBalance() {
        return bal;
    }

    @Override
    public void setBalance(float bal) {
        this.bal = bal;
    }
}
