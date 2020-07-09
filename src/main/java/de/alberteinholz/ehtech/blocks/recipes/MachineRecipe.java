package de.alberteinholz.ehtech.blocks.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.blockentities.containers.machines.MachineBlockEntity;
import de.alberteinholz.ehtech.blocks.recipes.Input.BlockIngredient;
import de.alberteinholz.ehtech.blocks.recipes.Input.DataIngredient;
import de.alberteinholz.ehtech.blocks.recipes.Input.EntityIngredient;
import de.alberteinholz.ehtech.blocks.recipes.Input.FluidIngredient;
import de.alberteinholz.ehtech.blocks.recipes.Input.ItemIngredient;
import de.alberteinholz.ehtech.registry.BlockRegistry;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class MachineRecipe implements Recipe<Inventory> {
    public final Identifier typeId;
    public final Identifier id;
    public final Input input;
    public final double consumes;
    public final Output output;
    public final double generates;
    public final double timeModifier;

    public MachineRecipe(Identifier id, Input input, double consumes, Output output, double generates, double timeModifier) {
        this.typeId = new Identifier(id.getNamespace(), id.getPath().split("/")[1]);
        this.id = id;
        this.input = input;
        this.consumes = consumes;
        this.output = output;
        this.generates = generates;
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
        return BlockRegistry.getEntry(typeId).recipeSerializer;
    }

    @Override
    public RecipeType<?> getType() {
        return BlockRegistry.getEntry(typeId).recipeType;
    }

    public boolean matches(BlockPos pos, World world) {
        MachineBlockEntity be = (MachineBlockEntity) world.getBlockEntity(pos);
        if (input == null || (input.items == null || be.containsItemIngredients(input.items)) && (input.fluids == null || be.containsFluidIngredients(input.fluids)) && (input.blocks == null || be.containsBlockIngredients(input.blocks)) && (input.entities == null || be.containsEntityIngredients(input.entities)) && (input.data == null || be.containsDataIngredients(input.data))) {
            return true;
        } else {
            return false;
        }
    }

    public static class Serializer implements RecipeSerializer<MachineRecipe> {
        private final Factory factory;

        public Serializer() {
            this(MachineRecipe::new);
        }

        public Serializer(Factory factory) {
            this.factory = factory;
        }
        
        //from file to server
        //TODO: make this shorter
        @Override
        public MachineRecipe read(Identifier id, JsonObject json) {
            //INPUT:
            Input input = null;
            if (json.has("input")) {
                JsonObject jsonInput = JsonHelper.getObject(json, "input");
                //Items:
                List<ItemIngredient> itemIngredientsList = new ArrayList<ItemIngredient>();
                if (jsonInput.has("items")) {
                    JsonArray jsonItemInput = JsonHelper.getArray(jsonInput, "items");
                    for (int i = 0; i < jsonItemInput.size(); i++) {
                        JsonObject itemInput = (JsonObject) jsonItemInput.get(i);
                        try {
							itemIngredientsList.add(new ItemIngredient(new Identifier(JsonHelper.getString(itemInput, "item")), JsonHelper.getInt(itemInput, "amount", 1), JsonHelper.hasString(itemInput, "nbt") ? StringNbtReader.parse(JsonHelper.getString(itemInput, "nbt")) : null));
						} catch (CommandSyntaxException e) {
							TechMod.LOGGER.bigBug(e);
						}
                    }
                }
                ItemIngredient[] itemIngredients = !itemIngredientsList.isEmpty() ? itemIngredientsList.toArray(new ItemIngredient[itemIngredientsList.size()]) : null;
                //Fluids:
                List<FluidIngredient> fluidIngredientsList = new ArrayList<FluidIngredient>();
                if (jsonInput.has("fluids")) {
                    JsonArray jsonFluidInput = JsonHelper.getArray(jsonInput, "fluids");
                    for (int i = 0; i < jsonFluidInput.size(); i++) {
                        JsonObject fluidInput = (JsonObject) jsonFluidInput.get(i);
                        try {
							fluidIngredientsList.add(new FluidIngredient(new Identifier(JsonHelper.getString(fluidInput, "fluid")), Fraction.of(JsonHelper.getInt(fluidInput, "numerator", 1), JsonHelper.getInt(fluidInput, "denominator", 1)), JsonHelper.hasString(fluidInput, "nbt") ? StringNbtReader.parse(JsonHelper.getString(fluidInput, "nbt")) : null));
						} catch (CommandSyntaxException e) {
							TechMod.LOGGER.bigBug(e);
						}
                    }
                }
                FluidIngredient[] fluidIngredients = !fluidIngredientsList.isEmpty() ? fluidIngredientsList.toArray(new FluidIngredient[fluidIngredientsList.size()]) : null;
                //Blocks:
                //TODO: state & nbt support (blockentities?)
                List<BlockIngredient> blockIngredientsList = new ArrayList<BlockIngredient>();
                if (jsonInput.has("blocks")) {
                    JsonArray jsonBlockInput = JsonHelper.getArray(jsonInput, "blocks");
                    for (int i = 0; i < jsonBlockInput.size(); i++) {
                        JsonObject blockInput = (JsonObject) jsonBlockInput.get(i);
                        blockIngredientsList.add(new BlockIngredient(new Identifier(JsonHelper.getString(blockInput, "block"))));
                    }
                }
                BlockIngredient[] blockIngredients = !blockIngredientsList.isEmpty() ? blockIngredientsList.toArray(new BlockIngredient[blockIngredientsList.size()]) : null;
                //Entities:
                List<EntityIngredient> entityIngredientsList = new ArrayList<EntityIngredient>();
                if (jsonInput.has("entities")) {
                    JsonArray jsonEntityInput = JsonHelper.getArray(jsonInput, "entities");
                    for (int i = 0; i < jsonEntityInput.size(); i++) {
                        JsonObject entityInput = (JsonObject) jsonEntityInput.get(i);
                        try {
							entityIngredientsList.add(new EntityIngredient(new Identifier(JsonHelper.getString(entityInput, "entity")), JsonHelper.getInt(entityInput, "amount", 1), JsonHelper.hasString(entityInput, "nbt") ? StringNbtReader.parse(JsonHelper.getString(entityInput, "nbt")) : null));
						} catch (CommandSyntaxException e) {
							TechMod.LOGGER.bigBug(e);
						}
                    }
                }
                EntityIngredient[] entityIngredients = !entityIngredientsList.isEmpty() ? entityIngredientsList.toArray(new EntityIngredient[entityIngredientsList.size()]) : null;
                //Data:
                List<DataIngredient> dataIngredientsList = new ArrayList<DataIngredient>();
                if (jsonInput.has("data")) {
                    JsonArray jsonDataInput = JsonHelper.getArray(jsonInput, "data");
                    for (int i = 0; i < jsonDataInput.size(); i++) {
                        try {
							dataIngredientsList.add(new DataIngredient(StringNbtReader.parse(jsonDataInput.get(i).getAsString())));
						} catch (CommandSyntaxException e) {
							TechMod.LOGGER.bigBug(e);
						}
                    }
                }
                DataIngredient[] dataIngredients = !dataIngredientsList.isEmpty() ? dataIngredientsList.toArray(new DataIngredient[dataIngredientsList.size()]) : null;
                input = new Input(itemIngredients, fluidIngredients, blockIngredients, entityIngredients, dataIngredients);
            }
            //CONSUMES:
            double consumes = json.has("consumes") ? json.get("consumes").getAsDouble() : Double.NaN;
            //OUTPUT:
            Output output = null;
            if (json.has("output")) {
                JsonObject jsonOutput = JsonHelper.getObject(json, "output");
                //Items:
                List<ItemStack> itemStacksList = new ArrayList<ItemStack>();
                if (jsonOutput.has("items")) {
                    JsonArray jsonItemOutput = JsonHelper.getArray(jsonOutput, "items");
                    for (int i = 0; i < jsonItemOutput.size(); i++) {
                        JsonObject itemOutput = (JsonObject) jsonItemOutput.get(i);
                        if (itemOutput.has("nbt")) {
                            try {
								itemStacksList.add(ItemStack.fromTag(StringNbtReader.parse(itemOutput.get("nbt").getAsString())));
							} catch (CommandSyntaxException e) {
								TechMod.LOGGER.bigBug(e);
							}
                        } else {
                            itemStacksList.add(new ItemStack(Registry.ITEM.get(new Identifier(JsonHelper.getString(itemOutput, "item"))), JsonHelper.getInt(itemOutput, "amount", 1)));
                        }
                    }
                }
                ItemStack[] itemStacks = !itemStacksList.isEmpty() ? itemStacksList.toArray(new ItemStack[itemStacksList.size()]) : null;
                //Fluids:
                List<FluidVolume> fluidVolumesList = new ArrayList<FluidVolume>();
                if (jsonOutput.has("fluids")) {
                    JsonArray jsonFluidOutput = JsonHelper.getArray(jsonOutput, "fluids");
                    for (int i = 0; i < jsonFluidOutput.size(); i++) {
                        JsonObject fluidOutput = (JsonObject) jsonFluidOutput.get(i);
                        if (fluidOutput.has("nbt")) {
                            try {
								fluidVolumesList.add(FluidVolume.fromTag(StringNbtReader.parse(fluidOutput.get("nbt").getAsString())));
							} catch (CommandSyntaxException e) {
								TechMod.LOGGER.bigBug(e);
							}
                        } else {
                            fluidVolumesList.add(new FluidVolume(Registry.FLUID.get(new Identifier(JsonHelper.getString(fluidOutput, "fluid"))), Fraction.of(JsonHelper.getInt(fluidOutput, "numerator", 1), JsonHelper.getInt(fluidOutput, "denominator", 1))));
                        }
                    }
                }
                FluidVolume[] fluidVolumes = !fluidVolumesList.isEmpty() ? fluidVolumesList.toArray(new FluidVolume[fluidVolumesList.size()]) : null;
                //Blocks:
                //TODO: state & nbt support (blockentities?)
                List<BlockState> blockStatesList = new ArrayList<BlockState>();
                if (jsonOutput.has("blocks")) {
                    JsonArray jsonBlockOutput = JsonHelper.getArray(jsonOutput, "blocks");
                    for (int i = 0; i < jsonBlockOutput.size(); i++) {
                        JsonObject blockOutput = (JsonObject) jsonBlockOutput.get(i);
                        blockStatesList.add(Registry.BLOCK.get(new Identifier(JsonHelper.getString(blockOutput, "block"))).getDefaultState());
                    }
                }
                BlockState[] blockStates = !blockStatesList.isEmpty() ? blockStatesList.toArray(new BlockState[blockStatesList.size()]) : null;
                //Entities:
                List<Entity> entitiesList = new ArrayList<Entity>();
                if (jsonOutput.has("entities")) {
                    JsonArray jsonEntityOutput = JsonHelper.getArray(jsonOutput, "entities");
                    for (int i = 0; i < jsonEntityOutput.size(); i++) {
                        JsonObject entityOutput = (JsonObject) jsonEntityOutput.get(i);
                        Entity entity = Registry.ENTITY_TYPE.get(new Identifier(JsonHelper.getString(entityOutput, "entity"))).create(null);
                        if (entityOutput.has("nbt")) {
                            try {
                                entity.fromTag(StringNbtReader.parse(entityOutput.get("nbt").getAsString()));
                            } catch (CommandSyntaxException e) {
                                TechMod.LOGGER.bigBug(e);
                            }
                        }
                        entitiesList.add(entity);
                    }
                }
                Entity[] entities = !entitiesList.isEmpty() ? entitiesList.toArray(new Entity[entitiesList.size()]) : null;
                //Data:
                List<Tag> dataList = new ArrayList<Tag>();
                if (jsonOutput.has("data")) {
                    JsonArray jsonDataOutput = JsonHelper.getArray(jsonOutput, "data");
                    for (int i = 0; i < jsonDataOutput.size(); i++) {
                        try {
							dataList.add(StringNbtReader.parse(jsonDataOutput.get(i).getAsString()));
						} catch (CommandSyntaxException e) {
							TechMod.LOGGER.bigBug(e);
						}
                    }
                }
                Tag[] data = !dataList.isEmpty() ? dataList.toArray(new Tag[dataList.size()]) : null;
                output = new Output(itemStacks, fluidVolumes, blockStates, entities, data);
            }
            //GENERATES:
            double generates = json.has("generates") ? json.get("generates").getAsDouble() : Double.NaN;
            //TIMEMODIFIER
            //Why does JsonHelper not support double values?
            double timeModifier = json.has("timeModifier") ? json.get("timeModifier").getAsDouble() : Double.NaN;
            return factory.create(id, input, consumes, output, generates, timeModifier);
        }

        //from server to packet
        @Environment(EnvType.SERVER)
        @Override
        public void write(PacketByteBuf buf, MachineRecipe recipe) {
            recipe.input.write(buf);
            buf.writeDouble(recipe.consumes);
            recipe.output.write(buf);
            buf.writeDouble(recipe.generates);
            buf.writeDouble(recipe.timeModifier);
        }

        //from packet to client
        @Environment(EnvType.CLIENT)
        @Override
        public MachineRecipe read(Identifier id, PacketByteBuf buf) {
            return factory.create(id, Input.read(buf), buf.readDouble(), Output.read(buf), buf.readDouble(), buf.readDouble());
        }

        public interface Factory {
            MachineRecipe create(Identifier id, Input input, double consumes, Output output, double generates, double timeModifier);
        }
    }

    @Deprecated
    @Override
    public boolean matches(Inventory inv, World world) {
        return false;
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
}