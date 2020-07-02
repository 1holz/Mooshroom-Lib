package de.alberteinholz.ehtech.items;

import java.util.List;

import de.alberteinholz.ehtech.blocks.blockentities.containerblockentities.machineblockentitys.MachineBlockEntity;
import de.alberteinholz.ehtech.blocks.directionalblocks.DirectionalBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Wrench extends Tool {
    public WrenchMode mode;
    
    public Wrench(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if (playerEntity.isSneaking()) {
            CompoundTag compoundTag = playerEntity.getStackInHand(hand).getOrCreateTag();
            mode = WrenchMode.ROTATE;
            if(compoundTag.contains("Mode")) {
                mode = WrenchMode.valueOf(compoundTag.getString("Mode"));
                mode = mode.next();
            }
            compoundTag.putString("Mode", mode.toString());
            if(world.isClient) {
                ((ClientPlayerEntity) playerEntity).sendMessage((Text)(new TranslatableText("title.ehtech.wrench", playerEntity.getStackInHand(hand).getTag().getString("Mode"))), true);
            }
            return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
        } else {
            return new TypedActionResult<>(ActionResult.PASS, playerEntity.getStackInHand(hand));
        }
        
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        Block block = blockState.getBlock();
        //BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
        if(context.getPlayer().isSneaking()) {
            if(mode == WrenchMode.ROTATE) {
                if(block instanceof DirectionalBlock) {
                    Direction direction = blockState.get(DirectionalBlock.FACING);
                    Direction[] values = Direction.values();
                    direction = values[(direction.ordinal() + 1) % values.length];
                    context.getWorld().setBlockState(context.getBlockPos(), blockState.with(Properties.FACING, direction));
                }
                return ActionResult.SUCCESS;
            } else if(mode == WrenchMode.POWER) {
                if(context.getWorld().isClient()) {
                    ((ClientPlayerEntity) context.getPlayer()).sendMessage(new TranslatableText("chat.ehtech.wip"), false);
                }
                return ActionResult.SUCCESS;
            } else if(mode == WrenchMode.ITEM) {
                if(context.getWorld().isClient()) {
                    ((ClientPlayerEntity) context.getPlayer()).sendMessage(new TranslatableText("chat.ehtech.wip"), false);
                }
                return ActionResult.SUCCESS;
            } else if(mode == WrenchMode.FLUID) {
                if(context.getWorld().isClient()) {
                    ((ClientPlayerEntity) context.getPlayer()).sendMessage(new TranslatableText("chat.ehtech.wip"), false);
                }
                return ActionResult.SUCCESS;
            } else if(mode == WrenchMode.CONFIGURE) {
                if(!context.getWorld().isClient()) {
                    context.getPlayer().openHandledScreen((MachineBlockEntity) context.getWorld().getBlockEntity(context.getBlockPos()));
                    //FIXME:ContainerProviderRegistry.INSTANCE.openContainer(BlockRegistry.getId(BlockRegistry.MACHINE_CONFIG), context.getPlayer(), buf -> buf.writeBlockPos(context.getBlockPos()));
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if(itemStack.getTag() != null && itemStack.getTag().getString("Mode") != null) {
            tooltip.add(new TranslatableText("tooltip.ehtech.wrench.withmode", itemStack.getTag().getString("Mode")));
        } else {
            tooltip.add(new TranslatableText("tooltip.ehtech.wrench.withoutmode"));
        }
    }

    public enum WrenchMode {
        ROTATE,
        POWER,
        ITEM,
        FLUID,
        CONFIGURE;

        private static WrenchMode[] values = values();

        public WrenchMode next() {
            return values[(ordinal() + 1) % values.length];
        }
    }
}