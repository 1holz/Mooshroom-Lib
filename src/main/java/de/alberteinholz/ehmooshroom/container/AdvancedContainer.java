/*
package de.alberteinholz.ehmooshroom.container;

public interface AdvancedContainer {
    public AdvancedContainerBlockEntity(RegistryEntry registryEntry) {
        comps.put(MooshroomLib.HELPER.makeId("name"), new NameDataComponent("name"));
        comps.put(MooshroomLib.HELPER.makeId("config"), new ConfigDataComponent());
        comps.forEach((id, comp) -> {
            if (comp instanceof AdvancedInventoryComponent) ((AdvancedInventoryComponent) comp).setConfig((ConfigDataComponent) comps.get(MooshroomLib.HELPER.makeId("config")));
        });
    }

    public void addComponent(Component comp) {

    }

    //you have to add all needed components first
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (world == null) return;
        for (String key : tag.getKeys()) {
            Identifier id = new Identifier(key);
            if (!tag.contains(key, NbtType.COMPOUND) || tag.getCompound(key).isEmpty()) continue;
            if (!comps.containsKey(id)) {
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no component with the id " + key + " in the AdvancedContainer" + getDisplayName().getString()));
                continue;
            }
            CompoundTag compTag = tag.getCompound(key);
            comps.get(id).fromTag(compTag);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (world == null) return tag;
        comps.forEach((id, comp) -> {
            CompoundTag compTag = new CompoundTag();
            comp.toTag(compTag);
            if (!compTag.isEmpty()) tag.put(id.toString(), compTag);
        });
        return tag;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(((NameDataComponent) comps.get(MooshroomLib.HELPER.makeId("name"))).containerName.getLabel().asString());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        writeScreenOpeningData((ServerPlayerEntity) player, buf);
        return registryEntry.clientHandlerFactory.create(syncId, playerInv, buf);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
*/