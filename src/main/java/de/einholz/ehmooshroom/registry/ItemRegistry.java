/*
 * Copyright 2023 Einholz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.einholz.ehmooshroom.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry extends RegistryBuilder<Item> {
    private static final Map<Identifier, List<ItemStack>> ITEM_GROUPS_RAW = new HashMap<>();
    private static final Map<Identifier, ItemGroup> ITEM_GROUPS = new HashMap<>();

    public ItemRegistry register(String name, Settings settings) {
        Block block = Registry.BLOCK.get(idFactory().apply(name));
        return (ItemRegistry) register(name, block, settings);
    }

    public ItemRegistry register(String name, Block block) {
        return (ItemRegistry) register(name, new BlockItem(block, defaultSettings()));
    }

    public ItemRegistry register(String name, Block block, Settings settings) {
        return (ItemRegistry) register(name, new BlockItem(block, settings));
    }

    public ItemRegistry register(String name, ItemFactory<Item> factory) {
        return (ItemRegistry) register(name, factory.create(defaultSettings()));
    }

    public ItemRegistry register(String name, ItemFactory<Item> factory, Settings settings) {
        return (ItemRegistry) register(name, factory.create(settings));
    }

    protected ItemRegistry() {
    }

    public ItemRegistry withFuel(int ticks) {
        FuelRegistry.INSTANCE.add(get(), ticks);
        return this;
    }

    public ItemRegistry withItemGroupSelf() {
        return withItemGroupAdd(getId()).withItemGroupCreate();
    }

    public ItemRegistry withItemGroupCreate() {
        ItemGroup group = FabricItemGroupBuilder.create(getId())
                .icon(() -> get().getDefaultStack())
                .appendItems(list -> {
                    list.addAll(ITEM_GROUPS_RAW.getOrDefault(getId(), new ArrayList<>(0)));
                })
                .build();
        ITEM_GROUPS.put(getId(), group);
        return this;
    }

    public ItemRegistry withItemGroupAdd(Identifier id) {
        ITEM_GROUPS_RAW.putIfAbsent(id, new ArrayList<>());
        ITEM_GROUPS_RAW.get(id).add(get().getDefaultStack());
        return this;
    }

    @Override
    protected Registry<Item> getRegistry() {
        return Registry.ITEM;
    }

    @Nullable
    public static ItemGroup getItemGroup(Identifier id) {
        return ITEM_GROUPS.get(id);
    }

    protected Settings defaultSettings() {
        return new Settings();
    }

    @FunctionalInterface
    public static interface ItemFactory<T extends Item> {
        T create(Settings settings);
    }
}
