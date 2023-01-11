package mod.bespectacled.modernbetaforge.api.world.gen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjector;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCave;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaMapGenScatteredFeature;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaStructureOceanMonument;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaWoodlandMansion;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class ChunkSource {
    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private static final Set<Biome> BIOMES_WITH_CUSTOM_SURFACES = new HashSet<Biome>(
        ModernBetaBiomeLists.BIOMES_WITH_CUSTOM_SURFACES
    );
    
    protected final ModernBetaChunkGenerator chunkGenerator;
    protected final ModernBetaChunkGeneratorSettings settings;
    
    protected final World world;
    protected final long seed;
    protected final boolean mapFeaturesEnabled;
    protected final Random random;

    private final MapGenBase caveCarver;
    private final MapGenBase ravineCarver; 
    
    private final MapGenStronghold strongholdGenerator;
    private final MapGenVillage villageGenerator;
    private final MapGenMineshaft mineshaftGenerator;
    private final ModernBetaMapGenScatteredFeature scatteredFeatureGenerator;
    private final ModernBetaStructureOceanMonument oceanMonumentGenerator;
    private final ModernBetaWoodlandMansion woodlandMansionGenerator;
    
    private final Biome[] biomes;
    private final BiomeInjector biomeInjector;
    private final SimplexOctaveNoise surfaceOctaveNoise;
    
    public ChunkSource(World world, ModernBetaChunkGenerator chunkGenerator, ModernBetaChunkGeneratorSettings chunkGeneratorSettings, long seed, boolean mapFeaturesEnabled) {
        this.chunkGenerator = chunkGenerator;
        this.settings = chunkGeneratorSettings;
        
        this.world = world;
        this.seed = seed;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        this.random = new Random(seed);

        this.caveCarver = TerrainGen.getModdedMapGen(new MapGenBetaCave(), InitMapGenEvent.EventType.CAVE);
        this.ravineCarver = TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE);
        
        this.strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(new MapGenStronghold(), InitMapGenEvent.EventType.STRONGHOLD);
        this.villageGenerator = (MapGenVillage)TerrainGen.getModdedMapGen(new MapGenVillage(), InitMapGenEvent.EventType.VILLAGE);
        this.mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(new MapGenMineshaft(), InitMapGenEvent.EventType.MINESHAFT);
        this.scatteredFeatureGenerator = (ModernBetaMapGenScatteredFeature)TerrainGen.getModdedMapGen(new ModernBetaMapGenScatteredFeature(), InitMapGenEvent.EventType.SCATTERED_FEATURE);
        this.oceanMonumentGenerator = (ModernBetaStructureOceanMonument)TerrainGen.getModdedMapGen(new ModernBetaStructureOceanMonument(this), InitMapGenEvent.EventType.OCEAN_MONUMENT);
        this.woodlandMansionGenerator = (ModernBetaWoodlandMansion)TerrainGen.getModdedMapGen(new ModernBetaWoodlandMansion(chunkGenerator), InitMapGenEvent.EventType.WOODLAND_MANSION);
        
        this.biomes = new Biome[256];
        this.biomeInjector = world.getBiomeProvider() instanceof ModernBetaBiomeProvider ? 
            new BiomeInjector(this, ((ModernBetaBiomeProvider)world.getBiomeProvider()).getBiomeSource()) :
            null;
        this.surfaceOctaveNoise = new SimplexOctaveNoise(new Random(seed), 4);

        // Important for correct structure spawning when y < seaLevel, e.g. villages
        world.setSeaLevel(this.settings.seaLevel);
    }
    
    public abstract void provideBaseChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract int getHeight(int x, int z, HeightmapChunk.Type type);
    
    public Chunk provideChunk(World world, int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        
        // Generate base terrain
        this.provideBaseChunk(chunkPrimer, chunkX, chunkZ);
        
        // Generate biome map
        world.getBiomeProvider().getBiomes(this.biomes, startX, startZ, 16, 16);
        
        // Populate biome-specific surface
        this.provideSurface(this.biomes, chunkPrimer, chunkX, chunkZ);
        
        // Post-process biome map
        if (this.biomeInjector != null) {
            this.biomeInjector.injectBiomes(this.biomes, chunkPrimer, chunkX, chunkZ);
        }
        
        // Carve terrain
        if (this.settings.useCaves) {
            this.caveCarver.generate(world, chunkX, chunkZ, chunkPrimer);
        }
        
        if (this.settings.useRavines) {
            this.ravineCarver.generate(world, chunkX, chunkZ, chunkPrimer);
        }
        
        // Generate map feature placements
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useVillages) {
                this.villageGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useMansions) {
                this.woodlandMansionGenerator.generate(world, chunkX, chunkZ, chunkPrimer);
            }
        }
        
        // Generate final chunk
        Chunk chunk = new Chunk(world, chunkPrimer, chunkX, chunkZ);
        
        // Set biome map in chunk
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
           biomeArray[i] = (byte)Biome.getIdForBiome(this.biomes[i]); 
        }
        
        chunk.generateSkylightMap();
        return chunk;
    }
    
    public void populateChunk(World world, int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        boolean hasVillageGenerated = false;
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        Biome biome = world.getBiome(new BlockPos(startX + 16, 0, startZ + 16));
        
        this.random.setSeed(world.getSeed());
        long randomLong0 = (random.nextLong() / 2L) * 2L + 1L;
        long randomLong1 = (random.nextLong() / 2L) * 2L + 1L;
        this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ world.getSeed());

        ForgeEventFactory.onChunkPopulate(true, this.chunkGenerator, world, this.random, chunkX, chunkZ, false);
        
        // Actually generate map features here
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generateStructure(world, this.random, chunkPos);
            }
            
            if (this.settings.useVillages) {
                hasVillageGenerated = this.villageGenerator.generateStructure(world, this.random, chunkPos);
            }
            
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generateStructure(world, this.random, chunkPos);
            }
            
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generateStructure(world, this.random, chunkPos);
            }
            
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generateStructure(world, this.random, chunkPos);
            }
            
            if (this.settings.useMansions) {
                this.woodlandMansionGenerator.generateStructure(world, this.random, chunkPos);
            }
        }
        
        // Reset seed for generation accuracy
        this.random.setSeed(world.getSeed());
        randomLong0 = (this.random.nextLong() / 2L) * 2L + 1L;
        randomLong1 = (this.random.nextLong() / 2L) * 2L + 1L;
        this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ world.getSeed());
        
        // Generate lakes, dungeons
        
        if (!hasVillageGenerated &&
            this.settings.useWaterLakes && 
            TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAKE)
        ) {
            if (random.nextInt(this.settings.waterLakeChance) == 0) { // Default: 4
                int x = startX + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(128);
                int z = startZ + this.random.nextInt(16) + 8;
                
                (new WorldGenLakes(Blocks.WATER)).generate(world, this.random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (!hasVillageGenerated &&
            this.settings.useLavaLakes &&
            TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAVA)
        ) {
            if (random.nextInt(this.settings.lavaLakeChance / 10) == 0) { // Default: 80 / 10 = 8
                int x = startX + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(this.random.nextInt(120) + 8);
                int z = startZ + this.random.nextInt(16) + 8;
                
                if (y < 64 || this.random.nextInt(10) == 0) {
                    (new WorldGenLakes(Blocks.LAVA)).generate(world, this.random, mutablePos.setPos(x, y, z));
                }
            }
        }
        
        if (this.settings.useDungeons && 
            TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.DUNGEON)
        ) {
            for (int i = 0; i < this.settings.dungeonChance; i++) {
                int x = startX + this.random.nextInt(16) + 8;
                int y = this.random.nextInt(128);
                int z = startZ + this.random.nextInt(16) + 8;
                
                new WorldGenDungeons().generate(world, this.random, mutablePos.setPos(x, y, z));
            }
        }
        
        // Generate biome decorations
        biome.decorate(world, this.random, new BlockPos(startX, 0, startZ));
        
        // Generate animals
        if (TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            WorldEntitySpawner.performWorldGenSpawning(world, biome, startX + 8, startZ + 8, 16, 16, this.random);
        }
        
        // Get biome provider to check for climate sampler
        ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();

        // Generate snow / ice
        if (TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ICE)) {
            for(int dX = 0; dX < 16; dX++) {
                for(int dZ = 0; dZ < 16; dZ++) {
                    // Adding 8 is important to prevent runaway chunk loading
                    int x = dX + startX + 8; 
                    int z = dZ + startZ + 8;
                    int y = world.getPrecipitationHeight(mutablePos.setPos(x, 0, z)).getY();

                    BlockPos blockPosDown = mutablePos.setPos(x, y, z).down();
                    
                    if (biomeProvider.getBiomeSource() instanceof ClimateSampler) {
                        ClimateSampler climateSampler = (ClimateSampler)biomeProvider.getBiomeSource();
                        
                        double temp = climateSampler.sample(x, z).temp();
                        temp = temp - ((double)(y - 64) / 64D) * 0.3D;
                        
                        if (BiomeBeta.canSetIce(world, blockPosDown, false, temp)) {
                            world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                        }
                        
                        if (BiomeBeta.canSetSnow(world, mutablePos, temp)) {
                            world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    } else {
                        if (world.canBlockFreezeWater(blockPosDown)) {
                            world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                        }
                        
                        if (world.canSnowAt(mutablePos, true)) {
                            world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        
        ForgeEventFactory.onChunkPopulate(false, this.chunkGenerator, world, this.random, chunkX, chunkZ, false);
        
        BlockFalling.fallInstantly = false;
    }
    
    public boolean generateStructures(World world, Chunk chunk, int chunkX, int chunkZ) {
        boolean generated = false;
        
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunk.getInhabitedTime() < 3600L) {
            generated |= this.oceanMonumentGenerator.generateStructure(world, this.random, new ChunkPos(chunkX, chunkZ));
        }
        
        return generated;
    }
    
    public List<Biome.SpawnListEntry> getPossibleCreatures(World world, EnumCreatureType enumCreatureType, BlockPos blockPos) {
        Biome biome = world.getBiome(blockPos);
        if (this.mapFeaturesEnabled) {
            if (enumCreatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(blockPos)) {
                return this.scatteredFeatureGenerator.getMonsters();
            }
            
            if (enumCreatureType == EnumCreatureType.MONSTER &&
                this.settings.useMonuments &&
                this.oceanMonumentGenerator.isPositionInStructure(world, blockPos)
            ) {
                return this.oceanMonumentGenerator.getMonsters();
            }
        }
        return biome.getSpawnableList(enumCreatureType);
    }
    
    public boolean isInsideStructure(World world, String structureName, BlockPos blockPos) {
        if (!this.mapFeaturesEnabled) {
            return false;
        }
        
        if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.isInsideStructure(blockPos);
        }
        
        if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
            return this.woodlandMansionGenerator.isInsideStructure(blockPos);
        }
        
        if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.isInsideStructure(blockPos);
        }
        
        if ("Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.isInsideStructure(blockPos);
        }
        
        if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.isInsideStructure(blockPos);
        }
        
        return "Temple".equals(structureName) && this.scatteredFeatureGenerator != null && this.scatteredFeatureGenerator.isInsideStructure(blockPos);
    }
    
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos blockPos, boolean findUnexplored) {
        if (!this.mapFeaturesEnabled) {
            return null;
        }
        
        if ("Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if ("Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
            return this.woodlandMansionGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if ("Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if ("Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if ("Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        return ("Temple".equals(structureName) && this.scatteredFeatureGenerator != null) ? this.scatteredFeatureGenerator.getNearestStructurePos(world, blockPos, findUnexplored) : null;
    }
    
    public void recreateStructures(World world, Chunk chunk, int chunkX, int chunkZ) {
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generate(world, chunkX, chunkZ, null);
            }
            
            if (this.settings.useVillages) {
                this.villageGenerator.generate(world, chunkX, chunkZ, null);
            }
            
            if (this.settings.useStrongholds) {
                this.strongholdGenerator.generate(world, chunkX, chunkZ, null);
            }
            
            if (this.settings.useTemples) {
                this.scatteredFeatureGenerator.generate(world, chunkX, chunkZ, null);
            }
            
            if (this.settings.useMonuments) {
                this.oceanMonumentGenerator.generate(world, chunkX, chunkZ, null);
            }
            
            if (this.settings.useMansions) {
                this.woodlandMansionGenerator.generate(world, chunkX, chunkZ, null);
            }
        }
    }
    
    public int getSeaLevel() {
        return this.settings.seaLevel;
    }
    
    public Biome getInjectedBiomeAtBlock(int x, int z) {
        if (this.biomeInjector != null) {
            return this.biomeInjector.sample(x, z);
        }
        
        return null;
    }
    
    public ModernBetaChunkGeneratorSettings getChunkGeneratorSettings() {
        return this.settings;
    }
    
    /**
     * Get a new Random object initialized with chunk coordinates for seed, for surface generation.
     * 
     * @param chunkX x-coordinate in chunk coordinates.
     * @param chunkZ z-coordinate in chunk coordinates.
     * 
     * @return New Random object initialized with chunk coordinates for seed.
     */
    protected Random createSurfaceRandom(int chunkX, int chunkZ) {
        long seed = (long)chunkX * 0x4f9939f508L + (long)chunkZ * 0x1ef1565bd5L;
        
        return new Random(seed);
    }
    
    /**
     * Use a biome-specific surface builder, at a given x/z-coordinate and topmost y-coordinate.
     * Valid biomes are checked on per-biome basis using identifier from BIOMES_WITH_CUSTOM_SURFACES set. 
     * 
     * @param biome Biome with surface builder to use.
     * @param biomeId Biome identifier, used to check if it uses valid custom surface builder.
     * @param region
     * @param chunk
     * @param random
     * @param mutable Mutable BlockPos at block coordinates position.
     * 
     * @return True if biome is included in valid biomes set and has run surface builder. False if not included and not run.
     */
    protected boolean useCustomSurfaceBuilder(Biome biome, ChunkPrimer chunkPrimer, Random random, int x, int z) {
        if (BIOMES_WITH_CUSTOM_SURFACES.contains(biome)) {
            double surfaceNoise = this.surfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(this.world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }
    
    static {
        BIOMES_WITH_CUSTOM_SURFACES.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(str -> ForgeRegistries.BIOMES.getValue(new ResourceLocation(str)))
                .collect(Collectors.toList())
        );
    }
}
