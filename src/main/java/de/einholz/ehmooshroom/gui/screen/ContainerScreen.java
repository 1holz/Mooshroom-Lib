package de.einholz.ehmooshroom.gui.screen;

import de.einholz.ehmooshroom.gui.gui.ContainerGui;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ContainerScreen<G extends SyncedGuiDescription> extends CottonInventoryScreen<G> {
    public ContainerScreen(G gui, PlayerInventory inventory, Text title) {
        this(gui, inventory.player, title);
    }

    @SuppressWarnings("unchecked")
    public ContainerScreen(G gui, PlayerEntity player, Text title) {
        super(gui, player, title);
        if (gui instanceof ContainerGui containerGui) containerGui.setScreen((ContainerScreen<? extends ContainerGui>) this);
    }

    public MinecraftClient getMinecraftClient() {
        return client;
    }
}
