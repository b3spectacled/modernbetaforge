package mod.bespectacled.modernbetaforge.api.world.biome;

import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.biome.climate.SimpleClimateSampler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class BiomeResolverAddSingleBiome implements BiomeResolverCustom {
    private final Biome biome;
    private final SimpleClimateSampler climateSampler;
    private final float chance;
    private final float noiseScale;
    
    /**
     * Constructs a biome resolver which provides a single biome if it meets a certain climate threshold.
     * 
     * @param biome The {@code ResourceLocation} of the biome to return if climate threshold is met.
     * @param seed The world seed.
     * @param climateSeed The climate seed multiplier. Ideally should be an odd prime number.
     * @param detailSeed The detail seed multiplier. Ideally should be an odd prime number.
     * @param chance The threshold for the climate noise to meet to return the biome. Between 0.0 and 1.0 inclusive.
     * @param noiseScale The noise scale for x/z coordinates when sampling.
     */
    public BiomeResolverAddSingleBiome(ResourceLocation biome, long seed, long climateSeed, long detailSeed, float chance, float noiseScale) {
        this(ForgeRegistryUtil.get(biome, ForgeRegistries.BIOMES), seed, climateSeed, detailSeed, chance, noiseScale);
    }

    /**
     * Constructs a biome resolver which provides a single biome if it meets a certain climate threshold.
     * 
     * @param biome The biome to return if climate threshold is met.
     * @param seed The world seed.
     * @param climateSeed The climate seed multiplier. Ideally should be an odd prime number.
     * @param detailSeed The detail seed multiplier. Ideally should be an odd prime number.
     * @param chance The threshold for the climate noise to meet to return the biome. Between 0.0 and 1.0 inclusive.
     * @param noiseScale The noise scale for x/z coordinates when sampling.
     */
    public BiomeResolverAddSingleBiome(Biome biome, long seed, long climateSeed, long detailSeed, float chance, float noiseScale) {
        this.biome = biome;
        this.climateSampler = new SimpleClimateSampler(seed, climateSeed, detailSeed);
        this.chance = MathHelper.clamp(chance, 0.0f, 1.0f);
        this.noiseScale = noiseScale;
    }

    @Override
    public Biome getCustomBiome(int x, int z) {
        double scaledX = x * this.noiseScale;
        double scaledZ = z * this.noiseScale;
        
        if (this.chance <= 0.0) {
            return null;
        }
        
        if (this.climateSampler.sample(scaledX, scaledZ) <= this.chance) {
            return this.biome;
        }
        
        return null;
    }
}
