package de.alberteinholz.ehmooshroom.container;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import de.alberteinholz.ehmooshroom.container.component.CombinedComponent;
import de.alberteinholz.ehmooshroom.container.component.NamedComponent;
import de.alberteinholz.ehmooshroom.container.component.TransportingComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent;
import de.alberteinholz.ehmooshroom.container.component.data.NameDataComponent;
import de.alberteinholz.ehmooshroom.container.component.data.ConfigDataComponent.ConfigBehavior;
import de.alberteinholz.ehmooshroom.container.component.item.AdvancedInventoryComponent;
import de.alberteinholz.ehmooshroom.container.component.item.CombinedInventoryComponent;
import de.alberteinholz.ehmooshroom.recipes.Input.BlockIngredient;
import de.alberteinholz.ehmooshroom.recipes.Input.DataIngredient;
import de.alberteinholz.ehmooshroom.recipes.Input.EntityIngredient;
import de.alberteinholz.ehmooshroom.recipes.Input.FluidIngredient;
import de.alberteinholz.ehmooshroom.recipes.Input.ItemIngredient;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
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

    //TODO:
    //maybe make them their own components?
    //declare the ids at the beginning of the class so it is easier to know them
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
    @SuppressWarnings({"unchecked", "unused"})
    public <T extends Component> CombinedComponent<T> getCombinedComp(CombinedComponent<T> combinedComponent) {
        Map<Identifier, T> map = new HashMap<>();
        for (Entry<Identifier, Component> entry : comps.entrySet()) {
            try {
                T value = (T) entry.getValue();
            } catch (ClassCastException e) {
                break;
            }
            map.put(entry.getKey(), (T) entry.getValue());
        }
        return combinedComponent.of(map);
    }

    public NameDataComponent getNameComp() {
        return (NameDataComponent) getImmutableComps().get(MooshroomLib.HELPER.makeId("name"));
    }
    
    public ConfigDataComponent getConfigComp() {
        return (ConfigDataComponent) getImmutableComps().get(MooshroomLib.HELPER.makeId("config"));
    }

    //for TransportingComponents that aren't UniversalComponents.INVENTORY_COMPONENT, UniversalComponents.TANK_COMPONENT or UniversalComponents.CAPACITOR_COMPONENT you would have to write the implementation yourself
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
                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.pull(hook.getTankComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.pull(hook.getCapComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                }
                if (getConfigComp().allowsConfig(id, ConfigBehavior.SELF_OUTPUT, dir)) {
                    if (comp instanceof InventoryComponent && hook.hasInvComponent(world, targetPos, targetDir)) comp.push(hook.getInvComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof TankComponent && hook.hasTankComponent(world, targetPos, targetDir)) comp.push(hook.getTankComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                    if (comp instanceof CapacitorComponent && hook.hasCapComponent(world, targetPos, targetDir)) comp.push(hook.getCapComponent(world, targetPos, targetDir), dir, ActionType.PERFORM);
                }
            }
        }
    }

    public boolean containsItemIngredients(ItemIngredient... ingredients) {
        for (ItemIngredient ingredient : ingredients) {
            int amount = ingredient.amount;
            for (InventoryComponent comp : getCombinedComp(new CombinedInventoryComponent()).getComps().values()) {
                if (comp instanceof AdvancedInventoryComponent) {
                    amount -= ((AdvancedInventoryComponent) comp).containsInput(ingredient);
                    /*TODO: delete
                    for (Slot slot : ((AdvancedInventoryComponent) comp).getSlots(Type.INPUT)) {
                        if (((Tag<Item>) ingredient.ingredient).contains(slot.stack.getItem())) {
                            amount -= slot.stack.getCount();
                            if (amount <= 0) break;
                        }
                    }
                    */
                } else for (ItemStack stack : comp.getStacks()) {
                    if ((ingredient.ingredient == null || ingredient.ingredient.contains(stack.getItem())) && (ingredient.tag == null || NbtHelper.matches(ingredient.tag, stack.getTag(), true))) amount -= stack.getCount();
                    if (amount <= 0) break;
                }
                //TODO: delete: amount -= comp.removeStack(amount, ActionType.TEST).getCount();
                if (amount <= 0) break;
            }
            if (amount > 0) return false;
        }
        return true;
    }

    public boolean containsFluidIngredients(FluidIngredient... ingredients) {
        boolean bl = true;
        for (FluidIngredient ingredient : ingredients) {
            MooshroomLib.LOGGER.wip("Containment Check for " + ingredient);
            //TODO:fluid
        }
        return bl;
    }

    //only by overriding
    public boolean containsBlockIngredients(BlockIngredient... ingredients) {
        return true;
    }

    //only by overriding
    public boolean containsEntityIngredients(EntityIngredient... ingredients) {
        return true;
    }

    //only by overriding
    public boolean containsDataIngredients(DataIngredient... ingredients) {
        return true;
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
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no Component with the id " + key + " in the AdvancedContainer" + getDisplayName().getString()));
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