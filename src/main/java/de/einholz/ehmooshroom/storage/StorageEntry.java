package de.einholz.ehmooshroom.storage;

import de.einholz.ehmooshroom.MooshroomLib;
import de.einholz.ehmooshroom.storage.transferable.Transferable;
import de.einholz.ehmooshroom.util.NbtSerializable;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

public class StorageEntry<T, U extends TransferVariant<T>> implements NbtSerializable {
    public final Storage<U> storage;
    public final char[] config;
    public final Transferable<T, U> trans;
    private final BlockEntity dirtyMarker;

    public StorageEntry(Storage<U> storage, char[] config, Transferable<T, U> trans, BlockEntity dirtyMarker) {
        this.storage = storage;
        if (config.length != SideConfigType.values().length) MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config char array should have a lenght of " + SideConfigType.values().length));
        // TODO add handling for when config is to short or to long
        this.config = config;
        this.trans = trans;
        this.dirtyMarker = dirtyMarker;
    }

    public void change(SideConfigType type) {
        for (int i = 0; i < SideConfigType.CHARS.length; i++) {
            if (SideConfigType.CHARS[i] != config[type.ordinal()]) continue;
            config[type.ordinal()] = SideConfigType.CHARS[i ^ 0x0001];
            return;
        }
        dirtyMarker.markDirty();
    }

    public boolean available(SideConfigType type) {
        return Character.isUpperCase(type.DEF);
    }

    public boolean allows(SideConfigType type) {
        return (type.OUTPUT ? storage.supportsExtraction() : storage.supportsInsertion()) && Character.toUpperCase(config[type.ordinal()]) == 'T';
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if (storage instanceof NbtSerializable seri) {
            NbtCompound storageNbt = seri.writeNbt(new NbtCompound());
            if (!storageNbt.isEmpty()) nbt.put("Storage", storageNbt);
        }
        nbt.putString("Config", String.valueOf(config));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (storage instanceof NbtSerializable seri && nbt.contains("Storage", NbtType.COMPOUND))
            seri.readNbt(nbt.getCompound("Storage"));
        if (nbt.contains("Config", NbtType.STRING)) {
            String str = nbt.getString("Config");
            if (str.length() < config.length) {
                MooshroomLib.LOGGER.smallBug(new ArrayIndexOutOfBoundsException("Config string for " + trans.getId() + " has a lenght of " + str.length() + " but should have " + config.length));
                return;
            }
            for (int i = 0; i < config.length; i++) config[i] = str.charAt(i);
        }
    }
}
