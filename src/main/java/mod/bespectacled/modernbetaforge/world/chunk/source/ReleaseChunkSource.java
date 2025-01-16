package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.function.Predicate;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverRiver;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.source.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.GenLayerUtil;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.biome.layer.ModernBetaGenLayer;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ReleaseChunkSource extends NoiseChunkSource {
    private static final float[] BIOME_WEIGHTS = new float[25];
    
    private final PerlinOctaveNoise minLimitOctaveNoise;
    private final PerlinOctaveNoise maxLimitOctaveNoise;
    private final PerlinOctaveNoise mainOctaveNoise;
    private final PerlinOctaveNoise beachOctaveNoise;
    private final PerlinOctaveNoise surfaceOctaveNoise;
    private final PerlinOctaveNoise depthOctaveNoise;
    private final PerlinOctaveNoise forestOctaveNoise;
    
    private final BiomeSource biomeSource;
    private final NoiseBiomeSource noiseBiomeSource;

    public ReleaseChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);

        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE
            .get(new ResourceLocation(settings.biomeSource))
            .apply(seed, settings);
        this.noiseBiomeSource = biomeSource instanceof NoiseBiomeSource ?
            (NoiseBiomeSource)biomeSource : new ReleaseNoiseBiomeSource(seed, settings);

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
        
        this.setCloudHeight(128);
    }

    public Biome getNoiseBiome(int x, int z) {
        return this.noiseBiomeSource.getBiome(x, z);
    }

    @Override
    public BiomeInjectionRules buildBiomeInjectorRules(BiomeSource biomeSource) {
        boolean replaceOceans = this.getGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
    
        Predicate<BiomeInjectionContext> riverPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.pos.getX(), context.pos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.RIVER);
        };
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.pos.getX(), context.pos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.OCEAN) && noiseBiome.equals(Biomes.DEEP_OCEAN);
        };
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.pos.getX(), context.pos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.OCEAN);
        };
        
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.pos.getY()) && this.isBeachBlock(context.state);
            
        if (replaceBeaches && biomeSource instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)biomeSource;
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionStep.POST_SURFACE);
        }
        
        if (replaceOceans && biomeSource instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)biomeSource;
    
            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionStep.PRE_SURFACE);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        if (biomeSource instanceof BiomeResolverRiver) {
            BiomeResolverRiver biomeResolverRiver = (BiomeResolverRiver)biomeSource;
            
            builder.add(riverPredicate, biomeResolverRiver::getRiverBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        return builder.build();
    }

    @Override
    protected void sampleNoiseColumn(
        double[] buffer,
        int startNoiseX,
        int startNoiseZ,
        int localNoiseX,
        int localNoiseZ
    ) {
        int noiseX = startNoiseX + localNoiseX;
        int noiseZ = startNoiseZ + localNoiseZ;
        
        int x = noiseX << 2;
        int z = noiseZ << 2;
        
        double depthNoiseScaleX = this.settings.depthNoiseScaleX; // Default: 200
        double depthNoiseScaleZ = this.settings.depthNoiseScaleZ;
        
        double coordinateScale = this.settings.coordinateScale;
        double heightScale = this.settings.heightScale;
        
        double mainNoiseScaleX = this.settings.mainNoiseScaleX; // Default: 80
        double mainNoiseScaleY = this.settings.mainNoiseScaleY; // Default: 160
        double mainNoiseScaleZ = this.settings.mainNoiseScaleZ;

        double lowerLimitScale = this.settings.lowerLimitScale;
        double upperLimitScale = this.settings.upperLimitScale;
        
        double baseSize = this.settings.baseSize;
        double heightStretch = this.settings.stretchY;
        
        double depth = this.depthOctaveNoise.sampleXZ(noiseX, noiseZ, depthNoiseScaleX, depthNoiseScaleZ);
        
        float biomeDepthOffset = this.settings.biomeDepthOffset;
        float biomeScaleOffset = this.settings.biomeScaleOffset;

        float biomeDepthWeight = this.settings.biomeDepthWeight;
        float biomeScaleWeight = this.settings.biomeScaleWeight;
        
        double biomeScale = 0.0;
        double biomeDepth = 0.0;
        double totalBiomeWeight = 0.0;

        float baseHeight = this.getBaseHeight(x, z);
        
        for (int localBiomeX = -2; localBiomeX <= 2; ++localBiomeX) {
            for (int localBiomeZ = -2; localBiomeZ <= 2; ++localBiomeZ) {
                int bX = (noiseX + localBiomeX) << 2;
                int bZ = (noiseZ + localBiomeZ) << 2;
                
                float curBaseHeight = this.getBaseHeight(bX, bZ);
                float curHeightVariation = this.getHeightVariation(bX, bZ);
                
                float curBiomeDepth = biomeDepthOffset + curBaseHeight * biomeDepthWeight;
                float curBiomeScale = biomeScaleOffset + curHeightVariation * biomeScaleWeight;

                float biomeWeight = BIOME_WEIGHTS[localBiomeX + 2 + (localBiomeZ + 2) * 5] / (curBiomeDepth + 2.0f);

                if (curBaseHeight > baseHeight) {
                    biomeWeight /= 2.0f;
                }

                biomeScale += curBiomeScale * biomeWeight;
                biomeDepth += curBiomeDepth * biomeWeight;
                totalBiomeWeight += biomeWeight;
            }
        }

        biomeScale = biomeScale / totalBiomeWeight;
        biomeDepth = biomeDepth / totalBiomeWeight;
        biomeScale = biomeScale * 0.9f + 0.1f;
        biomeDepth = (biomeDepth * 4.0f - 1.0f) / 8.0f;

        depth /= 8000.0;

        if (depth < 0.0) {
            depth = -depth * 0.3;
        }

        depth = depth * 3.0 - 2.0;

        if (depth < 0.0) {
            depth /= 2.0;

            if (depth < -1.0) {
                depth = -1.0;
            }

            depth /= 1.4;
            depth /= 2.0;

        } else {
            if (depth > 1.0) {
                depth = 1.0;
            }
            depth /= 8.0;
        }

        biomeDepth = biomeDepth + depth * 0.2;
        biomeDepth = biomeDepth * baseSize / 8.0;
        biomeDepth = baseSize + biomeDepth * 4.0;
        
        for (int noiseY = 0; noiseY < buffer.length; ++noiseY) {
            double density;
            double densityOffset = this.getOffset(noiseY, heightStretch, biomeDepth, biomeScale);
                 
            double mainNoise = (this.mainOctaveNoise.sample(
                noiseX, noiseY, noiseZ,
                coordinateScale / mainNoiseScaleX, 
                heightScale / mainNoiseScaleY, 
                coordinateScale / mainNoiseScaleZ
            ) / 10.0 + 1.0) / 2.0;
            
            if (mainNoise < 0.0) {
                density = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
            } else if (mainNoise > 1.0) {
                density = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
            } else {
                double minLimitNoise = this.minLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / lowerLimitScale;
                
                double maxLimitNoise = this.maxLimitOctaveNoise.sample(
                    noiseX, noiseY, noiseZ,
                    coordinateScale, 
                    heightScale, 
                    coordinateScale
                ) / upperLimitScale;
                
                density = minLimitNoise + (maxLimitNoise - minLimitNoise) * mainNoise;
            }
            
            buffer[noiseY] = density - densityOffset;
        }
    }
    
    private double getOffset(int noiseY, double heightStretch, double depth, double scale) {
        double offset = ((double)noiseY - depth) * heightStretch * 128.0 / 256.0 / scale;
        
        if (offset < 0.0)
            offset *= 4.0;
        
        return offset;
    }
    
    private Biome sampleBiome(int x, int z) {
        Biome biome = this.noiseBiomeSource.getBiome(x, z);
        
        if (this.settings.useBiomeDepthScale &&
            !BiomeDictionary.hasType(biome, Type.OCEAN) &&
            !BiomeDictionary.hasType(biome, Type.RIVER)
        ) {
            biome = this.biomeSource.getBiome(x, z);
        }
        
        return biome;
    }
    
    private float getBaseHeight(int x, int z) {
        return this.sampleBiome(x, z).getBaseHeight();
    }
    
    private float getHeightVariation(int x, int z) {
        return this.sampleBiome(x, z).getHeightVariation();
    }
    
    static {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
                BIOME_WEIGHTS[i + 2 + (j + 2) * 5] = f;
            }
        }
    }

    private static class ReleaseNoiseBiomeSource extends BiomeSource implements NoiseBiomeSource {
        private static final boolean DEBUG_VANILLA = false;
        
        private final GenLayer genLayer;
        private final ChunkCache<BiomeChunk> biomeCache;
    
        public ReleaseNoiseBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
            super(seed, settings);

            ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
            factory.biomeSize = settings.layerSize;
            factory.riverSize = settings.riverSize;

            GenLayer[] genLayers = ModernBetaGenLayer.initNoiseLayers(seed, WorldType.CUSTOMIZED, factory.build(), settings);
            genLayers = GenLayerUtil.getModdedBiomeGenerators(WorldType.CUSTOMIZED, seed, genLayers);
            
            this.genLayer = genLayers[1];
            this.biomeCache = new ChunkCache<>(
                "noise_biome",
                256,
                (chunkX, chunkZ) -> new BiomeChunk(this.getBiomes(chunkX << 4, chunkZ << 4))
            );
            
            if (DEBUG_VANILLA) {
                this.debugVanillaGenLayer();
            }
        }
    
        @Override
        public Biome getBiome(int x, int z) {
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            
            return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
        }
        
        private Biome[] getBiomes(int x, int z) {
            IntCache.resetIntCache();
            int[] ints = this.genLayer.getInts(x, z, 16, 16);
            
            Biome[] biomes = new Biome[256];
            for (int i = 0; i < 256; ++i) {
                biomes[i] = Biome.getBiome(ints[i], Biomes.DEFAULT);
            }
            
            return biomes;
        }

        private void debugVanillaGenLayer() {
            ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
            factory.biomeSize = this.settings.layerSize;
            factory.riverSize = this.settings.riverSize;
            
            WorldSettings worldSettings = new WorldSettings(this.seed, GameType.NOT_SET, false, false, WorldType.CUSTOMIZED);
            WorldInfo worldInfo = new WorldInfo(worldSettings.setGeneratorOptions(factory.toString()), "");
            BiomeProvider biomeProvider = new BiomeProvider(worldInfo);
            
            MutableBlockPos blockPos = new MutableBlockPos();
            for (int x = 0; x < 100000; ++x) {
                int chunkX = x >> 4;
            
                Biome biome0 = this.biomeCache.get(chunkX, 0).sample(x, 0);
                Biome biome1 = biomeProvider.getBiome(blockPos.setPos(x, 0, 0));
                
                if (biome0 != biome1) {
                    ModernBeta.log(Level.DEBUG, String.format("Biomes do not match at %d/%d!", x, 0));
                    break;
                }
            }
            
            ModernBeta.log(Level.DEBUG, String.format("Validated biome layers!"));
        }
    }
}