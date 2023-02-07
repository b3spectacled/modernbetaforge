package mod.bespectacled.modernbetaforge.world.biome.infdev;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenClay;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeDecoratorInfdev415 extends ModernBetaBiomeDecorator {
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
        
        double scale = 0.25D;
        int treeCount = (int) forestOctaveNoise.sampleXY(startX * scale, startZ * scale) << 3;
        
        if (TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            for (int i = 0; i < treeCount; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int z = startZ + random.nextInt(16) + 8;
                
                WorldGenAbstractTree worldGenTree = ModernBetaBiome.BIG_TREE_FEATURE;
                worldGenTree.setDecorationDefaults();
                
                BlockPos treePos = world.getHeight(mutablePos.setPos(x, 0, z));
                if (worldGenTree.generate(world, random, treePos)) {
                    worldGenTree.generateSaplings(world, random, treePos);
                }
            }
        }
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, random, chunkPos));
    }
}
