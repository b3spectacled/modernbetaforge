package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class ReleaseSurfaceBuilder extends SurfaceBuilder {
    public ReleaseSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }

    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                Biome biome = biomes[localX + localZ * 16];
                this.useCustomSurfaceBuilder(world, biome, chunkPrimer, random, x, z, true);
                
                // Post-process bedrock for variable height (extended) worlds
                if (this.getWorldFloor() != 0) {
                    for (int y = this.getWorldHeight(); y >= this.getWorldFloor(); y--) {
                        IBlockState blockState = chunkPrimer.getBlockState(localX, y, localZ);
                        
                        // Replace vanilla bedrock layer with either default block or air, depending on height map
                        if (y >= 0 && y <= 4 && blockState.getBlock() == Blocks.BEDROCK) {
                            int height = this.chunkSource.getHeight(x, z, Type.FLOOR);
                            
                            chunkPrimer.setBlockState(localX, y, localZ, y <= height ? this.defaultBlock : BlockStates.AIR);
                        }
                        
                        if (this.isBedrock(y, random)) {
                            chunkPrimer.setBlockState(localX, y, localZ, BlockStates.BEDROCK);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean replacesDefaultBlock() {
        return this.defaultBlock.equals(BlockStates.STONE);
    }
}
