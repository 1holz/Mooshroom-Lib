package de.alberteinholz.ehmooshroom.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import de.alberteinholz.ehmooshroom.container.component.NamedComponent;
import de.alberteinholz.ehmooshroom.container.component.TransportingComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent;
import de.alberteinholz.ehmooshroom.container.component.data.NameDataComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent.ConfigBehavior;
import de.alberteinholz.ehmooshroom.container.component.item.AdvancedInventoryComponent;
import de.alberteinholz.ehmooshroom.container.component.item.AdvancedInventoryComponent.Slot;
import de.alberteinholz.ehmooshroom.container.component.item.AdvancedInventoryComponent.Slot.Type;
import de.alberteinholz.ehmooshroom.registry.RegistryEntry;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.core.BlockComponentHook;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.item.InventoryComponent;
import io.netty.buffer.Unpooled;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class AdvancedContainerBlockEntity extends BlockEntity implements BlockEntityClientSerializable, ExtendedScreenHandlerFactory {
    protected final ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory;
    protected Map<Identifier, Component> comps = new HashMap<>();

    public AdvancedContainerBlockEntity(RegistryEntry registryEntry) {
        this(registryEntry.id, registryEntry.clientHandlerFactory, registryEntry.blockEntityType);
    }

    public AdvancedContainerBlockEntity(Identifier titelTranslationKey, ExtendedClientHandlerFactory<? extends ScreenHandler> clientHandlerFactory, BlockEntityType<? extends BlockEntity> blockEntityType) {
        super(blockEntityType);
        this.clientHandlerFactory = clientHandlerFactory;
        addComponent(MooshroomLib.HELPER.makeId("name"), new NameDataComponent(titelTranslationKey));
        addComponent(MooshroomLib.HELPER.makeId("config"), new ConfigDataComponent());
    }

    @SuppressWarnings("unchecked")
    protected void addComponent(Identifier id, Component comp) {
        if (comp instanceof NamedComponent) ((NamedComponent) comp).setId(id);
        comps.put(id, comp);
        if (!(comp instanceof TransportingComponent)) return;
        getConfigComp().addConfig(id);
        ((TransportingComponent<Component>) comp).setConfig(getConfigComp());
    }

    public Map<Identifier, Component> getImmutableComps() {
        return new HashMap<>(comps);
    }

    //convenience access to some comps
    public AdvancedInventoryComponent getCombinedInvComp() {
        List<Slot> slots = new ArrayList<>();
        AdvancedInventoryComponent combinedInvComp = new AdvancedInventoryComponent(new Type[0], new Identifier[0]);
        for (Component comp : getImmutableComps().values()) if (comp instanceof AdvancedInventoryComponent) slots.addAll(((AdvancedInventoryComponent) comp).getSlots(null));
        for (int i = 0; i < slots.size(); i++) {
            combinedInvComp.addSlots(new Type[]{slots.get(i).type}, new Identifier[]{slots.get(i).id});
            combinedInvComp.setStack(i, slots.get(i).stack);
        }
        return combinedInvComp;
    }

    public NameDataComponent getNameComp() {
        return (NameDataComponent) getImmutableComps().get(MooshroomLib.HELPER.makeId("name"));
    }
    
    public ConfigDataComponent getConfigComp() {
        return (ConfigDataComponent) getImmutableComps().get(MooshroomLib.HELPER.makeId("config"));
    }

    //for TransportingComponents that aren't UniversalComponents.INVENTORY_COMPONENT, UniversalComponents.TANK_COMPONENT or UniversalComponents.CAPACITOR_COMPONENT you have to write the implementation yourself
    public void transfer() {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = pos.offset(dir);
            Direction targetDir = dir.getOpposite();
            for (Entry<Identifier, Component> entry : comps.entrySet()) {
                if (!(entry.getValue() instanceof TransportingComponent)) continue;
                Identifier id = entry.getKey();
                @SuppressWarnings("unchecked")
                TransportingComponent<Component> comp = (TransportingComponent<Component>) entry.getValue();
                BlockComponentHook hook = BlockComponentHook.INSTANCE;
                if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_INPUT, dir)) {
                    if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.pull(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.pull(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.pull(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                }
                if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_OUTPUT, dir)) {
                    if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                }
            }
        }
    }

    //you have to add all needed components first
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (world == null) return;
        for (String key : tag.getKeys()) {
            Identifier id = new Identifier(key);
            if (!tag.contains(key, NbtType.COMPOUND) || tag.getCompound(key).isEmpty()) continue;
            if (!getImmutableComps().containsKey(id)) {
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no component with the id " + key + " in the AdvancedContainer" + getDisplayName().getString()));
                continue;
            }
            CompoundTag compTag = tag.getCompound(key);
            getImmutableComps().get(id).fromTag(compTag);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (world == null) return tag;
        getImmutableComps().forEach((id, comp) -> {
            CompoundTag compTag = new CompoundTag();
            comp.toTag(compTag);
            if (!compTag.isEmpty()) tag.put(id.toString(), compTag);
        });
        return tag;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(world.getBlockState(pos), tag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getNameComp().getName());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return clientHandlerFactory.create(syncId, playerInv, buf);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}