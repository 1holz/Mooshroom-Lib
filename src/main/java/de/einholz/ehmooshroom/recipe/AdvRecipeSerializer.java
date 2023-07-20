package de.einholz.ehmooshroom.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

// TODO use jankson
public class AdvRecipeSerializer implements RecipeSerializer<AdvRecipe> {
    protected static final Factory FACTORY = AdvRecipe::new;

    // from file to server
    @Override
    public AdvRecipe read(Identifier id, JsonObject json) {
        // INPUT
        Ingredient<?>[] ingredients = new Ingredient[0];
        if (json.has("input")) {
            JsonArray jsonInput = JsonHelper.getArray(json, "input");
            List<Ingredient<?>> ingredientList = new ArrayList<Ingredient<?>>();
            for (int i = 0; i < jsonInput.size(); i++)
                ingredientList.add(Gredient.<Object, Ingredient<Object>>readFromJson(Ingredient::new, jsonInput.get(i)));
            ingredients = ingredientList.toArray(new Ingredient[ingredientList.size()]);
        }
        // OUTPUT
        Exgredient<?, ?>[] exgredients = new Exgredient[0];
        if (json.has("output")) {
            JsonArray jsonOutput = JsonHelper.getArray(json, "output");
            List<Exgredient<?, ?>> exgredientList = new ArrayList<Exgredient<?, ?>>();
            for (int i = 0; i < jsonOutput.size(); i++)
                exgredientList.add(Gredient.<Object, Exgredient<Object, TransferVariant<Object>>>readFromJson(Exgredient::new, jsonOutput.get(i)));
            exgredients = exgredientList.toArray(new Exgredient[exgredientList.size()]);
        }
        // TIME MODIFIER
        float timeModifier = JsonHelper.getFloat(json, "timeModifier", 1);
        return FACTORY.create(id, ingredients, exgredients, timeModifier);
    }

    // from server to packet
    @Environment(EnvType.SERVER)
    @Override
    public void write(PacketByteBuf buf, AdvRecipe recipe) {
        buf.writeVarInt(recipe.input.length);
        for (Ingredient<?> ingredient : recipe.input)
            ingredient.write(buf);
        buf.writeVarInt(recipe.output.length);
        for (Exgredient<?, ?> exgredient : recipe.output)
            exgredient.write(buf);
        buf.writeFloat(recipe.timeModifier);
    }

    // from packet to client
    @Environment(EnvType.CLIENT)
    @Override
    public AdvRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient<?>[] ingredients = new Ingredient[buf.readVarInt()];
        for (int i = 0; i < ingredients.length; i++)
            ingredients[i] = Gredient.<Object, Ingredient<Object>>read(Ingredient::new, buf);
        Exgredient<?, ?>[] exgredients = new Exgredient[buf.readVarInt()];
        for (int i = 0; i < exgredients.length; i++)
            exgredients[i] = Gredient.<Object, Exgredient<Object, TransferVariant<Object>>>read(Exgredient::new, buf);
        return FACTORY.create(id, ingredients, exgredients, buf.readFloat());
    }

    @FunctionalInterface
    private interface Factory {
        AdvRecipe create(Identifier id, Ingredient<?>[] input, Exgredient<?, ?>[] output, float timeModifier);
    }
}
