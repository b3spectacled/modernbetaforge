package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.function.Predicate;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverRiver;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.spawn.BetaSpawnLocator;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
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
    
    private final BiomeSource noiseBiomeSource;

    public ReleaseChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings settings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, settings, noiseSettings, seed, mapFeaturesEnabled);
        
        this.minLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.maxLimitOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.mainOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);
        this.beachOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.surfaceOctaveNoise = new PerlinOctaveNoise(this.random, 4, true);
        this.depthOctaveNoise = new PerlinOctaveNoise(this.random, 16, true);
        this.forestOctaveNoise = new PerlinOctaveNoise(this.random, 8, true);

        BiomeSource biomeSource = this.biomeProvider.getBiomeSource();
        this.noiseBiomeSource = biomeSource instanceof ReleaseNoiseBiomeSource || biomeSource instanceof SingleBiomeSource ?
            biomeSource : new ReleaseNoiseBiomeSource(world.getWorldInfo());

        this.setBeachOctaveNoise(this.beachOctaveNoise);
        this.setSurfaceOctaveNoise(this.surfaceOctaveNoise);
        this.setForestOctaveNoise(this.forestOctaveNoise);
    }

    @Override
    public SpawnLocator getSpawnLocator() {
        return new BetaSpawnLocator();
    }

    public Biome getNoiseBiome(int x, int z) {
        return this.noiseBiomeSource.getBiome(x, z);
    }

    @Override
    protected BiomeInjectionRules buildBiomeInjectorRules() {
        boolean replaceOceans = this.getChunkGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getChunkGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
    
        Predicate<BiomeInjectionContext> riverPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.topPos.getX(), context.topPos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.RIVER);
        };
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.topPos.getX(), context.topPos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.OCEAN) && noiseBiome.equals(Biomes.DEEP_OCEAN);
        };
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> {
            Biome noiseBiome = this.getNoiseBiome(context.topPos.getX(), context.topPos.getZ());
            
            return BiomeDictionary.hasType(noiseBiome, Type.OCEAN);
        };
        
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.topPos.getY()) && this.isBeachBlock(context.topState);
            
        if (replaceBeaches && this.biomeProvider.getBiomeSource() instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)this.biomeProvider.getBiomeSource();
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, "beach");
        }
        
        if (replaceOceans && this.biomeProvider.getBiomeSource() instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)this.biomeProvider.getBiomeSource();
    
            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, "deep_ocean");
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, "ocean");
        }
        
        if (this.biomeProvider.getBiomeSource() instanceof BiomeResolverRiver) {
            BiomeResolverRiver biomeResolverRiver = (BiomeResolverRiver)this.biomeProvider.getBiomeSource();
            
            builder.add(riverPredicate, biomeResolverRiver::getRiverBiome, "river");
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

        Biome biome = this.sampleBiome(x, z);
        
        for (int localBiomeX = -2; localBiomeX <= 2; ++localBiomeX) {
            for (int localBiomeZ = -2; localBiomeZ <= 2; ++localBiomeZ) {
                int bX = (noiseX + localBiomeX) << 2;
                int bZ = (noiseZ + localBiomeZ) << 2;
                
                Biome curBiome = this.sampleBiome(bX, bZ);
                
                float curBiomeDepth = biomeDepthOffset + curBiome.getBaseHeight() * biomeDepthWeight;
                float curBiomeScale = biomeScaleOffset + curBiome.getHeightVariation() * biomeScaleWeight;

                float biomeWeight = BIOME_WEIGHTS[localBiomeX + 2 + (localBiomeZ + 2) * 5] / (curBiomeDepth + 2.0f);

                if (curBiome.getBaseHeight() > biome.getBaseHeight()) {
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

            density -= densityOffset;
            density = this.applySlides(density, noiseY);
            
            buffer[noiseY] = density;
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
            biome = this.biomeProvider.getBiomeSource().getBiome(x, z);
        }
        
        return biome;
    }
    
    static {
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                float f = 10.0f / MathHelper.sqrt((float)(i * i + j * j) + 0.2f);
                BIOME_WEIGHTS[i + 2 + (j + 2) * 5] = f;
            }
        }
    }

    private static class ReleaseNoiseBiomeSource extends BiomeSource {
        private final BiomeProvider biomeProvider;
    
        public ReleaseNoiseBiomeSource(WorldInfo worldInfo) {
            super(worldInfo);
    
            // Create new world info with Customized world type,
            // so biome provider will accept custom biome sizes
            ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
            ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.buildSettings(worldInfo.getGeneratorOptions());
            
            factory.biomeSize = settings.biomeSize;
            factory.riverSize = settings.riverSize;
            
            WorldInfo vanillaWorldInfo = new WorldInfo(worldInfo);
            vanillaWorldInfo.populateFromWorldSettings(new WorldSettings(worldInfo).setGeneratorOptions(factory.toString()));
            vanillaWorldInfo.setTerrainType(WorldType.CUSTOMIZED);
            
            this.biomeProvider = new BiomeProvider(vanillaWorldInfo);
        }
    
        @Override
        public Biome getBiome(int x, int z) {
            return this.biomeProvider.getBiome(new BlockPos(x, 0 , z));
        }
    }
}
