package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenClay;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass;
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
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorBeta extends ModernBetaBiomeDecorator {
    @Override
    public void decorate(World world, Random random, Biome biome, BlockPos startPos) {
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        int chunkX = startX >> 4;
        int chunkZ = startZ >> 4;
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        // Ore generators
        WorldGenerator worldGenClay = new WorldGenClay(settings.claySize);
        WorldGenerator worldGenDirt = new WorldGenMinable(BlockStates.DIRT, settings.dirtSize);
        WorldGenerator worldGenGravel = new WorldGenMinable(BlockStates.GRAVEL, settings.gravelSize);
        WorldGenerator worldGenCoal = new WorldGenMinable(BlockStates.COAL_ORE, settings.coalSize);
        WorldGenerator worldGenIron = new WorldGenMinable(BlockStates.IRON_ORE, settings.ironSize);
        WorldGenerator worldGenGold = new WorldGenMinable(BlockStates.GOLD_ORE, settings.goldSize);
        WorldGenerator worldGenRedstone = new WorldGenMinable(BlockStates.REDSTONE_ORE, settings.redstoneSize);
        WorldGenerator worldGenDiamond = new WorldGenMinable(BlockStates.DIAMOND_ORE, settings.diamondSize);
        WorldGenerator worldGenLapis = new WorldGenMinable(BlockStates.LAPIS_ORE, settings.lapisSize);
        
        // New mineable generators
        WorldGenerator worldGenGranite = new WorldGenMinable(BlockStates.GRANITE, settings.graniteSize);
        WorldGenerator worldGenDiorite = new WorldGenMinable(BlockStates.DIORITE, settings.dioriteSize);
        WorldGenerator worldGenAndesite = new WorldGenMinable(BlockStates.ANDESITE, settings.andesiteSize);
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, random, chunkPos));
        
        /*
         * Lake and dungeon generation handled in chunk source populate method.
         */

        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, random, startPos));
        
        if (TerrainGen.generateOre(world, random, worldGenClay, startPos, OreGenEvent.GenerateMinable.EventType.CUSTOM)) {
            for (int i = 0; i < settings.clayCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.clayMinHeight, settings.clayMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenClay.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.generateOre(world, random, worldGenDirt, startPos, OreGenEvent.GenerateMinable.EventType.DIRT)) {
            for (int i = 0; i < settings.dirtCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.dirtMinHeight, settings.dirtMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenDirt.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenGravel, startPos, OreGenEvent.GenerateMinable.EventType.GRAVEL)) {
            for (int i = 0; i < settings.gravelCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.gravelMinHeight, settings.gravelMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenGravel.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.generateOre(world, random, worldGenDiorite, startPos, OreGenEvent.GenerateMinable.EventType.DIORITE)) {
            for (int i = 0; i < settings.dioriteCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.dioriteMinHeight, settings.dioriteMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenDiorite.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.generateOre(world, random, worldGenGranite, startPos, OreGenEvent.GenerateMinable.EventType.GRANITE)) {
            for (int i = 0; i < settings.graniteCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.graniteMinHeight, settings.graniteMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenGranite.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.generateOre(world, random, worldGenAndesite, startPos, OreGenEvent.GenerateMinable.EventType.ANDESITE)) {
            for (int i = 0; i < settings.andesiteCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.andesiteMinHeight, settings.andesiteMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenAndesite.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.generateOre(world, random, worldGenCoal, startPos, OreGenEvent.GenerateMinable.EventType.COAL)) {
            for (int i = 0; i < settings.coalCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.coalMinHeight, settings.coalMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenCoal.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenIron, startPos, OreGenEvent.GenerateMinable.EventType.IRON)) {
            for (int i = 0; i < settings.ironCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.ironMinHeight, settings.ironMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenIron.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenGold, startPos, OreGenEvent.GenerateMinable.EventType.GOLD)) {
            for (int i = 0; i < settings.goldCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.goldMinHeight, settings.goldMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenGold.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenRedstone, startPos, OreGenEvent.GenerateMinable.EventType.REDSTONE)) {
            for (int i = 0; i < settings.redstoneCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.redstoneMinHeight, settings.redstoneMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenRedstone.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenDiamond, startPos, OreGenEvent.GenerateMinable.EventType.DIAMOND)) {
            for (int i = 0; i < settings.diamondCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getOreHeight(random, settings.diamondMinHeight, settings.diamondMaxHeight);
                int z = startZ + random.nextInt(16);
                
                worldGenDiamond.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(world, random, worldGenLapis, startPos, OreGenEvent.GenerateMinable.EventType.LAPIS)) {
            for (int i = 0; i < settings.lapisCount; i++) {
                int x = startX + random.nextInt(16);
                int y = this.getLapisHeight(random, settings.lapisCenterHeight, settings.lapisSpread);
                int z = startZ + random.nextInt(16);
                
                worldGenLapis.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, random, startPos));
        
        PerlinOctaveNoise forestOctaveNoise = this.getForestOctaveNoise(world);
        
        double scale = 0.5D;        
        int noiseCount = (int) ((forestOctaveNoise.sampleXY(startX * scale, startZ * scale) / 8D + random.nextDouble() * 4D + 4D) / 3D);
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
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            for (int i = 0; i < treeCount; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int z = startZ + random.nextInt(16) + 8;
                
                WorldGenAbstractTree worldGenTree = biome.getRandomTreeFeature(random);
                worldGenTree.setDecorationDefaults();
                
                BlockPos treePos = world.getHeight(mutablePos.setPos(x, 0, z));
                if (worldGenTree.generate(world, random, treePos)) {
                    worldGenTree.generateSaplings(world, random, treePos);
                }
            }
        }
        
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

        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            for (int i = 0; i < plantCount; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenFlowers(Blocks.YELLOW_FLOWER, EnumFlowerType.DANDELION).generate(world, random, mutablePos.setPos(x, y, z));
            }
            
            if (random.nextInt(2) == 0) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenFlowers(Blocks.RED_FLOWER, EnumFlowerType.POPPY).generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        plantCount = 0;
        
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
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.GRASS)) {
            for (int i = 0; i < plantCount; ++i) {
                BlockTallGrass.EnumType tallGrassType = BlockTallGrass.EnumType.GRASS;
                if (biome instanceof BiomeBetaRainforest && random.nextInt(3) != 0) {
                    tallGrassType = BlockTallGrass.EnumType.FERN;
                }
                
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenTallGrass(tallGrassType).generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (settings.useTallGrass && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH)) {
            if (biome instanceof BiomeBetaDesert) {
                for (int i = 0; i < 2; ++i) {
                    int x = startX + random.nextInt(16) + 8;
                    int y = random.nextInt(128);
                    int z = startZ + random.nextInt(16) + 8;
                    
                    new WorldGenDeadBush().generate(world, random, mutablePos.setPos(x, y, z));
                }
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            if (random.nextInt(4) == 0) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenBush(Blocks.BROWN_MUSHROOM).generate(world, random, mutablePos.setPos(x, y, z));
            }
            
            if (random.nextInt(8) == 0) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenBush(Blocks.RED_MUSHROOM).generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.REED)) {
            for (int i = 0; i < 10; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenReed().generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.PUMPKIN)) {
            if (random.nextInt(32) == 0) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenPumpkin().generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (biome instanceof BiomeBetaDesert && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.CACTUS)) {
            for (int i = 0; i < 10; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenCactus().generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_WATER)) {
            for (int i = 0; i < 50; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(random.nextInt(120) + 8);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenLiquids(Blocks.FLOWING_WATER).generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA)) {
            for (int i = 0; i < 20; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(random.nextInt(random.nextInt(112) + 8) + 8);
                int z = startZ + random.nextInt(16) + 8;
                
                new WorldGenLiquids(Blocks.FLOWING_LAVA).generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        /*
         *  Snow / ice generation handled in chunk source population method,
         *  if put here massive amounts of lag then crash will occur.
         */
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, chunkPos));
    }
}
