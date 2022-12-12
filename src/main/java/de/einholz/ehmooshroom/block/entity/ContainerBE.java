package de.einholz.ehmooshroom.block.entity;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.storage.DummyStorage;
import de.einholz.ehmooshroom.storage.providers.FluidStorageProv;
import de.einholz.ehmooshroom.storage.providers.ItemStorageProv;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ContainerBE extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory, ItemStorageProv, FluidStorageProv {
    private Storage<ItemVariant> itemStorage = new DummyStorage<>();
    private Storage<FluidVariant> fluidStorage = new DummyStorage<>();
    
    public ContainerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity be) {

    }

    @Override
    public Storage<ItemVariant> getItemStorage(@Nullable Direction dir) {
        return itemStorage;
    }

    @Override
    public Storage<FluidVariant> getFluidStorage(@Nullable Direction dir) {
        return fluidStorage;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        // TODO Auto-generated method stub
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        // TODO Auto-generated method stub
        super.readNbt(nbt);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Text getDisplayName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScreenHandler createMenu(int arg0, PlayerInventory arg1, PlayerEntity arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        // TODO Auto-generated method stub
        
    }
}
