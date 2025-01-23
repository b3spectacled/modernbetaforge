package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.SurfaceNoiseChunk;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class AlphaSurfaceBuilder extends SurfaceBuilder {
    private final ChunkCache<SurfaceNoiseChunk> sandCache;
    private final ChunkCache<SurfaceNoiseChunk> gravelCache;
    private final ChunkCache<SurfaceNoiseChunk> surfaceCache;
    
    public AlphaSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
        
        this.sandCache = new ChunkCache<>("sand", 16, this::sampleSandNoise);
        this.gravelCache = new ChunkCache<>("gravel", 16, this::sampleGravelNoise);
        this.surfaceCache = new ChunkCache<>("surface", 16, this::sampleSurfaceNoise);
    }

    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);
        Random sandstoneRandom = this.createSurfaceRandom(chunkX, chunkZ);
        
        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                int x = startX + localX;
                int z = startZ + localZ;

                boolean generatesBeaches = this.generatesBeaches(x, z, random);
                boolean generatesGravelBeaches = this.generatesGravelBeaches(x, z, random);
                
                int surfaceDepth = this.sampleSurfaceDepth(x, z, random);
                int runDepth = -1;
                
                Biome biome = biomes[localX + localZ * 16];
                
                IBlockState topBlock = biome.topBlock;
                IBlockState fillerBlock = biome.fillerBlock;
                
                // Skip if used custom surface generation or if below minimum surface level.
                if (this.useCustomSurfaceBuilder(world, biome, chunkPrimer, random, x, z, false)) {
                    continue;
                }
                
                // Generate from top to bottom of world
                for (int y = this.getWorldHeight() - 1; y >= 0; y--) {
                    
                    // Place bedrock
                    if (this.useBedrock() && y <= random.nextInt(6) - 1) {
                        chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                        continue;
                    }

                    IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);

                    if (BlockStates.isAir(blockState)) { // Skip if air block
                        runDepth = -1;
                        continue;
                    }

                    if (!BlockStates.isEqual(blockState, this.defaultBlock)) { // Skip if not stone
                        continue;
                    }

                    if (runDepth == -1) {
                        if (this.generatesBasin(surfaceDepth)) { // Generate stone basin if noise permits
                            topBlock = BlockStates.AIR;
                            fillerBlock = this.defaultBlock;
                            
                        } else if (this.atBeachDepth(y)) { // Generate beaches at this y range
                            topBlock = biome.topBlock;
                            fillerBlock = biome.fillerBlock;

                            if (generatesGravelBeaches) {
                                topBlock = BlockStates.AIR; // This reduces gravel beach height by 1
                                fillerBlock = BlockStates.GRAVEL;
                            }

                            if (generatesBeaches) {
                                topBlock = BlockStates.SAND;
                                fillerBlock = BlockStates.SAND;
                            }
                        }
                        
                        if (y < this.getSeaLevel() && BlockStates.isAir(topBlock)) {
                            topBlock = this.defaultFluid;
                        }

                        runDepth = surfaceDepth;
                        
                        if (y >= this.getSeaLevel() - 1) {
                            chunkPrimer.setBlockState(localX, y, localZ, topBlock);
                        } else {
                            chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                        }

                        continue;
                    }
                    
                    if (runDepth > 0) { 
                        runDepth--;
                        chunkPrimer.setBlockState(localX, y, localZ, fillerBlock);
                    }

                    // Generates layer of sandstone starting at lowest block of sand, of height 1 to 4.
                    if (this.useSandstone() && runDepth == 0 && BlockStates.isEqual(fillerBlock, BlockStates.SAND)) {
                        runDepth = sandstoneRandom.nextInt(4);
                        fillerBlock = fillerBlock.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ?
                            BlockStates.RED_SANDSTONE :
                            BlockStates.SANDSTONE;
                    }
                }
            }
        }
    }
    
    @Override
    public boolean generatesBeaches(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.sandCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return noise + random.nextDouble() * 0.2 > 0.0;
    }
    
    @Override
    public boolean generatesGravelBeaches(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.gravelCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return noise + random.nextDouble() * 0.2 > 3.0;
    }
    
    @Override
    public int sampleSurfaceDepth(int x, int z, Random random) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        double noise = this.surfaceCache.get(chunkX, chunkZ).getNoise()[(x & 0xF) + (z & 0xF) * 16];
        
        return (int)(noise / 3.0 + 3.0 + random.nextDouble() * 0.25);
    }
    
    @Override
    public boolean generatesBasin(int surfaceDepth) {
        return surfaceDepth <= 0;
    }
    
    @Override
    public boolean atBeachDepth(int y) {
        return y >= this.getSeaLevel() - 4 && y <= this.getSeaLevel() + 1;
    }
    
    private SurfaceNoiseChunk sampleSandNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getBeachOctaveNoise().sampleAlpha(
                chunkX << 4, chunkZ << 4, 0.0,
                16, 16, 1,
                0.03125, 0.03125, 1.0
            )
        );
    }
    
    private SurfaceNoiseChunk sampleGravelNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getBeachOctaveNoise().sampleAlpha(
                chunkZ << 4, 109.0134, chunkX << 4,
                16, 1, 16,
                0.03125, 1.0, 0.03125
            )
        );
    }
    
    private SurfaceNoiseChunk sampleSurfaceNoise(int chunkX, int chunkZ) {
        return new SurfaceNoiseChunk(
            this.getSurfaceOctaveNoise().sampleAlpha(
                chunkZ << 4, chunkZ << 4, 0.0,
                16, 16, 1,
                0.03125 * 2.0, 0.03125 * 2.0, 0.03125 * 2.0
            )
        );
    }
}
