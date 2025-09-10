package mod.bespectacled.modernbetaforge.world.biome;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

public class ModernBetaBiomeProvider extends BiomeProvider {
    private final ModernBetaGeneratorSettings settings;
    private final BiomeSource biomeSource;

    private final ChunkCache<BiomeChunk> baseBiomeCache;
    private final ChunkCache<BiomeChunk> injectedBiomeCache;
    
    private ModernBetaChunkGenerator chunkGenerator;
    
    public ModernBetaBiomeProvider(WorldInfo worldInfo) {
        super(worldInfo);

        this.settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaGeneratorSettings.build(worldInfo.getGeneratorOptions()) :
            ModernBetaGeneratorSettings.build();

        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE
            .get(this.settings.biomeSource)
            .apply(worldInfo.getSeed(), this.settings);

        this.baseBiomeCache = new ChunkCache<BiomeChunk>(
            "base_biomes",
            (chunkX, chunkZ) -> new BiomeChunk(this.getBaseBiomes(null, chunkX << 4, chunkZ << 4, 16, 16))
        );
        this.injectedBiomeCache = new ChunkCache<BiomeChunk>(
            "biomes",
            (chunkX, chunkZ) -> new BiomeChunk(this.chunkGenerator.getBiomes(chunkX, chunkZ))
        );
        
        this.chunkGenerator = null;
        
        DebugUtil.resetDebug(DebugUtil.SECTION_GET_BASE_BIOMES);
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
    public BlockPos findBiomePosition(int startX, int startZ, int range, List<Biome> allowed, Random random) {
        // Do not use injector here to speed initial world load
        return findBiomePosition(startX, startZ, range, allowed, random, this.biomeSource::getBiome);
    }
    
    /*
     * Used for structure generation
     */
    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed) {
        return areBiomesViable(x, z, radius, allowed, this::getBiome);
    }
    
    public Biome getBaseBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
            
        return this.baseBiomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    public Biome[] getBaseBiomes(int chunkX, int chunkZ) {
        return this.baseBiomeCache.get(chunkX, chunkZ).getBiomes();
    }
    
    /*
     * Used for /locatebiome command
     */
    public BlockPos locateBiome(int startX, int startZ, int range, int steps, Biome biomeToSearch, Random random) {
        BlockPos blockPos = null;
        int chance = 0;
        
        for (int size = 0; size <= range; size += steps) {
            for (int localX = -size; localX <= size; localX += steps) {
                boolean atEdgeX = Math.abs(localX) == size;
                
                for (int localZ = -size; localZ <= size; localZ += steps) {
                    boolean atEdgeZ = Math.abs(localZ) == size;
                    
                    if (!atEdgeX && !atEdgeZ) {
                        continue;
                    }
                    
                    int x = startX + localX;
                    int z = startZ + localZ;
                    
                    if (this.getBiome(x, z) == biomeToSearch && (blockPos == null || random.nextInt(chance + 1) == 0)) {
                        return new BlockPos(x, 0, z);
                    }
                    
                    ++chance;
                }
                
            }
        }
        
        return blockPos;
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource;
    }
    
    public void setChunkGenerator(ModernBetaChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
    }
    
    public boolean useVillageVariants() {
        return this.settings.useVillageVariants;
    }
    
    private Biome[] getBaseBiomes(@Nullable Biome[] biomes, int startX, int startZ, int sizeX, int sizeZ) {        
        DebugUtil.startDebug(DebugUtil.SECTION_GET_BASE_BIOMES);
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
    
        DebugUtil.endDebug(DebugUtil.SECTION_GET_BASE_BIOMES);
        return biomes;
    }

    private Biome getBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.injectedBiomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    public static BlockPos findBiomePosition(int startX, int startZ, int range, List<Biome> allowed, Random random, BiFunction<Integer, Integer, Biome> biomeFunc) {
        int minX = startX - range >> 2;
        int maxX = startX + range >> 2;
        
        int minZ = startZ - range >> 2;
        int maxZ = startZ + range >> 2;
        
        int sizeX = maxX - minX + 1;
        int sizeZ = maxZ - minZ + 1;
        
        BlockPos blockPos = null;
        int chance = 0;
        
        Set<Biome> allowedBiomes = new HashSet<>(allowed);
        
        for (int i = 0; i < sizeX * sizeZ; ++i) {
            int x = minX + i % sizeX << 2;
            int z = minZ + i / sizeX << 2;
            
            Biome biome = biomeFunc.apply(x, z);
            
            if (allowedBiomes.contains(biome) && (blockPos == null || random.nextInt(chance + 1) == 0)) {
                blockPos = new BlockPos(x, 0, z);
                ++chance;
            }
        }
        
        return blockPos;
    }
    
    public static boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed, BiFunction<Integer, Integer, Biome> biomeFunc) {
        Set<Biome> allowedBiomes = new HashSet<>(allowed);
        
        if (radius == 0) {
            return allowedBiomes.contains(biomeFunc.apply(x, z));
        } else {
            double r2 = radius * radius;
            
            for (int dX = x - radius; dX < x + radius; ++dX) {
                for (int dZ = z - radius; dZ < z + radius; ++dZ) {
                    double distance = MathUtil.distance(x, z, dX, dZ);
                    
                    if (distance < r2) {
                        if (!allowedBiomes.contains(biomeFunc.apply(dX, dZ)))
                            return false;
                    }
                }
            }
        }

        return true;
    }
}