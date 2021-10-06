package de.einholz.ehmooshroom.container.component.item;

import de.einholz.ehmooshroom.container.component.config.SideConfigComponent;
import de.einholz.ehmooshroom.container.component.util.CustomComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class SimpleItemComponent implements ItemComponent {
    private ComponentKey<ItemComponent> type;
    private DefaultedList<ItemStack> inv;
    private int maxSlotSize = 64;

    @Override
    public Identifier getId() {
        return type.getId();
    }

    @Override
    public <P> CustomComponent of(P provider) {
        // TODO Auto-generated method stub
        return null;
    }

    public SimpleItemComponent(ComponentKey<ItemComponent> type, int size) {
        this.type = type;
        inv = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public SideConfigComponent getSideConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Number getContent(ItemSpecification type) {
        int content = 0;
        for (ItemStack stack : inv) if (type.matches(stack)) content += stack.getCount();
        return content;
    }

    @Override
    public Number getSpace(ItemSpecification type) {
        int space = 0;
        for (ItemStack stack : inv) {
            if (stack.isEmpty() && type.size() != 1) space += maxSlotSize;
            else if (stack.isEmpty() || type.size() == 1 && type.matches(stack)) space += Math.min(maxSlotSize, type.getMinMaxStack());
        }
        return space;
    }

    @Override
    public Number getMaxTransfer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Number change(Number amount, Action action, ItemSpecification type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readNbt(NbtCompound tag) {
        // TODO Auto-generated method stub
        
    }
}
