package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.feature.WorldGenFancyOak;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;

public abstract class ModernBetaBiome extends Biome {
    public static final WorldGenTrees TREE_FEATURE = new WorldGenTrees(false);
    public static final WorldGenFancyOak BIG_TREE_FEATURE = new WorldGenFancyOak(false);
    
    public ModernBetaBiome(BiomeProperties properties) {
        super(properties);
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        return (random.nextInt(10) == 0) ? BIG_TREE_FEATURE : Biome.TREE_FEATURE;
    }
}
