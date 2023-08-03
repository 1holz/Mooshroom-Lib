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

 package de.einholz.ehmooshroom.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import de.einholz.ehmooshroom.storage.Transferable;
import de.einholz.ehmooshroom.storage.variants.BlockVariant;
import de.einholz.ehmooshroom.storage.variants.ElectricityVariant;
import de.einholz.ehmooshroom.storage.variants.EntityVariant;
import de.einholz.ehmooshroom.storage.variants.HeatVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

// XXX: anotate constructor with only server?
public class Exgredient<T, V extends TransferVariant<T>> implements Gredient<T> {
    public static final Map<Identifier, Factories<?, ?>> FACTORIES = new HashMap<>();
    private final Identifier typeId;
    private final boolean isSingleton;
    @Nullable
    private final Identifier id;
    private final NbtCompound nbt;
    private final long amount;
    private final T output;
    private final V outputVariant;

    @SuppressWarnings("unchecked")
    public Exgredient(Identifier typeId, @Nullable Identifier id, @Nullable NbtCompound nbt, long amount) {
        this.typeId = typeId;
        this.isSingleton = Registry.REGISTRIES.containsId(typeId);
        if (isSingleton) {
            this.id = null;
            this.nbt = new NbtCompound();
        } else {
            this.id = id;
            this.nbt = nbt == null ? new NbtCompound() : nbt;
        }
        this.amount = amount;
        output = (T) FACTORIES.get(this.typeId).outputFactory().build(id, amount, nbt);
        outputVariant = ((Function<T, V>) FACTORIES.get(this.typeId).variantFactory()).apply(output);
    }

    public Exgredient(RegistryKey<? extends Registry<T>> typeReg, @Nullable Identifier id, @Nullable NbtCompound nbt,
            long amount) {
        this(typeReg.getValue(), id, nbt, amount);
    }

    public T getOutput() {
        return output;
    }

    public V getOutputVariant() {
        return outputVariant;
    }

    @Override
    public boolean contains(T obj) {
        return getOutputVariant().isOf(obj);
    }

    @Override
    public Identifier getTypeId() {
        return typeId;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Nullable
    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public NbtCompound getNbt() {
        return nbt;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @FunctionalInterface
    public static interface OutputFactory<T> {
        public T build(@Nullable Identifier id, long amount, @Nullable NbtCompound nbt);
    }

    public static record Factories<T, V>(OutputFactory<T> outputFactory, Function<T, V> variantFactory) {
    }

    static {
        FACTORIES.putIfAbsent(Registry.ITEM_KEY.getValue(), new Factories<>((id, amount, nbt) -> {
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), (int) amount);
            stack.setNbt(nbt);
            return stack;
        }, ItemVariant::of));
        FACTORIES.putIfAbsent(Registry.FLUID_KEY.getValue(), new Factories<>((id, amount, nbt) -> {
            FluidVariant fluid = FluidVariant.of(Registry.FLUID.get(id), nbt);
            return fluid;
        }, fluid -> fluid));
        FACTORIES.putIfAbsent(Registry.BLOCK_KEY.getValue(), new Factories<>((id, amount, nbt) -> {
            return Registry.BLOCK.get(id).getDefaultState();
        }, state -> new BlockVariant(state.getBlock())));
        FACTORIES.putIfAbsent(Registry.ENTITY_TYPE_KEY.getValue(), new Factories<>((id, amount, nbt) -> {
            // TODO getWorld or return EntityType
            Entity entity = Registry.ENTITY_TYPE.get(id).create(null);
            if (nbt != null)
                entity.readNbt(nbt);
            return entity;
        }, entity -> new EntityVariant(entity.getType(), entity.writeNbt(new NbtCompound()))));
        FACTORIES.putIfAbsent(Transferable.ELECTRICITY_ID, new Factories<>((id, amount, nbt) -> {
            return ElectricityVariant.INSTANCE;
        }, electricity -> electricity));
        FACTORIES.putIfAbsent(Transferable.HEAT_ID, new Factories<>((id, amount, nbt) -> {
            return HeatVariant.INSTANCE;
        }, heat -> heat));
    }
}
