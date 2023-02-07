package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class ModernBetaBiomeDecorator extends BiomeDecorator {
    protected int getOreHeight(Random random, int minHeight, int maxHeight) {
        if (maxHeight < minHeight) {
            int height = minHeight;
            
            minHeight = maxHeight;
            maxHeight = height;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }
        
        return random.nextInt(maxHeight - minHeight) + minHeight;
    }
    
    protected int getLapisHeight(Random random, int centerHeight, int spread) {
        return random.nextInt(spread) + random.nextInt(spread) + centerHeight - spread;
    }
    
    protected PerlinOctaveNoise getForestOctaveNoise(World world) {
        ChunkProviderServer chunkProviderServer = (ChunkProviderServer)world.getChunkProvider();
        IChunkGenerator chunkGenerator = chunkProviderServer.chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ModernBetaChunkGenerator modernBetaChunkGenerator = (ModernBetaChunkGenerator)chunkGenerator;
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();
            
            if (chunkSource instanceof NoiseChunkSource) {
                return ((NoiseChunkSource)chunkSource).getForestOctaveNoise().orElse(new PerlinOctaveNoise(new Random(world.getSeed()), 8, true));
            }
        }
        
        return new PerlinOctaveNoise(new Random(world.getSeed()), 8, true);
    }
}
