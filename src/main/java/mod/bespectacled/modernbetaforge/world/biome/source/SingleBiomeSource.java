package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class SingleBiomeSource extends BiomeSource implements NoiseBiomeSource {
    private final Biome biome;

    public SingleBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.biome = BiomeUtil.getBiome(new ResourceLocation(settings.singleBiome), "singleBiome");
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }
}