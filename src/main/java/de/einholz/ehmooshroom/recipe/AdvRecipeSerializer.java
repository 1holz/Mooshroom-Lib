package de.einholz.ehmooshroom.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.einholz.ehmooshroom.recipe.deprecated.Input;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AdvRecipeSerializer implements RecipeSerializer<AdvancedRecipe> {
    private Factory factory;

    // XXX: where are these constructors used?
    public AdvRecipeSerializer() {
        this(AdvancedRecipe::new);
    }

    public AdvRecipeSerializer(Factory factory) {
        this.factory = factory;
    }
    
    // from file to server
    // TODO: make this shorter and/or make json to ingredient in input
    @Override
    public AdvancedRecipe read(Identifier id, JsonObject json) {
        // INPUT:
        Ingredient<?>[] ingredients = new Ingredient[0];
        if (json.has("input")) {
            JsonArray jsonInput = JsonHelper.getArray(json, "input");
            List<Ingredient<?>> ingredientList = new ArrayList<Ingredient<?>>();
            for (int i = 0; i < jsonInput.size(); i++) {
                JsonObject jsonIngredient = (JsonObject) jsonInput.get(i);
                ingredientList.add(new Ingredient<>(jsonIngredient.has("type") ? JsonHelper.getString(jsonIngredient, "type") : null, jsonIngredient.has("tagRegId") ? new Identifier(JsonHelper.getString(jsonIngredient, "tagRedId")) : null, JsonHelper.getString(jsonIngredient, "dataType", ""), jsonIngredient.has("tagId") ? new Identifier(JsonHelper.getString(jsonIngredient, "tagId")) : null, StringNbtReader.parse(JsonHelper.getString(jsonIngredient, "nbt", "{}")), JsonHelper.getLong(jsonIngredient, "amount", 0)));
            }
            ingredients = ingredientList.toArray(new Ingredient[ingredientList.size()]);
        }
        // CONSUMES:
        // float consumes = json.has("consumes") ? json.get("consumes").getAsFloat() : Float.NaN;
        // OUTPUT:
        Output output = null;
        if (json.has("output")) {
            JsonObject jsonOutput = JsonHelper.getObject(json, "output");
            // Items:
            List<ItemStack> itemStacksList = new ArrayList<ItemStack>();
            if (jsonOutput.has("items")) {
                JsonArray jsonItemOutput = JsonHelper.getArray(jsonOutput, "items");
                for (int i = 0; i < jsonItemOutput.size(); i++) {
                    JsonObject itemOutput = (JsonObject) jsonItemOutput.get(i);
                    if (itemOutput.has("nbt")) {
                        try {
                            itemStacksList.add(ItemStack.fromNbt(StringNbtReader.parse(itemOutput.get("nbt").getAsString())));
                        } catch (CommandSyntaxException e) {
                            MooshroomLib.LOGGER.bigBug(e);
                        }
                    } else itemStacksList.add(new ItemStack(Registry.ITEM.get(new Identifier(JsonHelper.getString(itemOutput, "item"))), JsonHelper.getInt(itemOutput, "amount", 1)));
                }
            }
            ItemStack[] itemStacks = !itemStacksList.isEmpty() ? itemStacksList.toArray(new ItemStack[itemStacksList.size()]) : null;
            // Fluids:
            /*TODO remake fluids
            List<FluidVolume> fluidVolumesList = new ArrayList<FluidVolume>();
            if (jsonOutput.has("fluids")) {
                JsonArray jsonFluidOutput = JsonHelper.getArray(jsonOutput, "fluids");
                for (int i = 0; i < jsonFluidOutput.size(); i++) {
                    JsonObject fluidOutput = (JsonObject) jsonFluidOutput.get(i);
                    if (fluidOutput.has("nbt")) {
                        try {
                            fluidVolumesList.add(FluidVolume.fromNbt(StringNbtReader.parse(fluidOutput.get("nbt").getAsString())));
                        } catch (CommandSyntaxException e) {
                            MooshroomLib.LOGGER.bigBug(e);
                        }
                    } else fluidVolumesList.add(new FluidVolume(Registry.FLUID.get(new Identifier(JsonHelper.getString(fluidOutput, "fluid"))), Fraction.of(JsonHelper.getInt(fluidOutput, "numerator", 1), JsonHelper.getInt(fluidOutput, "denominator", 1))));
                }
            }
            FluidVolume[] fluidVolumes = !fluidVolumesList.isEmpty() ? fluidVolumesList.toArray(new FluidVolume[fluidVolumesList.size()]) : null;
            */
            // Blocks:
            // TODO: state & nbt support (blockentities?)
            List<BlockState> blockStatesList = new ArrayList<BlockState>();
            if (jsonOutput.has("blocks")) {
                JsonArray jsonBlockOutput = JsonHelper.getArray(jsonOutput, "blocks");
                for (int i = 0; i < jsonBlockOutput.size(); i++) {
                    JsonObject blockOutput = (JsonObject) jsonBlockOutput.get(i);
                    blockStatesList.add(Registry.BLOCK.get(new Identifier(JsonHelper.getString(blockOutput, "block"))).getDefaultState());
                }
            }
            BlockState[] blockStates = !blockStatesList.isEmpty() ? blockStatesList.toArray(new BlockState[blockStatesList.size()]) : null;
            // Entities:
            List<Entity> entitiesList = new ArrayList<Entity>();
            if (jsonOutput.has("entities")) {
                JsonArray jsonEntityOutput = JsonHelper.getArray(jsonOutput, "entities");
                for (int i = 0; i < jsonEntityOutput.size(); i++) {
                    JsonObject entityOutput = (JsonObject) jsonEntityOutput.get(i);
                    Entity entity = Registry.ENTITY_TYPE.get(new Identifier(JsonHelper.getString(entityOutput, "entity"))).create(null);
                    if (entityOutput.has("nbt")) {
                        try {
                            entity.readNbt(StringNbtReader.parse(entityOutput.get("nbt").getAsString()));
                        } catch (CommandSyntaxException e) {
                            MooshroomLib.LOGGER.bigBug(e);
                        }
                    }
                    entitiesList.add(entity);
                }
            }
            Entity[] entities = !entitiesList.isEmpty() ? entitiesList.toArray(new Entity[entitiesList.size()]) : null;
            // Data:
            List<NbtElement> dataList = new ArrayList<NbtElement>();
            if (jsonOutput.has("data")) {
                JsonArray jsonDataOutput = JsonHelper.getArray(jsonOutput, "data");
                for (int i = 0; i < jsonDataOutput.size(); i++) {
                    try {
                        dataList.add(StringNbtReader.parse(jsonDataOutput.get(i).getAsString()));
                    } catch (CommandSyntaxException e) {
                        MooshroomLib.LOGGER.bigBug(e);
                    }
                }
            }
            NbtElement[] data = !dataList.isEmpty() ? dataList.toArray(new NbtElement[dataList.size()]) : null;
            output = new Output(itemStacks, /*fluidVolumes, */blockStates, entities, data);
        }
        // GENERATES:
        // float generates = json.has("generates") ? json.get("generates").getAsFloat() : Float.NaN;
        // TIMEMODIFIER
        // Why does JsonHelper not support float values?
        float timeModifier = JsonHelper.getFloat(json, "timeModifier", 1);
        return factory.create(id, input, output, timeModifier);
    }

    // from server to packet
    @Environment(EnvType.SERVER)
    @Override
    public void write(PacketByteBuf buf, AdvancedRecipe recipe) {
        recipe.input.write(buf);
        //buf.writeFloat(recipe.consumes);
        recipe.output.write(buf);
        //buf.writeFloat(recipe.generates);
        buf.writeFloat(recipe.timeModifier);
    }

    // from packet to client
    @Environment(EnvType.CLIENT)
    @Override
    public AdvancedRecipe read(Identifier id, PacketByteBuf buf) {
        return factory.create(id, Input.read(buf), buf.readFloat(), Output.read(buf), buf.readFloat(), buf.readFloat());
    }

    public interface Factory {
        AdvancedRecipe create(Identifier id, Input input, Output output, float timeModifier);
    }
}
