package de.einholz.ehmooshroom.container.component.item;

import java.util.List;

import de.einholz.ehmooshroom.container.component.CompContextProvider;
import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

//TODO add compability with vanilla inventories!!!
public class SimpleItemComponent implements ItemComponent {
    private ComponentKey<ItemComponent> type;
    private List<ItemStack> inv;
    private int maxSlotSize = 64;
    private int maxTransfer;

    //short 0: internal, 1: input, 2: output, 3: storage
    //int size
    //int maxTransfer
    public SimpleItemComponent(CompContextProvider contextProvider) {
        short s = (short) contextProvider.getCompContext(getId())[0];
        type = s == 0 ? ItemComponent.ITEM_INTERNAL : s == 1 ? ItemComponent.ITEM_INPUT : s == 2 ? ItemComponent.ITEM_OUTPUT : ItemComponent.ITEM_STORAGE;
        inv = DefaultedList.ofSize((int) contextProvider.getCompContext(getId())[1], ItemStack.EMPTY);
        maxTransfer = (int) contextProvider.getCompContext(getId())[2];
    }

    @Override
    public Identifier getId() {
        return type.getId();
    }

    @Override
    public SideConfigComponent getSideConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Number getMaxTransfer() {
        return maxTransfer;
    }

    @Override
    public void setMaxTransfer(Number maxTransfer) {
        this.maxTransfer = maxTransfer.intValue();
    }

    @Override
    public List<ItemStack> getStacks() {
        return inv;
    }

    @Override
    public int getMaxStackSize() {
        return maxSlotSize;
    }
}
