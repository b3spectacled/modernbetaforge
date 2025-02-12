package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.source.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SingleBiomeSource extends BiomeSource implements NoiseBiomeSource {
    private final Biome biome;

    public SingleBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.biome = ForgeRegistryUtil.getRegistryEntry(new ResourceLocation(settings.singleBiome), ForgeRegistries.BIOMES);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }
}