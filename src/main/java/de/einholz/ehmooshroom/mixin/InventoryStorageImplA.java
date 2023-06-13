package de.einholz.ehmooshroom.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.Inventory;

// TODO del
@Deprecated
@Mixin(InventoryStorageImpl.class)
public interface InventoryStorageImplA {
    @Deprecated
    @Accessor
    Inventory getInventory();
}
