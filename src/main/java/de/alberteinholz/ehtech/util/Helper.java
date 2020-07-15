package de.alberteinholz.ehtech.util;

import java.util.Map.Entry;

import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent.Slot;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent.Slot.Type;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigBehavior;
import de.alberteinholz.ehtech.blocks.components.container.machine.MachineDataProviderComponent.ConfigType;
import io.github.cottonmc.component.api.ActionType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class Helper {
    @Deprecated
    public static int pull(MachineDataProviderComponent data, ContainerInventoryComponent inventory, Inventory target, int maxTransfer, Direction dir) {
        int transfer = 0;
        for (int i = 0; i < target.size(); i++) {
            if (Boolean.TRUE.equals(((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_INPUT, dir))) {
                ItemStack extracted = target.removeStack(i, maxTransfer);
                for (Entry<String, Slot> inEntry : inventory.getSlots(Type.INPUT).entrySet()) {
                    int insertedCount = inventory.insertStack(inEntry.getKey(), extracted, ActionType.PERFORM).getCount();
                    transfer += insertedCount;
                    if (transfer >= maxTransfer) {
                        break;
                    }
                };
                if (transfer >= maxTransfer) {
                    break;
                }
            }
        };
        return transfer;
    }

    @Deprecated
    public static int push(MachineDataProviderComponent data, ContainerInventoryComponent inventory, Inventory target, int maxTransfer, Direction dir) {
        int transfer = 0;
        for (Entry<String, Slot> entry : inventory.getSlots(Type.OUTPUT).entrySet()) {
            if (Boolean.TRUE.equals(((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_OUTPUT, dir))) {
                ItemStack extracted = inventory.removeStack(entry.getKey(), maxTransfer, ActionType.TEST);
                for (int i = 0; i < target.size(); i++) {
                    int transfered = addInvStack(target, i, extracted);
                    transfer += transfered;
                    inventory.removeStack(entry.getKey(), transfered, ActionType.PERFORM);
                    if (transfer >= maxTransfer) {
                        break;
                    }
                }
                if (transfer >= maxTransfer) {
                    break;
                }
            }
        };
        return transfer;
    }

    private static int addInvStack(Inventory inv, int slot, ItemStack stack) {
        if (inv.getStack(slot).isItemEqual(stack)) {
            inv.getStack(slot).increment(stack.getCount());
            if (inv.getStack(slot).getCount() > inv.getMaxCountPerStack()) {
                int leftover = inv.getStack(slot).getCount() - inv.getMaxCountPerStack();
                if (inv.getStack(slot).getCount() > inv.getMaxCountPerStack()) {
                    inv.getStack(slot).setCount(inv.getMaxCountPerStack());
                    return stack.getCount() - leftover;
                } else {
                    return stack.getCount();
                }
            }
        }
        return 0;
    }

    //TODO: make helper methode for translation keys
}