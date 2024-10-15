package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Optional;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenFancyOak;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModernBetaBiome extends Biome {
    public static final WorldGenTrees TREE_FEATURE = new WorldGenTrees(false);
    public static final WorldGenFancyOak BIG_TREE_FEATURE = new WorldGenFancyOak(false);
    
    protected int skyColor;
    protected int fogColor;
    
    protected int grassColor;
    protected int foliageColor;
    
    public ModernBetaBiome(BiomeProperties properties) {
        super(properties);
        
        this.skyColor = -1;
        this.fogColor = -1;
        
        this.grassColor = -1;
        this.foliageColor = -1;
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        if (this.skyColor != -1)
            return this.skyColor;
        
        return super.getSkyColorByTemp(temp);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        if (this.grassColor != -1)
            return this.grassColor;
        
        return super.getGrassColorAtPos(blockPos);
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        if (this.foliageColor != -1)
            return this.foliageColor;
        
        return super.getFoliageColorAtPos(blockPos);
    }
    
    public Optional<Integer> getFogColor() {
        Optional<Integer> fogColor = Optional.empty();
        
        if (this.fogColor != -1)
            fogColor = Optional.of(this.fogColor);
        
        return fogColor;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        return (random.nextInt(10) == 0) ? BIG_TREE_FEATURE : TREE_FEATURE;
    }
    
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        return this.getRandomTreeFeature(random);
    }
}
