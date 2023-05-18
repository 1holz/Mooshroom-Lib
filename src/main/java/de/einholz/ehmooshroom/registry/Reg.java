package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.block.entity.ContainerBE;
import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import de.einholz.ehmooshroom.recipe.AdvRecipe;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;

public class Reg {
    public static final RegEntry<ContainerBE, SideConfigGui, ContainerScreen<SideConfigGui>, Recipe<?>> SIDE_CONFIG = new RegEntryBuilder<ContainerBE, SideConfigGui, ContainerScreen<SideConfigGui>, Recipe<?>>().withGuiBuild(ExtendedScreenHandlerType<SideConfigGui>::new, SideConfigGui::init).withScreenBuild(ContainerScreen::new).build("side_config");
    //public static final RegEntry<BlockEntity, ScreenHandler, HandledScreen<ScreenHandler>, AdvRecipe> ADV_RECIPE_SERIALIZER = new RegEntryBuilder<BlockEntity, ScreenHandler, HandledScreen<ScreenHandler>, AdvRecipe>().withRecipeSerializerBuild(AdvRecipeSerializer.INSTANCE).build("adv_recipe_serializer");
    public static final RegEntry<BlockEntity, ScreenHandler, HandledScreen<ScreenHandler>, AdvRecipe> DUMMY_RECIPE_TYPE = new RegEntryBuilder<BlockEntity, ScreenHandler, HandledScreen<ScreenHandler>, AdvRecipe>().withGenericRecipeTypeBuild().build("dummy_recipe_type");
}
