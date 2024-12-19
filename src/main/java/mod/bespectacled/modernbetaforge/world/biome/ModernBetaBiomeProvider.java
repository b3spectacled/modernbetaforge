package mod.bespectacled.modernbetaforge.world.biome;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaBiomeProvider extends BiomeProvider {
    private final ModernBetaGeneratorSettings settings;
    private final BiomeSource biomeSource;
    private final ChunkCache<BiomeChunk> biomeCache;
    
    private ChunkSource chunkSource;
    
    public ModernBetaBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBeta.log(Level.INFO, "TERRAIN TYPE: " + worldInfo.getTerrainType().getName());

        this.settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaGeneratorSettings.build(worldInfo.getGeneratorOptions()) :
            ModernBetaGeneratorSettings.build();

        this.biomeSource = ModernBetaRegistries.BIOME
            .get(new ResourceLocation(this.settings.biomeSource))
            .apply(worldInfo.getSeed(), this.settings);
        this.biomeCache = new ChunkCache<BiomeChunk>(
            "biomes",
            (chunkX, chunkZ) -> {
                // No need to pregenerate chunk if we know all the biomes will be the same (single biome)
                Biome[] biomes = this.biomeSource instanceof SingleBiomeSource ?
                    this.getBaseBiomes(null, chunkX << 4, chunkZ << 4, 16, 16) :
                    this.chunkSource.getBiomes(chunkX, chunkZ);
                
                return new BiomeChunk(biomes);
            }
        );
        
        this.chunkSource = null;
    }

    @Override
    public Biome getBiome(BlockPos blockPos)  {
        return this.getBiome(blockPos, null);
    }

    @Override
    public Biome getBiome(BlockPos blockPos, Biome defaultBiome) {
        return this.getBiome(blockPos.getX(), blockPos.getZ());
    }
    
    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int startX, int startZ, int sizeX, int sizeZ) {
        // Unused for the mod, used for just terrain gen in vanilla
        return this.getBiomes(biomes, startX >> 2, startZ >> 2, sizeX, sizeZ, true);
    }
    
    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int startX, int startZ, int sizeX, int sizeZ) {
        return this.getBiomes(biomes, startX, startZ, sizeX, sizeZ, true);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int startX, int startZ, int sizeX, int sizeZ, boolean cacheFlag) {
        if (biomes == null || biomes.length != sizeX * sizeZ) {
            biomes = new Biome[sizeX * sizeZ];
        }
        
        // Accesses done as x + z * 16, due to how vanilla biome array is stored.
        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                biomes[localX + localZ * 16] = this.getBiome(x, z);
            }
        }
        
        return biomes;
    }
    
    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> allowed, Random random) {
        int minX = x - range >> 2;
        int maxX = x + range >> 2;
        
        int minZ = z - range >> 2;
        int maxZ = z + range >> 2;
        
        int sizeX = maxX - minX + 1;
        int sizeZ = maxZ - minZ + 1;
        
        BlockPos blockPos = null;
        int j = 0;
        
        for (int i = 0; i < sizeX * sizeZ; ++i) {
            int startX = minX + i % sizeX << 2;
            int startZ = minZ + i / sizeX << 2;
            
            // Do not use injector here to speed initial world load
            Biome biome = this.biomeSource.getBiome(startX, startZ);
            
            if (allowed.contains(biome) && (blockPos == null || random.nextInt(j + 1) == 0)) {
                blockPos = new BlockPos(startX, 0, startZ);
                ++j;
            }
        }
        
        return blockPos;
    }
    
    /*
     * Used for structure generation
     */
    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        if (radius == 0) {
            return allowed.contains(this.getBiome(x, z));
        } else {
            double r2 = radius * radius;
            
            for (int dX = x - radius; dX < x + radius; ++dX) {
                for (int dZ = z - radius; dZ < z + radius; ++dZ) {
                    double distance = MathUtil.distance(x, z, dX, dZ);
                    
                    if (distance < r2) {
                        Biome biome = this.getBiome(dX, dZ);
                        
                        if (!allowed.contains(biome))
                            return false;
                    }
                }
            }
        }

        return true;
    }
    
    public Biome[] getBaseBiomes(@Nullable Biome[] biomes, int startX, int startZ, int sizeX, int sizeZ) {
        if (biomes == null || biomes.length != sizeX * sizeZ) {
            biomes = new Biome[sizeX * sizeZ];
        }
        
        // Accesses done as x + z * 16, due to how vanilla biome array is stored.
        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                biomes[localX + localZ * 16] = this.biomeSource.getBiome(x, z);
            }
        }
        
        return biomes;
    }
    
    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }
    
    public void setChunkSource(ChunkSource chunkSource) {
        this.chunkSource = chunkSource;
    }
    
    public boolean useVillageVariants() {
        return this.settings.useVillageVariants;
    }
    
    private Biome getBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
    }
}