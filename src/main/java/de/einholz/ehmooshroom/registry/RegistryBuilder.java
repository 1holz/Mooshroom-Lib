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

import java.util.function.Function;

import de.einholz.ehmooshroom.MooshroomLib;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class RegistryBuilder<T> {
    private Identifier id;
    private T type;

    protected abstract Registry<T> getRegistry();

    public RegistryBuilder<T> register(Identifier id, T type) {
        this.id = id;
        this.type = Registry.register(getRegistry(), getId(), type);
        return this;
    }

    public RegistryBuilder<T> register(String name, T type) {
        return register(idFactory().apply(name), type);
    }

    protected RegistryBuilder() {
    }

    protected Function<String, Identifier> idFactory() {
        return MooshroomLib.HELPER::makeId;
    }

    protected Identifier getId() {
        return id;
    }

    public T get() {
        return type;
    }
}
