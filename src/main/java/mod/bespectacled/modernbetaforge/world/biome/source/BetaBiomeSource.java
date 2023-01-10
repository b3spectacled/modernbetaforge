package mod.bespectacled.modernbetaforge.world.biome.source;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ClimateChunk;
import mod.bespectacled.modernbetaforge.util.chunk.SkyClimateChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.climate.BetaClimateMap;
import mod.bespectacled.modernbetaforge.world.biome.climate.ClimateMapping.ClimateType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;

public class BetaBiomeSource extends BiomeSource implements ClimateSampler, SkyClimateSampler, BiomeResolverOcean, BiomeResolverBeach {
    private final BetaClimateMap climateMap;
    private final BetaClimateSampler climateSampler;
    private final BetaSkyClimateSampler skyClimateSampler;
    
    public BetaBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        this.climateMap = new BetaClimateMap();
        this.climateSampler = new BetaClimateSampler(worldInfo.getSeed());
        this.skyClimateSampler = new BetaSkyClimateSampler(worldInfo.getSeed());
    }

    @Override
    public Biome getBiome(int x, int y, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, ClimateType.LAND);
    }

    @Override
    public Biome getOceanBiome(int x, int y, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, ClimateType.OCEAN);
    }

    @Override
    public Biome getBeachBiome(int x, int y, int z) {
        Clime clime = this.climateSampler.sampleClime(x, z);
        double temp = clime.temp();
        double rain = clime.rain();
        
        return this.climateMap.getBiome(temp, rain, ClimateType.BEACH);
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
    
    private static class BetaClimateSampler {
        private final SimplexOctaveNoise tempNoiseOctaves;
        private final SimplexOctaveNoise rainNoiseOctaves;
        private final SimplexOctaveNoise detailNoiseOctaves;
        
        private final ChunkCache<ClimateChunk> climateCache;
        
        private final double tempScale;
        private final double rainScale;
        private final double detailScale;
        
        public BetaClimateSampler(long seed) {
            this(seed, 1D);
        }
        
        public BetaClimateSampler(long seed, double climateScale) {
            this.tempNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
            this.rainNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 39811L), 4);
            this.detailNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 543321L), 2);
            
            this.climateCache = new ChunkCache<>(
                "climate", 
                1536, 
                true, 
                (chunkX, chunkZ) -> new ClimateChunk(chunkX, chunkZ, this::sampleClimateNoise)
            );
            
            this.tempScale = 0.02500000037252903D / climateScale;
            this.rainScale = 0.05000000074505806D / climateScale;
            this.detailScale = 0.25D / climateScale;
        }

        public Clime sampleClime(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.climateCache.get(chunkX, chunkZ).sampleClime(x, z);
        }
        
        public Clime sampleClimateNoise(int x, int z) {
            double temp = this.tempNoiseOctaves.sample(x, z, this.tempScale, this.tempScale, 0.25D);
            double rain = this.rainNoiseOctaves.sample(x, z, this.rainScale, this.rainScale, 0.33333333333333331D);
            double detail = this.detailNoiseOctaves.sample(x, z, this.detailScale, this.detailScale, 0.58823529411764708D);

            detail = detail * 1.1D + 0.5D;

            temp = (temp * 0.15D + 0.7D) * 0.99D + detail * 0.01D;
            rain = (rain * 0.15D + 0.5D) * 0.998D + detail * 0.002D;

            temp = 1.0D - (1.0D - temp) * (1.0D - temp);
            
            return new Clime(MathHelper.clamp(temp, 0.0, 1.0), MathHelper.clamp(rain, 0.0, 1.0));
        }
    }
    
    private static class BetaSkyClimateSampler {
        private final SimplexOctaveNoise tempNoiseOctaves;
        
        private final ChunkCache<SkyClimateChunk> skyClimateCache;
        
        private final double tempScale;
        
        public BetaSkyClimateSampler(long seed) {
            this(seed, 1D);
        }
        
        public BetaSkyClimateSampler(long seed, double climateScale) {
            this.tempNoiseOctaves = new SimplexOctaveNoise(new Random(seed * 9871L), 4);
            
            this.skyClimateCache = new ChunkCache<>(
                "sky", 
                256, 
                true, 
                (chunkX, chunkZ) -> new SkyClimateChunk(chunkX, chunkZ, this::sampleSkyTempNoise)
            );
            
            this.tempScale = 0.02500000037252903D / climateScale;
        }
        
        public double sampleSkyTemp(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.skyClimateCache.get(chunkX, chunkZ).sampleTemp(x, z);
        }
        
        private double sampleSkyTempNoise(int x, int z) {
            return this.tempNoiseOctaves.sample(x, z, this.tempScale, this.tempScale, 0.5D);
        }
    }
}
