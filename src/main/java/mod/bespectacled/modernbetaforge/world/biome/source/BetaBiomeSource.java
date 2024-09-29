package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateMap;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateType;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaSkyClimateSampler;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public class BetaBiomeSource extends BiomeSource implements ClimateSampler, SkyClimateSampler, BiomeResolverOcean, BiomeResolverBeach {
    private final BetaClimateMap climateMap;
    private final BetaClimateSampler climateSampler;
    private final BetaSkyClimateSampler skyClimateSampler;
    
    public BetaBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBetaChunkGeneratorSettings settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(worldInfo.getGeneratorOptions()).build() :
            new ModernBetaChunkGeneratorSettings.Factory().build();
        
        this.climateMap = new BetaClimateMap(settings);
        this.climateSampler = new BetaClimateSampler(worldInfo.getSeed(), settings);
        this.skyClimateSampler = new BetaSkyClimateSampler(worldInfo.getSeed(), settings);
    }

    @Override
    public Biome getBiome(int x, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, BetaClimateType.LAND);
    }

    @Override
    public Biome getOceanBiome(int x, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, BetaClimateType.OCEAN);
    }

    @Override
    public Biome getBeachBiome(int x, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, BetaClimateType.BEACH);
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
        return ModernBetaConfig.visualOptions.useBetaSkyColors;
    }
    
    @Override
    public boolean sampleBiomeColor() {
        return ModernBetaConfig.visualOptions.useBetaBiomeColors;
    }
    
    public boolean isModifiedMap() {
        return this.climateMap.isModifiedMap();
    }
}
