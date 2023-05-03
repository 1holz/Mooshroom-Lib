package de.einholz.ehmooshroom.registry.helpers;

import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public interface GuisREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract RegEntryBuilder<B, G, S, R> withGuiRaw(Function<RegEntryBuilder<B, G, S, R>, ScreenHandlerType<G>> guiFunc);
    abstract RegEntryBuilder<B, G, S, R> withScreenRaw(Function<RegEntryBuilder<B, G, S, R>, Factory<G, S>> screenFunc);

    default RegEntryBuilder<B, G, S, R> withGuiNull() {
        return withGuiRaw((entry) -> null);
    }

    @FunctionalInterface
    public static interface GuiFactory<G extends ScreenHandler, T extends ScreenHandlerType<G>> {
        T create(ExtendedClientHandlerFactory<G> factory);
    }

    default <T extends ScreenHandlerType<G>> RegEntryBuilder<B, G, S, R> withGuiBuild(GuiFactory<G, T> factory, ExtendedClientHandlerFactory<G> clientHandlerFactory) {
        return withGuiRaw((entry) -> factory.create(clientHandlerFactory));
    }

    /* TODO del?
    default RegEntry withGui(ExtendedClientHandlerFactory<G> clientHandlerFactory) {
        this.clientHandlerFactory = clientHandlerFactory;
        if (this.clientHandlerFactory != null) screenHandlerType = ScreenHandlerRegistry.registerExtended(id, this.clientHandlerFactory);
        return this;
    }
    */

    default RegEntryBuilder<B, G, S, R> withScreenNull() {
        return withScreenRaw((entry) -> null);
    }

    default RegEntryBuilder<B, G, S, R> withScreenBuild(Factory<G, S> screenFactory) {
        return withScreenRaw((entry) -> screenFactory);
    }

    /* TODO del if not needed
    @FunctionalInterface
    default static interface ScreenFactory<G extends ScreenHandler, T extends ScreenHandlerType<G>> {
        T create(ExtendedClientHandlerFactory<G> factory);
    }
    
    // fixme IF YOU KNOW A BETTER WAY OF DOING THIS PLEASE TELL ME!!!
    @SuppressWarnings({"rawtypes", "unchecked"})
    default RegEntry withScreenHacky(Factory screenFactory) {
        return withScreen(screenFactory);
    }

    default RegEntry<B, G, S, R> withScreen(Factory<G, S> screenFactory) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) return this;
        this.screenFactory = screenFactory;
        if (screenHandlerType == null) {
            MooshroomLib.LOGGER.smallBug(new NullPointerException("You must add a Gui before Screen for " + id.toString()));
            return this;
        }
        if (screenHandlerType != null && this.screenFactory != null) ScreenRegistry.<ScreenHandler, HandledScreen<ScreenHandler>>register(screenHandlerType, this.screenFactory);
        return this;
    }
    */
}
