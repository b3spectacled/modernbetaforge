package mod.bespectacled.modernbetaforge.world.biome;

import java.util.Random;
import java.util.function.Supplier;

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
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class ModernBetaBiomeDecorator extends BiomeDecorator {
    private static final WorldGenerator WORLD_GEN_LAVA_LAKES = new WorldGenLakes(Blocks.LAVA);
    private static final WorldGenerator WORLD_GEN_DUNGEONS = new WorldGenDungeons();
    
    private final WorldGenerator worldGenLavafall = new WorldGenLiquids(Blocks.FLOWING_LAVA);
    
    private WorldGenerator worldGenClay;
    private WorldGenerator worldGenDirt;
    private WorldGenerator worldGenGravel;
    private WorldGenerator worldGenCoal;
    private WorldGenerator worldGenIron;
    private WorldGenerator worldGenGold;
    private WorldGenerator worldGenRedstone;
    private WorldGenerator worldGenDiamond;
    private WorldGenerator worldGenLapis;
    
    private WorldGenerator worldGenGranite;
    private WorldGenerator worldGenDiorite;
    private WorldGenerator worldGenAndesite;
    private WorldGenerator worldGenEmerald;
    
    protected abstract int getTreeCount(World world, Random random, Biome biome, BlockPos startPos);
    
    protected void populateOres(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos) {
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.buildOrGet(world);
        
        // Ore generators
        this.worldGenClay = new WorldGenClay(settings.claySize);
        this.worldGenDirt = new WorldGenMinable(BlockStates.DIRT, settings.dirtSize);
        this.worldGenGravel = new WorldGenMinable(BlockStates.GRAVEL, settings.gravelSize);
        this.worldGenCoal = new WorldGenMinable(BlockStates.COAL_ORE, settings.coalSize);
        this.worldGenIron = new WorldGenMinable(BlockStates.IRON_ORE, settings.ironSize);
        this.worldGenGold = new WorldGenMinable(BlockStates.GOLD_ORE, settings.goldSize);
        this.worldGenRedstone = new WorldGenMinable(BlockStates.REDSTONE_ORE, settings.redstoneSize);
        this.worldGenDiamond = new WorldGenMinable(BlockStates.DIAMOND_ORE, settings.diamondSize);
        this.worldGenLapis = new WorldGenMinable(BlockStates.LAPIS_ORE, settings.lapisSize);
        
        // New mineable generators
        this.worldGenGranite = new WorldGenMinable(BlockStates.GRANITE, settings.graniteSize);
        this.worldGenDiorite = new WorldGenMinable(BlockStates.DIORITE, settings.dioriteSize);
        this.worldGenAndesite = new WorldGenMinable(BlockStates.ANDESITE, settings.andesiteSize);
        this.worldGenEmerald = new WorldGenMinable(BlockStates.EMERALD_ORE, settings.emeraldSize);
        
        if (TerrainGen.generateOre(world, random, this.worldGenClay, startPos, OreGenEvent.GenerateMinable.EventType.CUSTOM)) {
            this.populateOreStandard(world, random, startPos, this.worldGenClay, mutablePos, settings.clayCount, settings.clayMinHeight, settings.clayMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.worldGenDirt, startPos, OreGenEvent.GenerateMinable.EventType.DIRT)) {
            this.populateOreStandard(world, random, startPos, this.worldGenDirt, mutablePos, settings.dirtCount, settings.dirtMinHeight, settings.dirtMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenGravel, startPos, OreGenEvent.GenerateMinable.EventType.GRAVEL)) {
            this.populateOreStandard(world, random, startPos, this.worldGenGravel, mutablePos, settings.gravelCount, settings.gravelMinHeight, settings.gravelMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.worldGenDiorite, startPos, OreGenEvent.GenerateMinable.EventType.DIORITE)) {
            this.populateOreStandard(world, random, startPos, this.worldGenDiorite, mutablePos, settings.dioriteCount, settings.dioriteMinHeight, settings.dioriteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.worldGenGranite, startPos, OreGenEvent.GenerateMinable.EventType.GRANITE)) {
            this.populateOreStandard(world, random, startPos, this.worldGenGranite, mutablePos, settings.graniteCount, settings.graniteMinHeight, settings.graniteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.worldGenAndesite, startPos, OreGenEvent.GenerateMinable.EventType.ANDESITE)) {
            this.populateOreStandard(world, random, startPos, this.worldGenAndesite, mutablePos, settings.andesiteCount, settings.andesiteMinHeight, settings.andesiteMaxHeight);
        }
        
        if (TerrainGen.generateOre(world, random, this.worldGenCoal, startPos, OreGenEvent.GenerateMinable.EventType.COAL)) {
            this.populateOreStandard(world, random, startPos, this.worldGenCoal, mutablePos, settings.coalCount, settings.coalMinHeight, settings.coalMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenIron, startPos, OreGenEvent.GenerateMinable.EventType.IRON)) {
            this.populateOreStandard(world, random, startPos, this.worldGenIron, mutablePos, settings.ironCount, settings.ironMinHeight, settings.ironMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenGold, startPos, OreGenEvent.GenerateMinable.EventType.GOLD)) {
            this.populateOreStandard(world, random, startPos, this.worldGenGold, mutablePos, settings.goldCount, settings.goldMinHeight, settings.goldMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenRedstone, startPos, OreGenEvent.GenerateMinable.EventType.REDSTONE)) {
            this.populateOreStandard(world, random, startPos, this.worldGenRedstone, mutablePos, settings.redstoneCount, settings.redstoneMinHeight, settings.redstoneMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenDiamond, startPos, OreGenEvent.GenerateMinable.EventType.DIAMOND)) {
            this.populateOreStandard(world, random, startPos, this.worldGenDiamond, mutablePos, settings.diamondCount, settings.diamondMinHeight, settings.diamondMaxHeight);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenLapis, startPos, OreGenEvent.GenerateMinable.EventType.LAPIS)) {
            this.populateOreSpread(world, random, startPos, this.worldGenLapis, mutablePos, settings.lapisCount, settings.lapisCenterHeight, settings.lapisSpread);
        }

        if (TerrainGen.generateOre(world, random, this.worldGenEmerald, startPos, OreGenEvent.GenerateMinable.EventType.EMERALD)) {
            this.populateOreStandard(world, random, startPos, this.worldGenEmerald, mutablePos, settings.emeraldCount, settings.emeraldMinHeight, settings.emeraldMaxHeight);
        }
    }
    
    protected void populateWorldGenCount(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; ++i) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    protected void populateWorldGenChance(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int chance, int height) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        if (random.nextInt(chance) == 0) {
            int x = startX + random.nextInt(16) + 8;
            int y = random.nextInt(height);
            int z = startZ + random.nextInt(16) + 8;
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    protected void populateTallGrassCount(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, int count, int height) {
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
    
    protected void populateTallGrassChance(World world, Random random, Biome biome, BlockPos startPos, MutableBlockPos mutablePos, int chance, int height) {
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
        
        if (fluidBlock == null || fluidBlock == Blocks.WATER || fluidBlock == Blocks.FLOWING_WATER) {
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
            
            this.worldGenLavafall.generate(world, random, mutablePos.setPos(x, y, z));
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
    
    private void populateOreStandard(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int minHeight, int maxHeight) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; i++) {
            int x = startX + random.nextInt(16);
            int y = this.getOreHeight(random, minHeight, maxHeight);
            int z = startZ + random.nextInt(16);
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    private void populateOreSpread(World world, Random random, BlockPos startPos, WorldGenerator generator, MutableBlockPos mutablePos, int count, int centerHeight, int spread) {
        int startX = startPos.getX();
        int startZ = startPos.getZ();
        
        for (int i = 0; i < count; i++) {
            int x = startX + random.nextInt(16);
            int y = this.getOreHeightSpread(random, centerHeight, spread);
            int z = startZ + random.nextInt(16);
            
            generator.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }

    private int getOreHeight(Random random, int minHeight, int maxHeight) {
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

    private int getOreHeightSpread(Random random, int centerHeight, int spread) {
        return random.nextInt(spread) + random.nextInt(spread) + centerHeight - spread;
    }
    
    public static void populateWaterLakes(World world, Random random, ModernBetaGeneratorSettings settings, MutableBlockPos mutablePos, int chunkX, int chunkZ, IBlockState defaultFluid) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        WorldGenerator worldGenLakes = new WorldGenLakes(defaultFluid.getBlock());
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
                WORLD_GEN_LAVA_LAKES.generate(world, random, mutablePos.setPos(x, y, z));
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
            
            WORLD_GEN_DUNGEONS.generate(world, random, mutablePos.setPos(x, y, z));
        }
    }
    
    public static void populateSnowIce(World world, Random random, ModernBetaBiomeProvider biomeProvider, MutableBlockPos mutablePos, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        int seaLevel = 64;
        
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
                    temp = temp - ((double)(y - seaLevel) / (double)seaLevel) * 0.3;
                    
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
}
