package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.block.entity.ContainerBE;
import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import de.einholz.ehmooshroom.gui.screen.ContainerScreen;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

public class Reg {
    public static final RegEntry<ContainerBE, SideConfigGui, ContainerScreen<SideConfigGui>> SIDE_CONFIG = new RegEntryBuilder<ContainerBE, SideConfigGui, ContainerScreen<SideConfigGui>>().withGuiBuild(ExtendedScreenHandlerType<SideConfigGui>::new, SideConfigGui::init).withScreenBuild(ContainerScreen::new).build("side_config");
}
