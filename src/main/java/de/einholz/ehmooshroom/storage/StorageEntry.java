package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;

public class StorageEntry<T, U extends TransferVariant<T>> {
    public final Storage<U> storage;
    public final char[] config;
    public final Transferable<T, U> trans;

    public StorageEntry(Storage<U> storage, char[] config, Transferable<T, U> trans) {
        this.storage = storage;
        if (config.length != SideConfigType.values().length) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config char array should have a lenght of " + SideConfigType.values().length));
        this.config = config;
        this.trans = trans;
    }

    public void change(SideConfigType type) {
        for (int i = 0; i < SideConfigType.CHARS.length; i++)
            if (SideConfigType.CHARS[i] == config[type.ordinal()]) {
                config[type.ordinal()] = SideConfigType.CHARS[i ^ 0x0001];
                return;
            }
    }

    public boolean available(SideConfigType type) {
        return Character.isUpperCase(type.DEF);
    }

    public boolean allows(SideConfigType type) {
        return (type.OUTPUT ? storage.supportsExtraction() : storage.supportsInsertion()) && Character.toUpperCase(config[type.ordinal()]) == 'T';
    }
}
