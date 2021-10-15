package de.einholz.ehmooshroom.container.component.heat;

import de.einholz.ehmooshroom.container.component.CompContextProvider;
import de.einholz.ehmooshroom.container.component.util.BarComponent;

public class SimpleHeatComponent implements HeatComponent {
    private final float min;
    private float cur = BarComponent.ZERO;
    private final float max;
    private float bal;

    //float min usually 0
    //float max 1500 for basic Machine
    public SimpleHeatComponent(CompContextProvider contextProvider) {
        min = (float) contextProvider.getCompContext(getId())[0];
        max = (float) contextProvider.getCompContext(getId())[1];
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
