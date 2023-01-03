package de.einholz.ehmooshroom.recipe;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AdvancedRecipe implements Recipe<Inventory> {
    public final Identifier typeId;
    public final Identifier id;
    public final Ingredient[] input;
    //public final float consumes;
    public final Output output;
    //public final float generates;
    public final float timeModifier;

    public AdvancedRecipe(Identifier id, Ingredient[] input, Output output, float timeModifier) {
        this.typeId = new Identifier(id.getNamespace(), id.getPath().split("/")[1]);
        this.id = id;
        this.input = input;
        //this.consumes = consumes;
        this.output = output;
        //this.generates = generates;
        this.timeModifier = timeModifier;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.RECIPE_SERIALIZER.get(typeId);
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(typeId);
    }

    // FIXME redo
    @Deprecated
    public boolean matches(BlockPos pos, World world) {
        RecipeHolder be = (RecipeHolder) world.getBlockEntity(pos);
        
        //TODO: convert generate & consume to data ingredient or own ErnergyIngredient
        //if (consumes != Float.NaN && be.getMachineCapacitorComp().getCurrentEnergy() < consumes) return false;
        //return input == null || (input.items == null || input.items.length == 0 || be.containsItemIngredients(input.items))
        //    && (input.fluids == null || input.fluids.length == 0 || be.containsFluidIngredients(input.fluids))
        //    && (input.blocks == null || input.blocks.length == 0 || be.containsBlockIngredients(input.blocks))
        //    && (input.entities == null || input.entities.length == 0 || be.containsEntityIngredients(input.entities))
        //    && (input.data == null || input.data.length == 0 || be.containsDataIngredients(input.data));
    }

    @Deprecated
    @Override
    public boolean matches(Inventory inv, World world) {
        if (inv instanceof PosAsInv) return matches(((PosAsInv) inv).pos, world);
        else return false;
    }

    @Deprecated
    @Override
    public ItemStack craft(Inventory inv) {
        return getOutput();
    }

    @Deprecated
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Deprecated
    @Override
    public ItemStack getOutput() {
        return (output != null && output.items != null && output.items.length > 0) ? output.items[0] : ItemStack.EMPTY;
    }

    //FIXME: use instead of InventoryWrapperPos later on
    //done maybe?
    @Deprecated
    public static final class PosAsInv implements Inventory {
        public BlockPos pos;

        public PosAsInv(BlockPos pos) {
            this.pos = pos;
        }

        private void error() {
            MooshroomLib.LOGGER.smallBug(new UnsupportedOperationException("This inventory represents a BlockPoos."));
        }

		@Override
		public void clear() {
            error();
        }

		@Override
		public boolean canPlayerUse(PlayerEntity player) {
            error();
            return false;
        }

		@Override
		public ItemStack getStack(int slot) {
            error();
            return ItemStack.EMPTY;
		}

		@Override
		public boolean isEmpty() {
            error();
            return true;
		}

		@Override
		public void markDirty() {
            error();
		}

		@Override
		public ItemStack removeStack(int slot) {
            error();
            return ItemStack.EMPTY;
        }

		@Override
		public ItemStack removeStack(int slot, int amount) {
            error();
            return ItemStack.EMPTY;
		}

		@Override
		public void setStack(int slot, ItemStack stack) {
            error();
        }

		@Override
		public int size() {
            error();
            return 0;
        }
    }
}
