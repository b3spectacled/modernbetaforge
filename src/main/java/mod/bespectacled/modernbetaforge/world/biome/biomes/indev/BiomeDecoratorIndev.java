package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import java.util.Random;
import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorIndev extends ModernBetaBiomeDecorator {
    private WorldGenerator worldGenDandelion = new WorldGenFlowers(Blocks.YELLOW_FLOWER, EnumFlowerType.DANDELION);
    private WorldGenerator worldGenPoppy = new WorldGenFlowers(Blocks.RED_FLOWER, EnumFlowerType.POPPY);
    private WorldGenerator worldGenBrownMushroom = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    private WorldGenerator worldGenRedMushroom = new WorldGenBush(Blocks.RED_MUSHROOM);
    
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

        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            this.populateTrees(world, random, biome, startPos, mutablePos, () -> ModernBetaBiome.TREE_FEATURE);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            int count = this.getFlowerCount(biome);
            
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenDandelion, mutablePos, count, settings.height);
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenPoppy, mutablePos, count, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            int count = this.getMushroomCount();
            
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenBrownMushroom, mutablePos, count, settings.height);
            this.populateWorldGenCount(world, random, biome, startPos, this.worldGenRedMushroom, mutablePos, count, settings.height);
        }

        // New feature generators
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            this.populateTallGrass(world, random, biome, startPos, mutablePos, 1, settings.height);
        }
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, chunkPos));
    }
    
    protected void populateTrees(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, Supplier<WorldGenAbstractTree> treeFunc) {
        // A very feelscrafty simulation of Indev tree spawn spread.
        if (biome instanceof BiomeIndevWoods || random.nextInt(2) == 0) {
            super.populateTrees(world, random, biome, startPos, mutablePos, treeFunc);
        }
    }
    
    @Override
    protected int getTreeCount(World world, Random random, Biome biome, BlockPos startPos) {
        if (biome instanceof BiomeIndevWoods) {
            return 10;
        }
        
        return random.nextInt(5);
    }

    /*
     * Algorithm for determining plant generation attempt count:
     *  width * depth * height * baseCount / 1600000
     * where baseCount is:
     *  - 100 for flowers
     *  - 1000 for flowers in Paradise theme
     *  - 50 for mushrooms
     *  
     * Ex: A standard 128x128x64 normal world will try to generate a patch of flowers 65 times.
     * For each of these attempts, a random position in the level is chosen
     *  
     * To try to simulate original generation, take the result and divide by number of chunks in the level,
     * so using the above example, where 128x128x64 is 64 chunks in area:
     *  - ~1 flower patch per chunk
     *  - ~10 folower patches per chunk in Paradise biomes
     *  - ~0.5 mushroom patches per chunk, use random.nextInt(2) to simulate
     *  
     * However, the flower spread method only attempts to generate 64 times, vs. 100 times in Indev,
     * so the numbers are tweaked a bit more.
     * 
     */
    
    private int getFlowerCount(Biome biome) {
        return biome instanceof BiomeIndevParadise ? 20 : 2;
    }
    
    private int getMushroomCount() {
        return 2;
    }
}
