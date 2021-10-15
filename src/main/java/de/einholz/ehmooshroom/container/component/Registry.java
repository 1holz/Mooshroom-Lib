package de.einholz.ehmooshroom.container.component;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import dev.onyxstudios.cca.api.v3.util.GenericComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.util.GenericComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

public class Registry implements BlockComponentInitializer, ChunkComponentInitializer, EntityComponentInitializer, GenericComponentInitializer, ItemComponentInitializer, LevelComponentInitializer, ScoreboardComponentInitializer, WorldComponentInitializer {
    //XXX:
    //implement me in the registry system of this mod?
    //probably not see java doc for registerBlockComponentFactories

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry reg) {}

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry reg) {}

    @Override
    public void registerLevelComponentFactories(LevelComponentFactoryRegistry reg) {}

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry reg) {}

    @Override
    public void registerGenericComponentFactories(GenericComponentFactoryRegistry reg) {}

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry reg) {}

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry reg) {}

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry reg) {
        //TODO: also see BlockApiLookup and TechMod init
    }
}
