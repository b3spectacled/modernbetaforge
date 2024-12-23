package mod.bespectacled.modernbetaforge.world.spawn;

import java.util.Random;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IndevSpawnLocator implements SpawnLocator {
    @Override
    public BlockPos locateSpawn(World world, BlockPos spawnPos, ChunkSource chunkSource, BiomeSource biomeSource) {
        if (!(chunkSource instanceof FiniteChunkSource))
            return SpawnLocator.DEFAULT.locateSpawn(world, spawnPos, chunkSource, biomeSource);
        
        FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
        Random random = new Random();

        int spawnX;
        int spawnY;
        int spawnZ;
        
        int attempts = 0;
        
        int width = finiteChunkSource.getLevelWidth();
        int length = finiteChunkSource.getLevelLength();
        //int height = this.chunkProvider.getLevelHeight();
        
        // block0
        while (true) {
            spawnX = random.nextInt(width / 2) + width / 4;
            spawnZ = random.nextInt(length / 2) + length / 4;
            spawnY = finiteChunkSource.getHeight(world, spawnX - width / 2, spawnZ - length / 2, Type.SURFACE) + 2;
            
            if (attempts >= 1000000) {
                ModernBeta.log(Level.INFO, "[Indev] Exceeded spawn attempts, spawning anyway..");
                // spawnY = height + 100; From original code, but tends to fail on small worlds
                
                break;
            }
            
            attempts++;
            
            if (spawnY < 4) 
                continue;
            
            if (spawnY <= finiteChunkSource.getSeaLevel()) 
                continue;
            
            if (this.nearSolidBlocks0(spawnX, spawnY, spawnZ, finiteChunkSource))
                continue;
            
            if (this.nearSolidBlocks1(spawnX, spawnY, spawnZ, finiteChunkSource))
                continue;
            
            break;
        }
        
        // Offset spawn coordinates since Indev worlds are centered on 0/0.
        return new BlockPos(spawnX - width / 2, spawnY - 1, spawnZ - length / 2);
    }
    
    private boolean nearSolidBlocks0(int spawnX, int spawnY, int spawnZ, FiniteChunkSource chunkSource) {
        for (int x = spawnX - 3; x <= spawnX + 3; ++x) {
            for (int y = spawnY - 1; y <= spawnY + 2; ++y) {
                for (int z = spawnZ - 3 - 2; z <= spawnZ + 3; ++z) {
                    Block block = chunkSource.getLevelBlock(x, y, z);
                    
                    // Check if nearby block is solid and continue if so.
                    if (!(block.equals(Blocks.AIR) || block.equals(chunkSource.getDefaultFluid().getBlock())))
                        return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean nearSolidBlocks1(int spawnX, int spawnY, int spawnZ, FiniteChunkSource chunkSource) {
        int y = spawnY - 2;
        for (int x = spawnX - 3; x <= spawnX + 3; ++x) {
            for (int z = spawnZ - 3 - 2; z <= spawnZ + 3; ++z) {
                Block block = chunkSource.getLevelBlock(x, y, z);
                
                if (!(block.equals(Blocks.AIR) || block.equals(chunkSource.getDefaultFluid().getBlock())))
                    continue;
                
                return true;
            }
        }
        
        return false;
    }

}
