package mod.bespectacled.modernbetaforge.world.biome;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSourceType;
import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaBiomeProvider extends BiomeProvider {
    private final BiomeSource biomeSource;
    
    public ModernBetaBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBetaChunkGeneratorSettings settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(worldInfo.getGeneratorOptions()).build() :
            new ModernBetaChunkGeneratorSettings.Factory().build();

        this.biomeSource = BiomeSourceType.fromId(settings.biomeSource).create(worldInfo);
    }
    
    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int sizeX, int sizeZ) {
        // Unused for the mod, used for just terrain gen in vanilla
        return this.getBiomes(biomes, x >> 2, z >> 2, sizeX, sizeZ, true);
    }
    
    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int x, int z, int sizeX, int sizeZ) {
        return this.getBiomes(biomes, x, z, sizeX, sizeZ, true);
    }

    @Override
    public Biome[] getBiomes(@Nullable Biome[] biomes, int x, int z, int sizeX, int sizeZ, boolean cacheFlag) {
        if (biomes == null || biomes.length != sizeX * sizeZ) {
            biomes = new Biome[sizeX * sizeZ];
        }
        
        // Accesses done as x + z * 16, due to how vanilla biome array is stored.
        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                biomes[localX + localZ * 16] = this.biomeSource.getBiome(x + localX, 0, z + localZ);
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
            Biome biome = this.biomeSource.getBiome(startX, 0, startZ);
            
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
            return allowed.contains(this.biomeSource.getBiome(x, 0, z));
        } else {
            double r2 = radius * radius;
            
            for (int dX = x - radius; dX < x + radius; ++dX) {
                for (int dZ = z - radius; dZ < z + radius; ++dZ) {
                    double distance = MathUtil.distance(x, z, dX, dZ);
                    
                    if (distance < r2) {
                        Biome biome = this.biomeSource.getBiome(dX, 0, dZ);
                        
                        if (!allowed.contains(biome))
                            return false;
                    }
                }
            }
        }

        return true;
    }
    
    /*
     * Used for monument structure generation
     */
    public boolean areInjectedBiomesViable(int x, int z, int radius, List<Biome> allowed, ChunkSource chunkSource) {
        if (radius == 0) {
            return allowed.contains(chunkSource.getInjectedBiomeAtBlock(x, z));
        } else {
            double r2 = radius * radius;
            
            for (int dX = x - radius; dX < x + radius; ++dX) {
                for (int dZ = z - radius; dZ < z + radius; ++dZ) {
                    double distance = MathUtil.distance(x, z, dX, dZ);
                    
                    if (distance < r2) {
                        Biome biome = chunkSource.getInjectedBiomeAtBlock(dX, dZ);
                        
                        if (!allowed.contains(biome))
                            return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }
}
