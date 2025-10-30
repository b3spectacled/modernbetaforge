package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateMap;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateType;
import mod.bespectacled.modernbetaforge.world.biome.climate.pe.PEClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.climate.pe.PESkyClimateSampler;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.biome.Biome;

public class PEBiomeSource extends BiomeSource implements ClimateSampler, SkyClimateSampler, BiomeResolverOcean, BiomeResolverBeach {
    private final BetaClimateMap climateMap;
    private final PEClimateSampler climateSampler;
    private final PESkyClimateSampler skyClimateSampler;
    private final boolean useClimateFeatures;
    
    public PEBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.climateMap = new BetaClimateMap(settings);
        this.climateSampler = new PEClimateSampler(seed, settings);
        this.skyClimateSampler = new PESkyClimateSampler(seed, settings);
        this.useClimateFeatures = settings.useClimateFeatures;
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.getBiomeByType(x, z, BetaClimateType.BASE);
    }

    @Override
    public Biome getOceanBiome(int x, int z) {
        return this.getBiomeByType(x, z, BetaClimateType.OCEAN);
    }

    @Override
    public Biome getBeachBiome(int x, int z) {
        return this.getBiomeByType(x, z, BetaClimateType.BEACH);
    }

    @Override
    public double sampleSkyTemp(int x, int z) {
        return this.skyClimateSampler.sampleSkyTemp(x, z);
    }

    @Override
    public Clime sample(int x, int z) {
        return this.climateSampler.sampleClime(x, z);
    }
    
    @Override
    public boolean sampleSkyColor() {
        return ModernBetaConfig.visualOptions.useBetaSkyColors && !this.climateMap.containsNonBetaBiomes();
    }
    
    @Override
    public boolean sampleBiomeColor() {
        return ModernBetaConfig.visualOptions.useBetaBiomeColors && !this.climateMap.containsNonBetaBiomes();
    }
    
    @Override
    public boolean sampleForFeatureGeneration() {
        return this.useClimateFeatures && !this.climateMap.containsNonBetaBiomes();
    }
    
    private Biome getBiomeByType(int x, int z, BetaClimateType type) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();

        return this.climateMap.getMapping(temp, rain).biomeByClimateType(type);
    }
}
