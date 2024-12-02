package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public class SingleBiomeSource extends BiomeSource {
    private final Biome biome;

    public SingleBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBetaChunkGeneratorSettings settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaChunkGeneratorSettings.build(worldInfo.getGeneratorOptions()) :
            ModernBetaChunkGeneratorSettings.build();
        
        this.biome = BiomeUtil.getBiome(settings.singleBiome, "singleBiome");
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }
}