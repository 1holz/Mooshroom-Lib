package de.einholz.ehmooshroom.registry.helpers;

import java.util.function.Function;

import de.einholz.ehmooshroom.recipe.AdvRecipe;
import de.einholz.ehmooshroom.registry.Reg;
import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;

public interface RecipesREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract RegEntryBuilder<B, G, S, R> withRecipeTypeRaw(Function<RegEntryBuilder<B, G, S, R>, RecipeType<R>> recipeTypeFunc);
    abstract RegEntryBuilder<B, G, S, R> withRecipeSerializerRaw(Function<RegEntryBuilder<B, G, S, R>, RecipeSerializer<R>> recipeSerializerFunc);

    default RegEntryBuilder<B, G, S, R> withRecipeTypeNull() {
        return withRecipeTypeRaw((entry) -> null);
    }

    default RegEntryBuilder<B, G, S, R> withRecipeTypeBuild(RecipeType<R> recipeType) {
        return withRecipeTypeRaw((entry) -> recipeType);
    }

    public final static class GenericRecipeType<T extends Recipe<?>> implements RecipeType<T> {
        public GenericRecipeType() {}
    }

    default RegEntryBuilder<B, G, S, R> withGenericRecipeTypeBuild() {
        return withRecipeTypeBuild(new GenericRecipeType<R>());
    }

    default RegEntryBuilder<B, G, S, R> withRecipeSerializerNull() {
        return withRecipeSerializerRaw((entry) -> null);
    }

    default RegEntryBuilder<B, G, S, R> withRecipeSerializerBuild(RecipeSerializer<R> recipeSerializer) {
        return withRecipeSerializerRaw((entry) -> recipeSerializer);
    }

    @SuppressWarnings("unchecked")
    default RegEntryBuilder<B, G, S, AdvRecipe> withAdvRecipeBuild() {
        return (RegEntryBuilder<B, G, S, AdvRecipe>) withGenericRecipeTypeBuild().withRecipeSerializerBuild((RecipeSerializer<R>) Reg.ADV_RECIPE_SERIALIZER.RECIPE_SERIALIZER);
    }
}
