package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeDecoratorInfdev420 extends BiomeDecoratorInfdev {
    public BiomeDecoratorInfdev420() {
        super(ModernBetaBiome.BIG_TREE_FEATURE);
    }
    
    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world, startPos.getX() >> 4, startPos.getZ() >> 4);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        double scale = 0.05;
        int treeCount = (int) (forestOctaveNoise.sample(startX * scale, startZ * scale) - random.nextDouble());
        
        if (treeCount < 0)
            treeCount = 0;
        
        if (random.nextInt(100) == 0)
            treeCount++;
        
        return treeCount;
    }
}
