package de.einholz.ehmooshroom.container.component.item;

import java.util.ArrayList;
import java.util.List;

import de.einholz.ehmooshroom.container.component.CompContextProvider;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;

//TODO add compability with vanilla inventories!!!
public class SimpleItemComponent implements ItemComponent {
    //private ComponentKey<ItemComponent> type;
    private List<Slot> inv = new ArrayList<>();
    private int maxTransfer;
    private SideConfigComponent sideConfig;

    /**
     * <p> OLD: short 0: internal, 1: input, 2: output, 3: storage
     * <p> OLD: int size
     * <p> int maxTransfer
     * <p> SlotFactory... inv:
     * @param contextProvider
     */
    public SimpleItemComponent(CompContextProvider contextProvider) {
        //short s = (short) contextProvider.getCompContext(getId())[0];
        //type = s == 0 ? ItemComponent.ITEM_INTERNAL : s == 1 ? ItemComponent.ITEM_INPUT : s == 2 ? ItemComponent.ITEM_OUTPUT : ItemComponent.ITEM_STORAGE;
        maxTransfer = (int) contextProvider.getCompContext(getId())[0];
        sideConfig = contextProvider.getSideConfig();
        for (Object slot : contextProvider.getCompContext(getId())) if (slot instanceof SlotFactory) addSlots(((SlotFactory) slot).getSlots());
    }

    @Override
    public SideConfigComponent getSideConfig() {
        return sideConfig;
    }

    @Override
    public List<Slot> getSlots() {
        return inv;
    }

    @Override
    public Number getMaxTransfer() {
        return maxTransfer;
    }

    @Override
    public void setMaxTransfer(Number maxTransfer) {
        this.maxTransfer = maxTransfer.intValue();
    }
}
