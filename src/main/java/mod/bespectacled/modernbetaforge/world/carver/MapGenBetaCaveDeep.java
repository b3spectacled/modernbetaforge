package mod.bespectacled.modernbetaforge.world.carver;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class MapGenBetaCaveDeep extends MapGenBeta18Cave {
    public MapGenBetaCaveDeep(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource.getDefaultBlock(),
            chunkSource.getDefaultFluid(),
            BlockStates.AIR,
            settings.deepCaveWidth,
            8,
            settings.deepCaveCount,
            settings.deepCaveChance,
            settings.floor,
            settings.floor
        );
    }
}
