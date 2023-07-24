package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry.Factory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry.ExtendedClientHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class ScreenHandlerRegistry<T extends ScreenHandler> extends RegistryBuilder<ScreenHandlerType<?>> {
    public static final ScreenHandlerType<?> SIDE_CONFIG = new ScreenHandlerRegistry<SideConfigGui>()
            .register("side_config", ExtendedScreenHandlerType::new, SideConfigGui::init)
            .withScreen((Factory<SideConfigGui, ContainerScreen<SideConfigGui>>) ContainerScreen::new)
            .get();

    @SuppressWarnings("unchecked")
    public ScreenHandlerRegistry<T> register(String name, ScreenHandlerFactory<T, ScreenHandlerType<T>> factory,
            ExtendedClientHandlerFactory<T> clientHandlerFactory) {
        return (ScreenHandlerRegistry<T>) register(name, factory.create(clientHandlerFactory));
    }

    protected ScreenHandlerRegistry() {
    }

    @SuppressWarnings("unchecked")
    public ScreenHandlerRegistry<T> withScreen(Factory<T, ? extends HandledScreen<T>> factory) {
        if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType()))
            ScreenRegistry.register((ScreenHandlerType<T>) get(), factory);
        return this;
    }

    @Override
    protected Registry<ScreenHandlerType<?>> getRegistry() {
        return Registry.SCREEN_HANDLER;
    }

    @FunctionalInterface
    public static interface ScreenHandlerFactory<U extends ScreenHandler, T extends ScreenHandlerType<U>> {
        T create(ExtendedClientHandlerFactory<U> factory);
    }

    public static void registerAll() {
    }
}
