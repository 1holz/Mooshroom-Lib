package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities;

import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.components.container.ContainerInventoryComponent;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class ContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public ContainerInventoryComponent inventory = initializeInventoryComponent();
    public ContainerDataProviderComponent data = initializeDataProviderComponent();

    public ContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
        inventory.setDataProvider(data);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
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
        fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    protected ContainerInventoryComponent initializeInventoryComponent() {
        return new ContainerInventoryComponent();
    }

    protected ContainerDataProviderComponent initializeDataProviderComponent() {
        return new ContainerDataProviderComponent("block.ehtech.container");
    }
}