package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType.ExtendedFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HandledScreens.Provider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;

public class ScreenHandlerRegistry<T extends ScreenHandler> extends RegistryBuilder<ScreenHandlerType<?>> {
    public static final ScreenHandlerType<?> SIDE_CONFIG = new ScreenHandlerRegistry<SideConfigGui>()
            .register("side_config", ExtendedScreenHandlerType::new, SideConfigGui::init)
            .withScreen((Provider<SideConfigGui, ContainerScreen<SideConfigGui>>) ContainerScreen::new)
            .get();

    @SuppressWarnings("unchecked")
    public ScreenHandlerRegistry<T> register(String name, ScreenHandlerFactory<T, ScreenHandlerType<T>> factory,
            ExtendedFactory<T> clientHandlerFactory) {
        return (ScreenHandlerRegistry<T>) register(name, factory.create(clientHandlerFactory));
    }

    protected ScreenHandlerRegistry() {
    }

    @SuppressWarnings("unchecked")
    public ScreenHandlerRegistry<T> withScreen(Provider<T, ? extends HandledScreen<T>> factory) {
        if (EnvType.CLIENT.equals(FabricLoader.getInstance().getEnvironmentType()))
            HandledScreens.register((ScreenHandlerType<T>) get(), factory);
        return this;
    }

    @Override
    protected Registry<ScreenHandlerType<?>> getRegistry() {
        return Registry.SCREEN_HANDLER;
    }

    @FunctionalInterface
    public static interface ScreenHandlerFactory<U extends ScreenHandler, T extends ScreenHandlerType<U>> {
        T create(ExtendedFactory<U> factory);
    }

    public static void registerAll() {
    }
}
