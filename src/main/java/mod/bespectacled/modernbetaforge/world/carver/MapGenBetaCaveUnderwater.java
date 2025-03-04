package mod.bespectacled.modernbetaforge.world.carver;

import java.util.List;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MapGenBetaCaveUnderwater extends MapGenBeta18Cave {
    public MapGenBetaCaveUnderwater(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource.getDefaultBlock(), BlockStates.AIR, chunkSource.getDefaultFluid(), 1.0f, chunkSource.getSeaLevel(), 20, 15);
    }
    
    @Override
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer, Biome[] biomes, List<StructureComponent> structureComponents) {
        int x = originChunkX << 4;
        int z = originChunkZ << 4;

        if (BiomeDictionary.hasType(biomes[(x & 0xF) + (z & 0xF) * 16], Type.OCEAN)) {
            super.generate(world, originChunkX, originChunkZ, chunkPrimer, biomes, structureComponents);
        }
    }

    @Override
    public void generate(World world, int originChunkX, int originChunkZ, ChunkPrimer chunkPrimer) {
        this.world = world;
        this.rand.setSeed(world.getSeed());
        
        long randomLong0 = (this.rand.nextLong() / 2L) * 3L + 2L;
        long randomLong1 = (this.rand.nextLong() / 2L) * 3L + 2L;
        
        for (int chunkX = originChunkX - this.range; chunkX <= originChunkX + this.range; ++chunkX) {
            for (int chunkZ = originChunkZ - this.range; chunkZ <= originChunkZ + this.range; ++chunkZ) {
                long randomLong2 = (long)chunkX * randomLong0;
                long randomLong3 = (long)chunkZ * randomLong1;
                long chunkSeed = randomLong2 ^ randomLong3 ^ world.getSeed();
                this.setRandoms(chunkSeed);
                
                this.recursiveGenerate(world, chunkX, chunkZ, originChunkX, originChunkZ, chunkPrimer);
            }
        }
    }

    @Override
    protected void carveAtPoint(ChunkPrimer chunkPrimer, int localX, int localY, int localZ, Block block, boolean isGrassBlock) {
        if (this.carvables.contains(block) && localY - 1 == LAVA_LEVEL - 1) {
            chunkPrimer.setBlockState(localX, localY, localZ, BlockStates.OBSIDIAN);
            
        } else {
            super.carveAtPoint(chunkPrimer, localX, localY, localZ, block, isGrassBlock);
            
        }
    }
}
