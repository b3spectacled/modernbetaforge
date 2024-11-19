package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;
import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorBeta extends ModernBetaBiomeDecorator {
    private WorldGenerator worldGenDandelion = new WorldGenFlowers(Blocks.YELLOW_FLOWER, EnumFlowerType.DANDELION);
    private WorldGenerator worldGenPoppy = new WorldGenFlowers(Blocks.RED_FLOWER, EnumFlowerType.POPPY);
    private WorldGenerator worldGenDeadBush = new WorldGenDeadBush();
    private WorldGenerator worldGenBrownMushroom = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    private WorldGenerator worldGenRedMushroom = new WorldGenBush(Blocks.RED_MUSHROOM);
    private WorldGenerator worldGenReed = new WorldGenReed();
    private WorldGenerator worldGenPumpkin = new WorldGenPumpkin();
    private WorldGenerator worldGenCactus = new WorldGenCactus();
    
    @Override
    public void decorate(World world, Random random, Biome biome, BlockPos startPos) {
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
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
            
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenDandelion, mutablePos, plantCount, settings.height);
        }

        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            int plantCount = this.getTallGrassCount(biome);

            this.populateTallGrass(world, random, biome, startPos, mutablePos, plantCount, settings.height);
        }
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH)) {
            if (biome instanceof BiomeBetaDesert) {
                this.populateWorldGenCount(world, random, biome, startPos, this.worldGenDeadBush, mutablePos, 2, settings.height);
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenPoppy, mutablePos, 2, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenBrownMushroom, mutablePos, 4, settings.height);
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenRedMushroom, mutablePos, 8, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.REED)) {
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenReed, mutablePos, 10, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.PUMPKIN)) {
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenPumpkin, mutablePos, 32, settings.height);
        }
        
        if (biome instanceof BiomeBetaDesert && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.CACTUS)) {
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenCactus, mutablePos, 10, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_WATER)) {
            this.populateWaterfalls(world, random, biome, startPos, mutablePos, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)) {
            this.populateLavafalls(world, random, biome, startPos, mutablePos, settings.height);
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
