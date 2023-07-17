package de.einholz.ehmooshroom.registry.rebs;

import java.util.function.Function;

import de.einholz.ehmooshroom.registry.RegEntryBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

public interface ItemsREB<B extends BlockEntity, G extends ScreenHandler, S extends HandledScreen<G>, R extends Recipe<?>> {
    abstract Block getBlock();

    abstract RegEntryBuilder<B, G, S, R> withItemRaw(Function<RegEntryBuilder<B, G, S, R>, Item> itemFunc);

    abstract RegEntryBuilder<B, G, S, R> withFuelRaw(Function<RegEntryBuilder<B, G, S, R>, Integer> fuelTicks);

    abstract RegEntryBuilder<B, G, S, R> withItemGroupAddRaw(
            Function<RegEntryBuilder<B, G, S, R>, Identifier> itemGroupAddFunc);

    abstract RegEntryBuilder<B, G, S, R> withItemGroupCreateRaw(
            Function<RegEntryBuilder<B, G, S, R>, Boolean> itemGroupCreateFunc);

    default RegEntryBuilder<B, G, S, R> withItemNull() {
        return withItemRaw(entry -> null);
    }

    @FunctionalInterface
    public static interface ItemFactory<I extends Item> {
        I create(Settings settings);
    }

    default RegEntryBuilder<B, G, S, R> withItemBuild(ItemFactory<? extends Item> factory, Settings settings) {
        return withItemRaw(entry -> factory.create(settings));
    }

    default RegEntryBuilder<B, G, S, R> withBlockItemBuild(Settings settings) {
        return withItemRaw(entry -> new BlockItem(getBlock(), settings));
    }

    default RegEntryBuilder<B, G, S, R> withFuelBuild(int ticks) {
        return withFuelRaw(entry -> ticks);
    }

    default RegEntryBuilder<B, G, S, R> withFuelNull() {
        return withFuelRaw(entry -> null);
    }

    default RegEntryBuilder<B, G, S, R> withItemGroupAddBuild(Identifier id) {
        return withItemGroupAddRaw(entry -> id);
    }

    default RegEntryBuilder<B, G, S, R> withSelfItemGroupBuild(ItemFactory<? extends Item> factory, Settings settings) {
        return withItemBuild(factory, settings).withItemGroupCreateRaw(entry -> true)
                .withItemGroupAddRaw(entry -> entry.getId());
    }

    default RegEntryBuilder<B, G, S, R> withSelfBlockItemGroupBuild(Settings settings) {
        return withBlockItemBuild(settings).withItemGroupCreateRaw(entry -> true)
                .withItemGroupAddRaw(entry -> entry.getId());
    }
}
