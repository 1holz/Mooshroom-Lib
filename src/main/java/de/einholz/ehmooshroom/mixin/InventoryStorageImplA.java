package de.einholz.ehmooshroom.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.inventory.Inventory;

@Mixin(InventoryStorageImpl.class)
public interface InventoryStorageImplA {
    @Accessor
    Inventory getInventory();
}
