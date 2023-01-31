package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenFancyOak;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
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

    public PerlinOctaveNoise getForestOctaveNoise(World world) {
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
