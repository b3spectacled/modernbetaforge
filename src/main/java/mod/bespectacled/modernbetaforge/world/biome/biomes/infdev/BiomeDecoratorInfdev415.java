package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeDecoratorInfdev415 extends BiomeDecoratorInfdev {
    public BiomeDecoratorInfdev415() {
        super(ModernBetaBiome.BIG_TREE_FEATURE);
    }
    
    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world, startPos.getX() >> 4, startPos.getZ() >> 4);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        double scale = 0.25;
        return (int) forestOctaveNoise.sample(startX * scale, startZ * scale) << 3;
    }
}
