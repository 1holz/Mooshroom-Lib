package de.einholz.ehmooshroom.gui.screens;

import de.einholz.ehmooshroom.gui.gui.ContainerGui;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ContainerScreen extends CottonInventoryScreen<SyncedGuiDescription> {
    public ContainerScreen(ScreenHandler description, PlayerInventory inventory, Text title) {
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
