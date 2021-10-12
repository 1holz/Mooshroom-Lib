package de.einholz.ehmooshroom.container;

import de.einholz.ehmooshroom.container.types.ContainerWithFluids;
import de.einholz.ehmooshroom.container.types.ContainerWithItems;
import de.einholz.ehmooshroom.recipes.Ingrediets.BlockIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.DataIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.EntityIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.FluidIngredient;
import de.einholz.ehmooshroom.recipes.Ingrediets.ItemIngredient;
import de.einholz.ehmooshroom.registry.RegistryEntry;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class AdvancedContainerBE extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {
    protected final ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    
    public AdvancedContainerBE(RegistryEntry registryEntry) {
        this(registryEntry.id, registryEntry.clientHandlerFactory, registryEntry.blockEntityType);
    }

    public AdvancedContainerBE(Identifier titelTranslationKey, ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory, BlockEntityType<? extends BlockEntity> blockEntityType) {
        super(blockEntityType);
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean containsItemIngredients(ItemIngredient... ingredients) {
        return this instanceof ContainerWithItems ? ContainerWithItems.containsItems(this, ingredients) : ingredients.length == 0;
    }

    public boolean containsFluidIngredients(FluidIngredient... ingredients) {
        return this instanceof ContainerWithFluids ? ContainerWithFluids.containsFluids(this, ingredients) : ingredients.length == 0;
    }

    //only by overriding
    public boolean containsBlockIngredients(BlockIngredient... ingredients) {
        return ingredients.length == 0;
    }

    //only by overriding
    public boolean containsEntityIngredients(EntityIngredient... ingredients) {
        return ingredients.length == 0;
    }

    //only by overriding
    public boolean containsDataIngredients(DataIngredient... ingredients) {
        return ingredients.length == 0;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return clientHandlerFactory.create(syncId, inv, buf);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void fromClientTag(NbtCompound nbt) {
        fromTag(world.getBlockState(pos), nbt);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public NbtCompound toClientTag(NbtCompound nbt) {
        return writeNbt(nbt);
    }
}
