package de.einholz.ehmooshroom.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AdvRecipe implements Recipe<Inventory> {
    private final Identifier typeId;
    private final Identifier id;
    public final Ingredient<?>[] input; // TODO add catalysts (maybe with own SideConfigAccessor?)
    //public final float consumes;
    public final Exgredient<?>[] output;
    //public final float generates;
    public final float timeModifier;

    public AdvRecipe(Identifier id, Ingredient<?>[] input, Exgredient<?>[] output, float timeModifier) {
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

    public boolean matches(BlockPos pos, World world) {
        if (world.getBlockEntity(pos) instanceof RecipeHolder rh) return rh.containsIngredients(input);
        return false;
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
        if (inv instanceof PosAsInv) return matches(((PosAsInv) inv).POS, world);
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
        for (Exgredient<?> out : output) if (out.getOutput() instanceof ItemStack stack) return stack;
        return ItemStack.EMPTY;
    }
}
