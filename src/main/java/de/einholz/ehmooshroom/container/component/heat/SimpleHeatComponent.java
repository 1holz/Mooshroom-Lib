package de.einholz.ehmooshroom.container.component.heat;

import de.einholz.ehmooshroom.container.component.util.BarComponent;

public class SimpleHeatComponent implements HeatComponent {
    private final float min;
    private float cur = BarComponent.ZERO;
    private final float max;
    private float bal;

    @Override
    public <P> SimpleHeatComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }

    //1500 for CoalGenerator
    public SimpleHeatComponent(float range) {
        this(BarComponent.ZERO, range);
    }

    public SimpleHeatComponent(float min, float max) {
        this.min = min;
        this.max = max;
        checkInit();
    }

    @Override
    public float getMin() {
        return min;
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
