package de.einholz.ehmooshroom.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.recipe.Ingrediets.BlockIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.DataIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.EntityIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.FluidIngredient;
import de.einholz.ehmooshroom.recipe.Ingrediets.ItemIngredient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AdvancedRecipe implements Recipe<Inventory> {
    public final Identifier typeId;
    public final Identifier id;
    public final Input input;
    public final float consumes;
    public final Output output;
    public final float generates;
    public final float timeModifier;

    public AdvancedRecipe(Identifier id, Input input, float consumes, Output output, float generates, float timeModifier) {
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
        return Registry.RECIPE_SERIALIZER.get(typeId);
    }

    @Override
    public RecipeType<?> getType() {
        return Registry.RECIPE_TYPE.get(typeId);
    }

    public boolean matches(BlockPos pos, World world) {
        RecipeHolder be = (RecipeHolder) world.getBlockEntity(pos);
        //TODO: convert generate & consume to data ingredient or own ErnergyIngredient
        //if (consumes != Float.NaN && be.getMachineCapacitorComp().getCurrentEnergy() < consumes) return false;
        return input == null || (input.items == null || input.items.length == 0 || be.containsItemIngredients(input.items))
            && (input.fluids == null || input.fluids.length == 0 || be.containsFluidIngredients(input.fluids))
            && (input.blocks == null || input.blocks.length == 0 || be.containsBlockIngredients(input.blocks))
            && (input.entities == null || input.entities.length == 0 || be.containsEntityIngredients(input.entities))
            && (input.data == null || input.data.length == 0 || be.containsDataIngredients(input.data));
    }

    public static class Serializer implements RecipeSerializer<AdvancedRecipe> {
        private Factory factory;

        //XXX: where are these constructors used?
        public Serializer() {
            this(AdvancedRecipe::new);
        }

        public Serializer(Factory factory) {
            this.factory = factory;
        }
        
        //from file to server
        //TODO: make this shorter and/or make json to ingredient in input
        @Override
        public AdvancedRecipe read(Identifier id, JsonObject json) {
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
							MooshroomLib.LOGGER.bigBug(e);
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
							fluidIngredientsList.add(new FluidIngredient(new Identifier(JsonHelper.getString(fluidInput, "fluid")), JsonHelper.getFloat(fluidInput, "amount", 1), JsonHelper.hasString(fluidInput, "nbt") ? StringNbtReader.parse(JsonHelper.getString(fluidInput, "nbt")) : null));
						} catch (CommandSyntaxException e) {
							MooshroomLib.LOGGER.bigBug(e);
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
							MooshroomLib.LOGGER.bigBug(e);
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
							MooshroomLib.LOGGER.bigBug(e);
						}
                    }
                }
                DataIngredient[] dataIngredients = !dataIngredientsList.isEmpty() ? dataIngredientsList.toArray(new DataIngredient[dataIngredientsList.size()]) : null;
                input = new Input(itemIngredients, fluidIngredients, blockIngredients, entityIngredients, dataIngredients);
            }
            //CONSUMES:
            float consumes = json.has("consumes") ? json.get("consumes").getAsFloat() : Float.NaN;
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
								itemStacksList.add(ItemStack.fromNbt(StringNbtReader.parse(itemOutput.get("nbt").getAsString())));
							} catch (CommandSyntaxException e) {
								MooshroomLib.LOGGER.bigBug(e);
							}
                        } else itemStacksList.add(new ItemStack(Registry.ITEM.get(new Identifier(JsonHelper.getString(itemOutput, "item"))), JsonHelper.getInt(itemOutput, "amount", 1)));
                    }
                }
                ItemStack[] itemStacks = !itemStacksList.isEmpty() ? itemStacksList.toArray(new ItemStack[itemStacksList.size()]) : null;
                //Fluids:
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
                                entity.readNbt(StringNbtReader.parse(entityOutput.get("nbt").getAsString()));
                            } catch (CommandSyntaxException e) {
                                MooshroomLib.LOGGER.bigBug(e);
                            }
                        }
                        entitiesList.add(entity);
                    }
                }
                Entity[] entities = !entitiesList.isEmpty() ? entitiesList.toArray(new Entity[entitiesList.size()]) : null;
                //Data:
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
            //GENERATES:
            float generates = json.has("generates") ? json.get("generates").getAsFloat() : Float.NaN;
            //TIMEMODIFIER
            //Why does JsonHelper not support float values?
            float timeModifier = json.has("timeModifier") ? json.get("timeModifier").getAsFloat() : Float.NaN;
            return factory.create(id, input, consumes, output, generates, timeModifier);
        }

        //from server to packet
        @Environment(EnvType.SERVER)
        @Override
        public void write(PacketByteBuf buf, AdvancedRecipe recipe) {
            recipe.input.write(buf);
            buf.writeFloat(recipe.consumes);
            recipe.output.write(buf);
            buf.writeFloat(recipe.generates);
            buf.writeFloat(recipe.timeModifier);
        }

        //from packet to client
        @Environment(EnvType.CLIENT)
        @Override
        public AdvancedRecipe read(Identifier id, PacketByteBuf buf) {
            return factory.create(id, Input.read(buf), buf.readFloat(), Output.read(buf), buf.readFloat(), buf.readFloat());
        }

        public interface Factory {
            AdvancedRecipe create(Identifier id, Input input, float consumes, Output output, float generates, float timeModifier);
        }
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
