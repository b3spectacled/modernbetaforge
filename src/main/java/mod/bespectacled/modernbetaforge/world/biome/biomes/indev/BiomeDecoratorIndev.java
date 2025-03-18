package mod.bespectacled.modernbetaforge.world.biome.biomes.indev;

import java.util.Random;
import java.util.function.Supplier;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorIndev extends ModernBetaBiomeDecorator {
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

        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            this.populateTrees(world, random, biome, startPos, mutablePos, () -> ModernBetaBiome.TREE_FEATURE);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            int count = this.getFlowerCount(biome);
            
            populateWorldGenCount(world, random, startPos, FEATURE_DANDELION, mutablePos, count, settings.height);
            populateWorldGenCount(world, random, startPos, FEATURE_POPPY, mutablePos, count, settings.height);
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            int count = this.getMushroomCount();
            
            populateWorldGenCount(world, random, startPos, FEATURE_BROWN_SHROOM, mutablePos, count, settings.height);
            populateWorldGenCount(world, random, startPos, FEATURE_RED_SHROOM, mutablePos, count, settings.height);
        }

        // New feature generators
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            populateTallGrassChance(world, random, biome, startPos, mutablePos, 2, settings.height);
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
