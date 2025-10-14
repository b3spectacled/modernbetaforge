package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaRainforest;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenClay;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenMinableMutable;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class ModernBetaBiomeDecorator extends BiomeDecorator {
    public static final WorldGenerator FEATURE_DANDELION = new WorldGenFlowers(Blocks.YELLOW_FLOWER, EnumFlowerType.DANDELION);
    public static final WorldGenerator FEATURE_POPPY = new WorldGenFlowers(Blocks.RED_FLOWER, EnumFlowerType.POPPY);
    
    protected static final WorldGenerator FEATURE_DEAD_BUSH = new WorldGenDeadBush();
    protected static final WorldGenerator FEATURE_BROWN_SHROOM = new WorldGenBush(Blocks.BROWN_MUSHROOM);
    protected static final WorldGenerator FEATURE_RED_SHROOM = new WorldGenBush(Blocks.RED_MUSHROOM);
    protected static final WorldGenerator FEATURE_REED = new WorldGenReed();
    protected static final WorldGenerator FEATURE_PUMPKIN = new WorldGenPumpkin();
    protected static final WorldGenerator FEATURE_CACTUS = new WorldGenCactus();
    
    private static final WorldGenerator FEATURE_LAVA_LAKES = new WorldGenLakes(Blocks.LAVA);
    private static final WorldGenerator FEATURE_DUNGEONS = new WorldGenDungeons();
    private static final WorldGenerator FEATURE_LAVA_FALL = new WorldGenLiquids(Blocks.FLOWING_LAVA);
    
    private static final Set<Block> VANILLA_FLUIDS = ImmutableSet.of(Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA);
    
    private WorldGenerator oreClay;
    private WorldGenerator oreDirt;
    private WorldGenerator oreGravel;
    private WorldGenerator oreCoal;
    private WorldGenerator oreIron;
    private WorldGenerator oreGold;
    private WorldGenerator oreRedstone;
    private WorldGenerator oreDiamond;
    private WorldGenerator oreLapis;
    
    private WorldGenerator oreGranite;
    private WorldGenerator oreDiorite;
    private WorldGenerator oreAndesite;
    private WorldGenerator oreEmerald;
    
    protected abstract int getTreeCount(World world, Random random, Biome biome, BlockPos startPos);
    
    protected void populateOres(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(world);
        boolean useOldOres = settings.useOldOres;
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        ChunkPos chunkPos = new ChunkPos(startX >> 4, startZ >> 4);
        
        // Ore generators
        this.oreClay = new WorldGenClay(settings.claySize);
        this.oreDirt = new WorldGenMinableMutable(BlockStates.DIRT, settings.dirtSize, useOldOres);
        this.oreGravel = new WorldGenMinableMutable(BlockStates.GRAVEL, settings.gravelSize, useOldOres);
        this.oreCoal = new WorldGenMinableMutable(BlockStates.COAL_ORE, settings.coalSize, useOldOres);
        this.oreIron = new WorldGenMinableMutable(BlockStates.IRON_ORE, settings.ironSize, useOldOres);
        this.oreGold = new WorldGenMinableMutable(BlockStates.GOLD_ORE, settings.goldSize, useOldOres);
        this.oreRedstone = new WorldGenMinableMutable(BlockStates.REDSTONE_ORE, settings.redstoneSize, useOldOres);
        this.oreDiamond = new WorldGenMinableMutable(BlockStates.DIAMOND_ORE, settings.diamondSize, useOldOres);
        this.oreLapis = new WorldGenMinableMutable(BlockStates.LAPIS_ORE, settings.lapisSize, useOldOres);
        
        // New mineable generators
        this.oreGranite = new WorldGenMinableMutable(BlockStates.GRANITE, settings.graniteSize, useOldOres);
        this.oreDiorite = new WorldGenMinableMutable(BlockStates.DIORITE, settings.dioriteSize, useOldOres);
        this.oreAndesite = new WorldGenMinableMutable(BlockStates.ANDESITE, settings.andesiteSize, useOldOres);
        this.oreEmerald = new WorldGenMinableMutable(BlockStates.EMERALD_ORE, settings.emeraldSize, useOldOres);
        
        if (TerrainGen.generateOre(world, random, this.oreClay, startPos, OreGenEvent.GenerateMinable.EventType.CUSTOM)) {
            populateOreStandard(world, random, startPos, this.oreClay, mutablePos, settings.clayCount, settings.clayMinHeight, settings.clayMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.oreDirt, startPos, OreGenEvent.GenerateMinable.EventType.DIRT)) {
            populateOreStandard(world, random, startPos, this.oreDirt, mutablePos, settings.dirtCount, settings.dirtMinHeight, settings.dirtMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreGravel, startPos, OreGenEvent.GenerateMinable.EventType.GRAVEL)) {
            populateOreStandard(world, random, startPos, this.oreGravel, mutablePos, settings.gravelCount, settings.gravelMinHeight, settings.gravelMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.oreDiorite, startPos, OreGenEvent.GenerateMinable.EventType.DIORITE)) {
            populateOreStandard(world, random, startPos, this.oreDiorite, mutablePos, settings.dioriteCount, settings.dioriteMinHeight, settings.dioriteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.oreGranite, startPos, OreGenEvent.GenerateMinable.EventType.GRANITE)) {
            populateOreStandard(world, random, startPos, this.oreGranite, mutablePos, settings.graniteCount, settings.graniteMinHeight, settings.graniteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.oreAndesite, startPos, OreGenEvent.GenerateMinable.EventType.ANDESITE)) {
            populateOreStandard(world, random, startPos, this.oreAndesite, mutablePos, settings.andesiteCount, settings.andesiteMinHeight, settings.andesiteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.oreCoal, startPos, OreGenEvent.GenerateMinable.EventType.COAL)) {
            populateOreStandard(world, random, startPos, this.oreCoal, mutablePos, settings.coalCount, settings.coalMinHeight, settings.coalMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreIron, startPos, OreGenEvent.GenerateMinable.EventType.IRON)) {
            populateOreStandard(world, random, startPos, this.oreIron, mutablePos, settings.ironCount, settings.ironMinHeight, settings.ironMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreGold, startPos, OreGenEvent.GenerateMinable.EventType.GOLD)) {
            populateOreStandard(world, random, startPos, this.oreGold, mutablePos, settings.goldCount, settings.goldMinHeight, settings.goldMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreRedstone, startPos, OreGenEvent.GenerateMinable.EventType.REDSTONE)) {
            populateOreStandard(world, random, startPos, this.oreRedstone, mutablePos, settings.redstoneCount, settings.redstoneMinHeight, settings.redstoneMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreDiamond, startPos, OreGenEvent.GenerateMinable.EventType.DIAMOND)) {
            populateOreStandard(world, random, startPos, this.oreDiamond, mutablePos, settings.diamondCount, settings.diamondMinHeight, settings.diamondMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.oreLapis, startPos, OreGenEvent.GenerateMinable.EventType.LAPIS)) {
            populateOreSpread(world, random, startPos, this.oreLapis, mutablePos, settings.lapisCount, settings.lapisCenterHeight, settings.lapisSpread);
        }

        if (TerrainGen.generateOre(world, random, this.oreEmerald, startPos, OreGenEvent.GenerateMinable.EventType.EMERALD)) {
            populateOreStandard(world, random, startPos, this.oreEmerald, mutablePos, settings.emeraldCount, settings.emeraldMinHeight, settings.emeraldMaxHeight);
        }

        if (settings.useSandDisks && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SAND)) {
            populateDisks(world, random, startPos, this.sandGen, mutablePos, this.sandPatchesPerChunk);
        }

        if (settings.useClayDisks && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.CLAY)) {
            populateDisks(world, random, startPos, this.clayGen, mutablePos, this.clayPerChunk);
        }

        if (settings.useGravelDisks && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.SAND_PASS2)) {
            populateDisks(world, random, startPos, this.gravelGen, mutablePos, this.gravelPatchesPerChunk);
        }
    }
    
    protected void populateTrees(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, Supplier<WorldGenAbstractTree> treeFunc) {
        int treeCount = this.getTreeCount(world, random, biome, startPos);
        
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < treeCount; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int z = startZ + random.nextInt(16) + 8;
            
            WorldGenAbstractTree worldGenTree = treeFunc.get();
            worldGenTree.setDecorationDefaults();
            
            BlockPos treePos = world.getHeight(mutablePos.setPos(x, 0, z));
            if (worldGenTree.generate(world, random, treePos)) {
                worldGenTree.generateSaplings(world, random, treePos);
            }
        }
    }
    
    protected void populateWaterfalls(World world, Random random, BlockPos startPos, MutableBlockPos mutablePos, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(world);
        Block fluidBlock = ForgeRegistryUtil.getFluid(settings.defaultFluid).getBlock();
        
        if (fluidBlock == null || VANILLA_FLUIDS.contains(fluidBlock)) {
            fluidBlock = Blocks.FLOWING_WATER;
        }
        
        WorldGenerator worldGenLiquids = new WorldGenLiquids(fluidBlock);
        for (int i = 0; i < 50; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(random.nextInt(height - 8) + 8);
            int z = startZ + random.nextInt(16) + 8;
            
            worldGenLiquids.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    protected void populateLavafalls(World world, Random random, BlockPos startPos, MutableBlockPos mutablePos, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();

        for (int i = 0; i < 20; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(random.nextInt(random.nextInt(height - 16) + 8) + 8);
            int z = startZ + random.nextInt(16) + 8;
            
            FEATURE_LAVA_FALL.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    protected PerlinOctaveNoise getForestOctaveNoise(World world, int chunkX, int chunkZ) {
        ChunkProviderServer chunkProviderServer = (ChunkProviderServer)world.getChunkProvider();
        IChunkGenerator chunkGenerator = chunkProviderServer.chunkGenerator;
        Random random = new Random(world.getSeed());
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ModernBetaChunkGenerator modernBetaChunkGenerator = (ModernBetaChunkGenerator)chunkGenerator;
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();

            return chunkSource.getForestOctaveNoise().orElse(new PerlinOctaveNoise(random, 8, true));
        }
        
        return new PerlinOctaveNoise(random, 8, true);
    }
    
    public static void populateWorldGenCount(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateWorldGenChance(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int chance, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        if (random.nextInt(chance) == 0) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateOreStandard(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int minHeight, int maxHeight) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; i++) {
            int x = startX + random.nextInt(16);
            int y = getOreHeight(random, minHeight, maxHeight);
            int z = startZ + random.nextInt(16);
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateOreSpread(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int centerHeight, int spread) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; i++) {
            int x = startX + random.nextInt(16);
            int y = getOreHeightSpread(random, centerHeight, spread);
            int z = startZ + random.nextInt(16);
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateDisks(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int z = startZ + random.nextInt(16) + 8;
            
            generator.generate(world, random, world.getTopSolidOrLiquidBlock(mutablePos.setPos(x, 0, z)));
        }
    }
    
    public static void populateTallGrassCount(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, int count, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; ++i) {
            BlockTallGrass.EnumType tallGrassType = BlockTallGrass.EnumType.GRASS;
            if (biome instanceof BiomeBetaRainforest && random.nextInt(3) != 0) {
                tallGrassType = BlockTallGrass.EnumType.FERN;
            }
            
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            new WorldGenTallGrass(tallGrassType).generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateTallGrassChance(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, int chance, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        if (random.nextInt(chance) == 0) {
            BlockTallGrass.EnumType tallGrassType = BlockTallGrass.EnumType.GRASS;
            if (biome instanceof BiomeBetaRainforest && random.nextInt(3) != 0) {
                tallGrassType = BlockTallGrass.EnumType.FERN;
            }
            
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            new WorldGenTallGrass(tallGrassType).generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateWaterLakes(World world, Random random, ModernBetaGeneratorSettings settings, MutableBlockPos mutablePos, int chunkX, int chunkZ, IBlockState defaultFluid) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        Block fluidBlock = ForgeRegistryUtil.getFluid(settings.defaultFluid).getBlock();
        
        if (fluidBlock == null || VANILLA_FLUIDS.contains(fluidBlock)) {
            fluidBlock = Blocks.WATER;
        }
        
        WorldGenerator worldGenLakes = new WorldGenLakes(fluidBlock);
        if (random.nextInt(settings.waterLakeChance) == 0) { // Default: 4
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(settings.height);
            int z = startZ + random.nextInt(16) + 8;
            
            worldGenLakes.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateLavaLakes(World world, Random random, ModernBetaGeneratorSettings settings, MutableBlockPos mutablePos, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        if (random.nextInt(settings.lavaLakeChance / 10) == 0) { // Default: 80 / 10 = 8
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(random.nextInt(settings.height - 8) + 8);
            int z = startZ + random.nextInt(16) + 8;
            
            if (y < 64 || random.nextInt(10) == 0) {
                FEATURE_LAVA_LAKES.generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
    }
    
    public static void populateDungeons(World world, Random random, ModernBetaGeneratorSettings settings, MutableBlockPos mutablePos, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        for (int i = 0; i < settings.dungeonChance; i++) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(settings.height);
            int z = startZ + random.nextInt(16) + 8;
            
            FEATURE_DUNGEONS.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateSnowIce(World world, Random random, ModernBetaBiomeProvider biomeProvider, MutableBlockPos mutablePos, int chunkX, int chunkZ, int snowLineOffset) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        BiomeSource biomeSource = biomeProvider.getBiomeSource();
        
        for(int localX = 0; localX < 16; localX++) {
            for(int localZ = 0; localZ < 16; localZ++) {
                // Adding 8 is important to prevent runaway chunk loading
                int x = localX + startX + 8;
                int z = localZ + startZ + 8;
                int y = world.getPrecipitationHeight(mutablePos.setPos(x, 0, z)).getY();

                Biome biome = biomeProvider.getBiome(mutablePos);
                BlockPos blockPosDown = mutablePos.setPos(x, y, z).down();
                
                boolean canSetIce = false;
                boolean canSetSnow = false;
                
                if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleForFeatureGeneration()) {
                    double temp = ((ClimateSampler)biomeSource).sample(x, z).temp();
                    temp = temp - ((double)(y - snowLineOffset) / (double)snowLineOffset) * 0.3;
                    
                    canSetIce = BiomeBeta.canSetIceBeta(world, blockPosDown, false, temp);
                    canSetSnow = BiomeBeta.canSetSnowBeta(world, mutablePos, temp);
                    
                } else if (biome instanceof ModernBetaBiome) {
                    ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
                    double temp = (double)biome.getDefaultTemperature();
                    
                    canSetIce = modernBetaBiome.canSetIce(world, blockPosDown, false, temp);
                    canSetSnow = modernBetaBiome.canSetSnow(world, mutablePos, temp);
                    
                } else {
                    canSetIce = world.canBlockFreezeWater(blockPosDown);
                    canSetSnow = world.canSnowAt(mutablePos, true);
                    
                }
                
                if (canSetIce) world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                if (canSetSnow) world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
            }
        }
    }

    private static int getOreHeight(Random random, int minHeight, int maxHeight) {
        if (maxHeight < minHeight) {
            int height = minHeight;
            
            minHeight = maxHeight;
            maxHeight = height;
        } else if (maxHeight == minHeight) {
            if (minHeight < 255) {
                ++maxHeight;
            } else {
                --minHeight;
            }
        }
        
        return random.nextInt(maxHeight - minHeight) + minHeight;
    }

    private static int getOreHeightSpread(Random random, int centerHeight, int spread) {
        return random.nextInt(spread) + random.nextInt(spread) + centerHeight - spread;
    }
}
