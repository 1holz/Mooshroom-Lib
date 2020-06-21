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
    public static int pull(MachineDataProviderComponent data, ContainerInventoryComponent inventory, Inventory target, int maxTransfer, Direction dir) {
        int transfer = 0;
        for (int i = 0; i < target.getInvSize(); i++) {
            if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_INPUT, dir)) {
                ItemStack extracted = target.takeInvStack(i, maxTransfer);
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

    public static int push(MachineDataProviderComponent data, ContainerInventoryComponent inventory, Inventory target, int maxTransfer, Direction dir) {
        int transfer = 0;
        for (Entry<String, Slot> entry : inventory.getSlots(Type.OUTPUT).entrySet()) {
            if (((MachineDataProviderComponent) data).getConfig(ConfigType.ITEM, ConfigBehavior.SELF_OUTPUT, dir)) {
                ItemStack extracted = inventory.takeStack(entry.getKey(), maxTransfer, ActionType.TEST);
                for (int i = 0; i < target.getInvSize(); i++) {
                    int transfered = addInvStack(target, i, extracted);
                    transfer += transfered;
                    inventory.takeStack(entry.getKey(), transfered, ActionType.PERFORM);
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
        if (inv.getInvStack(slot).isItemEqual(stack)) {
            inv.getInvStack(slot).increment(stack.getCount());
            if (inv.getInvStack(slot).getCount() > inv.getInvMaxStackAmount()) {
                int leftover = inv.getInvStack(slot).getCount() - inv.getInvMaxStackAmount();
                if (inv.getInvStack(slot).getCount() > inv.getInvMaxStackAmount()) {
                    inv.getInvStack(slot).setCount(inv.getInvMaxStackAmount());
                    return stack.getCount() - leftover;
                } else {
                    return stack.getCount();
                }
            }
        }
        return 0;
    }
}