package de.alberteinholz.ehtech.itemgroups;

import de.alberteinholz.ehtech.registry.ItemRegistry;
import de.alberteinholz.ehtech.util.Ref;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroups {
    public static final ItemGroup EH_TECH = FabricItemGroupBuilder.create(new Identifier(Ref.MOD_ID, "eh_tech")).icon(() -> new ItemStack(ItemRegistry.WRENCH.item)).build();
}