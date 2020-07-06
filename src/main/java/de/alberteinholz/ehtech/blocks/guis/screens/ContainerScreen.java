package de.alberteinholz.ehtech.blocks.guis.screens;

import de.alberteinholz.ehtech.blocks.guis.guis.ContainerGui;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ContainerScreen extends CottonInventoryScreen<SyncedGuiDescription> {
    public ContainerScreen(SyncedGuiDescription description, PlayerInventory inventory, Text title) {
        this((ContainerGui) description, inventory.player, title);
    }

    public ContainerScreen(ContainerGui gui, PlayerEntity player, Text title) {
        super(gui, player, title);
        gui.screen = this;
    }

    public MinecraftClient getMinecraftClient() {
        return client;
    }
}