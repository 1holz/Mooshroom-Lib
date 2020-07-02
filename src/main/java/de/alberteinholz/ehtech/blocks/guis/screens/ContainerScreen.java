package de.alberteinholz.ehtech.blocks.guis.screens;

import de.alberteinholz.ehtech.blocks.guis.guis.ContainerGui;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerScreen extends CottonInventoryScreen<ContainerGui> {
    public ContainerScreen(ContainerGui description, PlayerEntity player) {
        super(description, player);
        description.screen = this;
    }

    public MinecraftClient getMinecraftClient() {
        return client;
    }
}