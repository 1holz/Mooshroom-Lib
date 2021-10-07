package de.einholz.ehmooshroom.container.component.item;

import java.util.List;

import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
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

    @Override
    public Identifier getId() {
        return type.getId();
    }

    @Override
    public <P> CustomComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleItemComponent(ComponentKey<ItemComponent> type, int size, int maxTransfer) {
        this.type = type;
        inv = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.maxTransfer = maxTransfer;
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
