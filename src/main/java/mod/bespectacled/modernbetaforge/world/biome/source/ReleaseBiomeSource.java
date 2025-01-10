package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverRiver;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.layer.ModernBetaGenLayer;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ReleaseBiomeSource extends BiomeSource implements BiomeResolverOcean, BiomeResolverBeach, BiomeResolverRiver {
    private final ChunkCache<BiomeChunk> biomeCache;
    private final GenLayer biomeLayer;

    public ReleaseBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.biomeCache = new ChunkCache<>("biome", (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ, this::getBiomes));
        
        GenLayer[] genLayers = ModernBetaGenLayer.initBiomeLayers(seed, ModernBetaWorldType.INSTANCE, settings);
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
        int[] ints = this.biomeLayer.getInts(x, z, 16, 16);
        
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
