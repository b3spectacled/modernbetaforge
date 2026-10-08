package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;

public class BiomesOPlentyCoralReefResolver extends BiomesOPlentyOceanResolver {
    public BiomesOPlentyCoralReefResolver(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(
            chunkSource,
            settings,
            BOPBiomes.coral_reef.get(),
            2027L,
            26183L,
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_CORAL_REEF_CHANCE),
            settings.getFloatProperty(CompatBiomesOPlenty.KEY_CORAL_REEF_NOISE_SCALE)
        );
    }
}
