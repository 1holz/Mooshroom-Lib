package de.alberteinholz.ehtech.blocks.blockentities.containers;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public abstract class ContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {
    protected final BlockRegistry registryEntry;
    public ContainerInventoryComponent inventory = initializeInventoryComponent();
    public ContainerDataProviderComponent data = initializeDataProviderComponent();

    public ContainerBlockEntity(BlockRegistry registryEntry) {
        super(registryEntry.blockEntityType);
        this.registryEntry = registryEntry;
        inventory.setDataProvider(data);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (world != null) {
            if (tag.contains("Inventory", NbtType.COMPOUND)) {
                inventory.fromTag(tag.getCompound("Inventory"));
            }
            if (tag.contains("Data", NbtType.COMPOUND)) {
                data.fromTag(tag.getCompound("Data"));
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (world != null) {
            CompoundTag inventoryTag = new CompoundTag();
            inventory.toTag(inventoryTag);
            if (!inventoryTag.isEmpty()) {
                tag.put("Inventory", inventoryTag);
            }
            CompoundTag dataTag = new CompoundTag();
            data.toTag(dataTag);
            if (!dataTag.isEmpty()) {
                tag.put("Data", dataTag);
            }
        }
        return tag;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(null, tag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    //@Environment(EnvType.SERVER)
    @Override
    public Text getDisplayName() {
        return data.containerName.getLabel();
    }

    //@Environment(EnvType.SERVER)
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return registryEntry.clientHandlerFactory.create(syncId, playerInv, buf);
    }

    //@Environment(EnvType.SERVER)
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    protected ContainerInventoryComponent initializeInventoryComponent() {
        return new ContainerInventoryComponent();
    }

    protected ContainerDataProviderComponent initializeDataProviderComponent() {
        return new ContainerDataProviderComponent("block.ehtech.container");
    }
}