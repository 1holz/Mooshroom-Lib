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
    // TODO add catalysts (maybe with own SideConfigAccessor?)
    public final Ingredient<?>[] input;
    public final Exgredient<?, ?>[] output;
    public final float timeModifier;

    public AdvRecipe(Identifier id, Ingredient<?>[] input, Exgredient<?, ?>[] output, float timeModifier) {
        this.typeId = new Identifier(id.getNamespace(), id.getPath().split("/")[1]);
        this.id = id;
        this.input = input;
        this.output = output;
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
        if (world.getBlockEntity(pos) instanceof RecipeHolder rh)
            return rh.containsIngredients(input);
        return false;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public boolean matches(Inventory inv, World world) {
        if (inv instanceof PosAsInv)
            return matches(((PosAsInv) inv).POS, world);
        else
            return false;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public ItemStack craft(Inventory inv) {
        return getOutput();
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Deprecated(since = "0.0.5", forRemoval = false)
    @Override
    public ItemStack getOutput() {
        for (Exgredient<?, ?> out : output)
            if (out.getOutput() instanceof ItemStack stack)
                return stack;
        return ItemStack.EMPTY;
    }
}
