package de.einholz.ehmooshroom.container.component.energy;

import de.einholz.ehmooshroom.container.component.CompContextProvider;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import de.einholz.ehmooshroom.container.component.util.BarComponent;

public class SimpleEnergyComponent implements EnergyComponent {
    private float cur = BarComponent.ZERO;
    private final float max;
    private float bal;
    private float maxTransfer;

    //float max
    //float maxTransfer
    public SimpleEnergyComponent(CompContextProvider contextProvider) {
        max = (float) contextProvider.getCompContext(getId())[0];
        maxTransfer = (float) contextProvider.getCompContext(getId())[1];
        checkInit();
    }

    @Override
    public SideConfigComponent getSideConfig() {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public Number getMaxTransfer() {
        return maxTransfer;
    }

    @Override
    public void setMaxTransfer(Number maxTransfer) {
        this.maxTransfer = maxTransfer.floatValue();
    }
}
