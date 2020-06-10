package de.alberteinholz.ehtech.blocks.blockentities.containerblockentities;

import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.ContainerDataProviderComponent;
import de.alberteinholz.ehtech.blocks.directionalblocks.containerblocks.components.ContainerInventoryComponent;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class ContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public ContainerInventoryComponent inventory = initializeInventoryComponent();
    public ContainerDataProviderComponent data = initializeDataProviderComponent();

    public ContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (world != null) {
            inventory.fromTag(tag);
            data.fromTag(tag);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (world != null) {
            inventory.toTag(tag);
            data.toTag(tag);
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

    protected abstract ContainerInventoryComponent initializeInventoryComponent();

    protected abstract ContainerDataProviderComponent initializeDataProviderComponent();
}