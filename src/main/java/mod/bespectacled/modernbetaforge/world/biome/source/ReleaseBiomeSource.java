package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverRiver;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.biome.layer.ModernBetaGenLayer;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.init.Biomes;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public class ReleaseBiomeSource extends BiomeSource implements BiomeResolverOcean, BiomeResolverBeach, BiomeResolverRiver {
    private final ChunkCache<BiomeChunk> biomeCache;
    private final GenLayer biomeLayer;

    public ReleaseBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        this.biomeCache = new ChunkCache<>(
            "biome",
            512,
            true,
            (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, this::getBiomes)
        );
        
        String generatorOptions = worldInfo.getGeneratorOptions();
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.buildSettings(generatorOptions);
        
        GenLayer[] genLayers = ModernBetaGenLayer.initLayers(worldInfo.getSeed(), worldInfo.getTerrainType(), settings);
        genLayers = getModdedBiomeGenerators(worldInfo.getTerrainType(), worldInfo.getSeed(), genLayers);
        
        this.biomeLayer = genLayers[1];
    }

    @Override
    public Biome getBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    @Override
    public Biome getOceanBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.FROZEN_OCEAN : Biomes.OCEAN;
    }
    
    @Override
    public Biome getDeepOceanBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.FROZEN_OCEAN : Biomes.DEEP_OCEAN;
    }
    
    @Override
    public Biome getBeachBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.COLD_BEACH : Biomes.BEACH;
    }
    
    @Override
    public Biome getRiverBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.FROZEN_RIVER : Biomes.RIVER;
    }

    private Biome[] getBiomes(int x, int z) {
        IntCache.resetIntCache();
        Biome[] biomes = new Biome[256];
        
        int[] ints = this.biomeLayer.getInts(x, z, 16, 16);

        int i = 0;
        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                biomes[(localZ & 0xF) + (localX & 0xF) * 16] = Biome.getBiome(ints[i++], Biomes.DEFAULT);
            }
        }

        return biomes;
    }
    
    private static boolean isSnowy(Biome biome) {
        return BiomeDictionary.hasType(biome, Type.SNOWY);
    }
    
    private static GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
        WorldTypeEvent.InitBiomeGens event = new WorldTypeEvent.InitBiomeGens(worldType, seed, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        
        return event.getNewBiomeGens();
    }
}
