package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeDecoratorInfdev611 extends BiomeDecoratorInfdev {
    public BiomeDecoratorInfdev611() {
        super(ModernBetaBiome.TREE_FEATURE);
    }
    
    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world, startPos.getX() >> 4, startPos.getZ() >> 4);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        double scale = 0.5;
        int treeCount = (int) (forestOctaveNoise.sampleXY(startX * scale, startZ * scale) / 8.0 + random.nextDouble() * 4.0 + 4.0);
        
        if (treeCount < 0)
            treeCount = 0;
        
        if (random.nextInt(10) == 0)
            treeCount++;
        
        return treeCount;
    }
}
