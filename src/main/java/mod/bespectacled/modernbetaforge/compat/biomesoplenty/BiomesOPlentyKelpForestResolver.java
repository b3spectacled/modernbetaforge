package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class BiomesOPlentyKelpForestResolver extends BiomesOPlentyOceanResolver {
    public BiomesOPlentyKelpForestResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BOPBiomes.kelp_forest.get(),
            3469L,
            55579L,
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_KELP_FOREST_CHANCE),
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_KELP_FOREST_NOISE_SCALE)
        );
    }
}
