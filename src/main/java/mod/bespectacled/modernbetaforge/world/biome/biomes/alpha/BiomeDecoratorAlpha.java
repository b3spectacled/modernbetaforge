package mod.bespectacled.modernbetaforge.world.biome.biomes.alpha;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorAlpha extends ModernBetaBiomeDecorator {
    private WorldGenerator worldGenDandelion = new WorldGenFlowers(Blocks.YELLOW_FLOWER, EnumFlowerType.DANDELION);
    private WorldGenerator worldGenPoppy = new WorldGenFlowers(Blocks.RED_FLOWER, EnumFlowerType.POPPY);
    private WorldGenerator worldGenBrownMushroom = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    private WorldGenerator worldGenRedMushroom = new WorldGenBush(Blocks.RED_MUSHROOM);
    private WorldGenerator worldGenReed = new WorldGenReed();
    private WorldGenerator worldGenCactus = new WorldGenCactus();
    
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
        
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, random, startPos));
        this.populateOres(world, random, biome, startPos, mutablePos);
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, random, startPos));

        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            this.populateTrees(world, random, biome, startPos, mutablePos, () -> biome.getRandomTreeFeature(random));
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenDandelion, mutablePos, 2, settings.height);
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenPoppy, mutablePos, 2, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenBrownMushroom, mutablePos, 4, settings.height);
            this.populateWorldGenChance(world, random, biome, startPos, this.worldGenRedMushroom, mutablePos, 8, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.REED)) {
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenReed, mutablePos, 10, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.CACTUS)) {
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenCactus, mutablePos, 1, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_WATER)) {
            this.populateWaterfalls(world, random, biome, startPos, mutablePos, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)) {
            this.populateLavafalls(world, random, biome, startPos, mutablePos, settings.height);
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
        
        double scale = 0.5;
        int noiseCount = (int) ((forestOctaveNoise.sampleXY(startX * scale, startZ * scale) / 8.0 + random.nextDouble() * 4.0 + 4.0) / 3.0);
        
        if (noiseCount < 0) {
            noiseCount = 0;
        }
        
        int treeCount = 0;
        
        if (random.nextInt(10) == 0) {
            treeCount++;
        }
        
        treeCount += noiseCount;
        
        return treeCount;
    }
}
