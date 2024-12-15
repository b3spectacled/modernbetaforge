package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class BiomeDecoratorInfdev extends ModernBetaBiomeDecorator {
    private final WorldGenAbstractTree treeFeature;
    
    public BiomeDecoratorInfdev(WorldGenAbstractTree treeFeature) {
        this.treeFeature = treeFeature;
    }
    
    @Override
    public void decorate(World world, Random random, Biome biome, BlockPos startPos) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.build(world.getWorldInfo().getGeneratorOptions());
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        ChunkPos chunkPos = new ChunkPos(startX >> 4, startZ >> 4);
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, random, chunkPos));
        
        /*
         * Lake and dungeon generation handled in chunk source populate method.
         */
        
        /*
         * For future reference, since it will not be implemented here due to standardization of ore gen.
         * 
         * In Infdev 20100415, gold and diamond ore deposits do not generate given a number of attempts,
         * they generate based on chance, like certain other features, based on the following conditions:
         * 
         * - Gold: random.nextInt(2) == 0
         * - Diamond: random.nextInt(8) == 0 
         * 
         */
        
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, random, startPos));
        this.populateOres(world, random, biome, startPos, mutablePos);
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, random, startPos));
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            this.populateTrees(world, random, biome, startPos, mutablePos, () -> this.treeFeature);
        }
        
        // New feature generators
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            this.populateTallGrassChance(world, random, biome, startPos, mutablePos, 2, settings.height);
        }
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, chunkPos));
    }

    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world, startPos.getX() >> 4, startPos.getZ() >> 4);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        double scale = 0.25;
        return (int) forestOctaveNoise.sampleXY(startX * scale, startZ * scale) << 3;
    }
}
