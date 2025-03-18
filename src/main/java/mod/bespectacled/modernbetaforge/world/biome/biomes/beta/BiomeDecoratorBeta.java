package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;
import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
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

public class BiomeDecoratorBeta extends ModernBetaBiomeDecorator {
    @Override
    public void decorate(World world, Random random, Biome biome, BlockPos startPos) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(world);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        this.chunkPos = startPos;
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        ChunkPos chunkPos = new ChunkPos(startX >> 4, startZ >> 4);
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, random, chunkPos));
        
        /*
         * Lake and dungeon generation handled in chunk source populate method.
         */

        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, random, startPos));
        this.populateOres(world, random, biome, startPos, mutablePos);
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, random, startPos));
        
        Supplier<WorldGenAbstractTree> treeSupplier = biome instanceof ModernBetaBiome ?
            () -> ((ModernBetaBiome)biome).getRandomTreeFeature(random, settings) :
            () -> biome.getRandomTreeFeature(random);
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            this.populateTrees(world, random, biome, startPos, mutablePos, treeSupplier);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            int plantCount = this.getYellowFlowerCount(biome);
            
            populateWorldGenCount(world, random, startPos, FEATURE_DANDELION, mutablePos, plantCount, settings.height);
        }

        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            int plantCount = this.getTallGrassCount(biome);

            populateTallGrassCount(world, random, biome, startPos, mutablePos, plantCount, settings.height);
        }
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH)) {
            if (biome instanceof BiomeBetaDesert) {
                populateWorldGenCount(world, random, startPos, FEATURE_DEAD_BUSH, mutablePos, 2, settings.height);
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            populateWorldGenChance(world, random, startPos, FEATURE_POPPY, mutablePos, 2, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            populateWorldGenChance(world, random, startPos, FEATURE_BROWN_SHROOM, mutablePos, 4, settings.height);
            populateWorldGenChance(world, random, startPos, FEATURE_RED_SHROOM, mutablePos, 8, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.REED)) {
            populateWorldGenCount(world, random, startPos, FEATURE_REED, mutablePos, 10, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.PUMPKIN)) {
            populateWorldGenChance(world, random, startPos, FEATURE_PUMPKIN, mutablePos, 32, settings.height);
        }
        
        if (biome instanceof BiomeBetaDesert && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.CACTUS)) {
            populateWorldGenCount(world, random, startPos, FEATURE_CACTUS, mutablePos, 10, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_WATER)) {
            this.populateWaterfalls(world, random, startPos, mutablePos, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)) {
            this.populateLavafalls(world, random, startPos, mutablePos, settings.height);
        }
        
        /*
         *  Snow / ice generation handled in chunk source population method,
         *  if put here massive amounts of lag then crash will occur.
         */
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, chunkPos));
    }

    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world, startPos.getX() >> 4, startPos.getZ() >> 4);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        double scale = 0.5;        
        int noiseCount = (int) ((forestOctaveNoise.sampleXY(startX * scale, startZ * scale) / 8.0 + random.nextDouble() * 4.0 + 4.0) / 3.0);
        int treeCount = 0;
        
        if (random.nextInt(10) == 0) {
            treeCount++;
        }
        
        if (biome instanceof BiomeBetaForest) {
            treeCount += noiseCount + 5;
        }
        
        if (biome instanceof BiomeBetaRainforest) {
            treeCount += noiseCount + 5;
        }
        
        if (biome instanceof BiomeBetaSeasonalForest) {
            treeCount += noiseCount + 2;
        }
        
        if (biome instanceof BiomeBetaTaiga) {
            treeCount += noiseCount + 5;
        }
        
        if (biome instanceof BiomeBetaDesert) {
            treeCount -= 20;
        }
        
        if (biome instanceof BiomeBetaTundra) {
            treeCount -= 20;
        }
        
        if (biome instanceof BiomeBetaPlains) {
            treeCount -= 20;
        }
        
        if (biome instanceof BiomeBetaIceDesert) {
            treeCount -= 20;
        }
        
        return treeCount;
    }
    
    private int getYellowFlowerCount(Biome biome) {
        int plantCount = 0;
        
        if (biome instanceof BiomeBetaForest) {
            plantCount = 2;
        }
        
        if (biome instanceof BiomeBetaSeasonalForest) {
            plantCount = 4;
        }
        
        if (biome instanceof BiomeBetaTaiga) {
            plantCount = 2;
        }
        
        if (biome instanceof BiomeBetaPlains) {
            plantCount = 3;
        }
        
        return plantCount;
    }
    
    private int getTallGrassCount(Biome biome) {
        int plantCount = 0;
        
        if (biome instanceof BiomeBetaForest) {
            plantCount = 2;
        }

        if (biome instanceof BiomeBetaRainforest) {
            plantCount = 10;
        }
        
        if (biome instanceof BiomeBetaSeasonalForest) {
            plantCount = 2;
        }
        
        if (biome instanceof BiomeBetaTaiga) {
            plantCount = 1;
        }
        
        if (biome instanceof BiomeBetaPlains) {
            plantCount = 10;
        }
        
        return plantCount;
    }
}
