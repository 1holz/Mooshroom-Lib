package de.einholz.ehmooshroom.registry.deprecated;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Deprecated
public interface BlockEntityTypeReg extends Reg {
    public static <T extends BlockEntity> BlockEntityType<T> registerRaw(Identifier id, BlockEntityType<T> blockEntityType) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, blockEntityType);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBE(Identifier id, Factory<T> blockEntityFactory, Block... blocks) {
        return registerRaw(id, FabricBlockEntityTypeBuilder.create(blockEntityFactory, blocks).build());
    }
}
