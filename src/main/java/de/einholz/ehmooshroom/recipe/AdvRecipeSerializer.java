package de.einholz.ehmooshroom.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.einholz.ehmooshroom.MooshroomLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

// TODO use jankson
public class AdvRecipeSerializer implements RecipeSerializer<AdvRecipe> {
    protected final static Factory FACTORY = AdvRecipe::new;

    // from file to server
    @Override
    public AdvRecipe read(Identifier id, JsonObject json) {
        // INPUT
        Ingredient<?>[] ingredients = new Ingredient[0];
        if (json.has("input")) {
            JsonArray jsonInput = JsonHelper.getArray(json, "input");
            List<Ingredient<?>> ingredientList = new ArrayList<Ingredient<?>>();
            for (int i = 0; i < jsonInput.size(); i++) {
                JsonObject jsonIngredient = (JsonObject) jsonInput.get(i);
                try {
                    ingredientList.add(new Ingredient<>(
                            new Identifier(
                                    jsonIngredient.has("type") ? JsonHelper.getString(jsonIngredient, "type") : ""),
                            jsonIngredient.has("tagId") ? new Identifier(JsonHelper.getString(jsonIngredient, "tagId"))
                                    : null,
                            StringNbtReader.parse(JsonHelper.getString(jsonIngredient, "nbt", "{}")),
                            JsonHelper.getLong(jsonIngredient, "amount", 0)));
                } catch (CommandSyntaxException e) {
                    MooshroomLib.LOGGER.bigBug(e);
                }
            }
            ingredients = ingredientList.toArray(new Ingredient[ingredientList.size()]);
        }
        // OUTPUT
        Exgredient<?, ?>[] exgredients = new Exgredient[0];
        if (json.has("output")) {
            JsonArray jsonOutput = JsonHelper.getArray(json, "output");
            List<Exgredient<?, ?>> exgredientList = new ArrayList<Exgredient<?, ?>>();
            for (int i = 0; i < jsonOutput.size(); i++) {
                JsonObject jsonExgredient = (JsonObject) jsonOutput.get(i);
                try {
                    exgredientList.add(new Exgredient<>(
                            new Identifier(
                                    jsonExgredient.has("type") ? JsonHelper.getString(jsonExgredient, "type") : ""),
                            jsonExgredient.has("id") ? new Identifier(JsonHelper.getString(jsonExgredient, "id"))
                                    : null,
                            StringNbtReader.parse(JsonHelper.getString(jsonExgredient, "nbt", "{}")),
                            JsonHelper.getLong(jsonExgredient, "amount", 0)));
                } catch (CommandSyntaxException e) {
                    MooshroomLib.LOGGER.bigBug(e);
                }
            }
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
            ingredients[i] = Ingredient.read(buf);
        Exgredient<?, ?>[] exgredients = new Exgredient[buf.readVarInt()];
        for (int i = 0; i < exgredients.length; i++)
            exgredients[i] = Exgredient.read(buf);
        return FACTORY.create(id, ingredients, exgredients, buf.readFloat());
    }

    private interface Factory {
        AdvRecipe create(Identifier id, Ingredient<?>[] input, Exgredient<?, ?>[] output, float timeModifier);
    }
}
