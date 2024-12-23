package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateMap;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateType;
import mod.bespectacled.modernbetaforge.world.biome.climate.pe.PEClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.climate.pe.PESkyClimateSampler;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public class PEBiomeSource extends BiomeSource implements ClimateSampler, SkyClimateSampler, BiomeResolverOcean, BiomeResolverBeach {
    private final ClimateMap climateMap;
    private final PEClimateSampler climateSampler;
    private final PESkyClimateSampler skyClimateSampler;
    
    public PEBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBetaChunkGeneratorSettings settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaChunkGeneratorSettings.build(worldInfo.getGeneratorOptions()) :
            ModernBetaChunkGeneratorSettings.build();
        
        this.climateMap = new ClimateMap(settings);
        this.climateSampler = new PEClimateSampler(worldInfo.getSeed(), settings);
        this.skyClimateSampler = new PESkyClimateSampler(worldInfo.getSeed(), settings);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.getBiomeByType(x, z, ClimateType.BASE);
    }

    @Override
    public Biome getOceanBiome(int x, int z) {
        return this.getBiomeByType(x, z, ClimateType.OCEAN);
    }

    @Override
    public Biome getBeachBiome(int x, int z) {
        return this.getBiomeByType(x, z, ClimateType.BEACH);
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
        return ModernBetaConfig.visualOptions.useBetaSkyColors && !this.climateMap.isModifiedMap();
    }
    
    @Override
    public boolean sampleBiomeColor() {
        return ModernBetaConfig.visualOptions.useBetaBiomeColors && !this.climateMap.isModifiedMap();
    }
    
    @Override
    public boolean sampleForFeatureGeneration() {
        return !this.climateMap.isModifiedMap();
    }
    
    private Biome getBiomeByType(int x, int z, ClimateType type) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();

        return this.climateMap.getMapping(temp, rain).biomeByClimateType(type);
    }
}
