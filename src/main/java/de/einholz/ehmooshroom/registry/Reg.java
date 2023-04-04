package de.einholz.ehmooshroom.registry;

import de.einholz.ehmooshroom.gui.gui.SideConfigGui;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

public class Reg {
    public static final RegEntry SIDE_CONFIG = new RegEntryBuilder().withGuiBuild(ExtendedScreenHandlerType<SideConfigGui>::new, SideConfigGui::init).build("side_config");
}
