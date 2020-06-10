package de.alberteinholz.ehtech.blocks.guis.screens;

import de.alberteinholz.ehtech.blocks.guis.controllers.ContainerCraftingController;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class EHContainerScreen extends CottonInventoryScreen<ContainerCraftingController> {
    public EHContainerScreen(ContainerCraftingController container, PlayerEntity player) {
        super(container, player);
        this.container.screen = this;
    }

    public MinecraftClient getMinecraftClient() {
        return minecraft;
    }
}