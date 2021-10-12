package de.einholz.ehmooshroom.container;
/*XXX: Do we need this? maybe in the future
package de.alberteinholz.ehmooshroom.container;

import java.util.Map;
import java.util.NoSuchElementException;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import de.alberteinholz.ehmooshroom.container.component.data.NameDataComponent;
import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public interface AdvancedContainer {
    /*
    public AdvancedContainerBlockEntity(RegistryEntry registryEntry) {
        comps.put(MooshroomLib.HELPER.makeId("name"), new NameDataComponent("name"));
        comps.put(MooshroomLib.HELPER.makeId("config"), new ConfigDataComponent());
        comps.forEach((id, comp) -> {
            if (comp instanceof AdvancedInventoryComponent) ((AdvancedInventoryComponent) comp).setConfig((ConfigDataComponent) comps.get(MooshroomLib.HELPER.makeId("config")));
        });
    }
    */

    /*
    public Map<Identifier, Component> getComponents();

    public void addComponent(Component comp) {

    }

    public NameDataComponent getNameComponent();

    //you have to add all needed components first
    default public void fromTag(CompoundTag tag) {
        for (String key : tag.getKeys()) {
            Identifier id = new Identifier(key);
            if (!tag.contains(key, NbtType.COMPOUND) || tag.getCompound(key).isEmpty()) continue;
            if (!getComponents().containsKey(id)) {
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no component with the id " + key + " in the AdvancedContainer" + getDisplayName().getString()));
                continue;
            }
            CompoundTag compTag = tag.getCompound(key);
            getComponents().get(id).fromTag(compTag);
        }
    }

    default public CompoundTag toTag(CompoundTag tag) {
        getComponents().forEach((id, comp) -> {
            CompoundTag compTag = new CompoundTag();
            comp.toTag(compTag);
            if (!compTag.isEmpty()) tag.put(id.toString(), compTag);
        });
        return tag;
    }

    default public Text getDisplayName() {
        return new TranslatableText(getNameComponent().containerName.getLabel().asString());
    }

    default public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return registryEntry.clientHandlerFactory.create(syncId, playerInv, buf);
    }

    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
*/