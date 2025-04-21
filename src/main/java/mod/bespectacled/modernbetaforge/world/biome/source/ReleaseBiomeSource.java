package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverRiver;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.layer.ModernBetaGenLayer;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Biomes;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ReleaseBiomeSource extends BiomeSource implements BiomeResolverOcean, BiomeResolverBeach, BiomeResolverRiver {
    private final ChunkCache<BiomeChunk> biomeCache;
    private final ChunkCache<BiomeChunk> oceanCache;
    
    private final GenLayer biomeLayer;
    private final GenLayer oceanLayer;

    public ReleaseBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.biomeCache = new ChunkCache<>("biome", (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, this::getBiomes));
        this.oceanCache = new ChunkCache<>("ocean", (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, this::getOceanBiomes));
        
        GenLayer[] biomeLayers = ModernBetaGenLayer.initBiomeLayers(seed, ModernBetaWorldType.INSTANCE, settings);
        biomeLayers = BiomeUtil.getModdedBiomeGenerators(WorldType.CUSTOMIZED, seed, biomeLayers);
        
        GenLayer[] oceanLayers = ModernBetaGenLayer.initOceanLayers(seed, ModernBetaWorldType.INSTANCE, settings);
        oceanLayers = BiomeUtil.getModdedBiomeGenerators(WorldType.CUSTOMIZED, seed, oceanLayers);
        
        this.biomeLayer = biomeLayers[1];
        this.oceanLayer = oceanLayers[1];
    }

    @Override
    public Biome getBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    @Override
    public Biome getOceanBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        Biome baseBiome = this.biomeCache.get(chunkX, chunkZ).sample(x, z);
        Biome oceanBiome = this.oceanCache.get(chunkX, chunkZ).sample(x, z);
        
        return isSnowy(baseBiome) ? Biomes.FROZEN_OCEAN : oceanBiome;
    }
    
    @Override
    public Biome getDeepOceanBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.FROZEN_OCEAN : Biomes.DEEP_OCEAN;
    }
    
    @Override
    public Biome getBeachBiome(int x, int z) {
        Biome biome = this.getBiome(x, z);
        
        return isSnowy(biome) ?
            Biomes.COLD_BEACH :
            BiomeDictionary.hasType(biome, Type.MOUNTAIN) ? Biomes.STONE_BEACH : Biomes.BEACH;
    }
    
    @Override
    public Biome getRiverBiome(int x, int z) {
        return isSnowy(this.getBiome(x, z)) ? Biomes.FROZEN_RIVER : Biomes.RIVER;
    }

    private Biome[] getBiomes(int x, int z) {
        IntCache.resetIntCache();
        int[] ints = this.biomeLayer.getInts(x, z, 16, 16);
        
        Biome[] biomes = new Biome[256];
        for (int i = 0; i < 256; ++i) {
            biomes[i] = Biome.getBiome(ints[i], Biomes.DEFAULT);
        }
        
        return biomes;
    }
    
    private Biome[] getOceanBiomes(int x, int z) {
        IntCache.resetIntCache();
        int[] ints = this.oceanLayer.getInts(x, z, 16, 16);
        
        Biome[] biomes = new Biome[256];
        for (int i = 0; i < 256; ++i) {
            biomes[i] = Biome.getBiome(ints[i], Biomes.DEFAULT);
        }
        
        return biomes;
    }
    
    private static boolean isSnowy(Biome biome) {
        return BiomeDictionary.hasType(biome, Type.SNOWY);
    }
}
