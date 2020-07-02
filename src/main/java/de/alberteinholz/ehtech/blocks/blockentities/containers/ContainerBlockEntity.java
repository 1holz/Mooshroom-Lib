package de.alberteinholz.ehtech.blocks.blockentities.containers;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public abstract class ContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {
    public ContainerInventoryComponent inventory = initializeInventoryComponent();
    public ContainerDataProviderComponent data = initializeDataProviderComponent();

    public ContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
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
    
    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    @Override
    public Text getDisplayName() {
        return data.containerName.getLabel();
    }

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