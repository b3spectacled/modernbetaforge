package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.chunk.blocksource.BlockSource;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ComponentChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeMobs;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjector;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class ChunkSource {
    private static final int MAX_RENDER_DISTANCE_AREA = 1024;
    
    protected static final int OCEAN_MIN_DEPTH = 4;
    protected static final int DEEP_OCEAN_MIN_DEPTH = 16;
    
    protected final ModernBetaChunkGenerator chunkGenerator;
    protected final ModernBetaGeneratorSettings settings;
    protected final ModernBetaBiomeProvider biomeProvider;
    
    protected final World world;
    protected final long seed;
    protected final boolean mapFeaturesEnabled;
    protected final Random random;
    
    protected final IBlockState defaultBlock;
    protected final IBlockState defaultFluid;

    protected final int worldHeight;
    protected final int seaLevel;
    
    protected final List<BlockSource> blockSources;
    
    protected final MapGenVillage villageGenerator;
    protected final ChunkCache<ComponentChunk> componentCache;
    
    private final MapGenBase caveCarver;
    private final MapGenBase ravineCarver; 
    
    private final MapGenStronghold strongholdGenerator;
    private final MapGenMineshaft mineshaftGenerator;
    
    private final MapGenScatteredFeature scatteredFeatureGenerator;
    private final StructureOceanMonument oceanMonumentGenerator;
    private final WoodlandMansion woodlandMansionGenerator;

    private final BiomeInjector biomeInjector;
    private final ChunkCache<ChunkPrimerContainer> initialChunkCache;
    
    private Optional<PerlinOctaveNoise> beachOctaveNoise;
    private Optional<PerlinOctaveNoise> surfaceOctaveNoise;
    private Optional<PerlinOctaveNoise> forestOctaveNoise;
    
    /**
     * Constructs an abstract ChunkSource to hold basic underlying terrain generation information.
     * 
     * @param world The world.
     * @param chunkGenerator The ModernBetaChunkGenerator which hooks into this for terrain generation.
     * @param chunkGeneratorSettings The generator settings.
     */
    public ChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaGeneratorSettings chunkGeneratorSettings
    ) {
        this.chunkGenerator = chunkGenerator;
        this.settings = chunkGeneratorSettings;
        this.biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
        
        this.world = world;
        this.seed = world.getSeed();
        this.mapFeaturesEnabled = world.getWorldInfo().isMapFeaturesEnabled();
        this.random = new Random(seed);
        
        this.defaultBlock = BlockStates.STONE;
        this.defaultFluid = settings.useLavaOceans ? BlockStates.LAVA : BlockStates.WATER;
        
        this.worldHeight = settings.height;
        this.seaLevel = settings.seaLevel;
        
        this.blockSources = ModernBetaRegistries.BLOCK
            .getEntries()
            .stream()
            .map(e -> e.apply(this.world, this, this.settings))
            .collect(Collectors.toList());
        
        this.villageGenerator = (MapGenVillage)TerrainGen.getModdedMapGen(new MapGenVillage(), InitMapGenEvent.EventType.VILLAGE);
        this.componentCache = new ChunkCache<>("structure_components", MAX_RENDER_DISTANCE_AREA + MAX_RENDER_DISTANCE_AREA / 2, ComponentChunk::new);

        this.caveCarver = TerrainGen.getModdedMapGen(ModernBetaRegistries.CARVER.get(new ResourceLocation(this.settings.caveCarver)).apply(this.world, this, settings), InitMapGenEvent.EventType.CAVE);
        this.ravineCarver = TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE);
        
        this.strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(new MapGenStronghold(), InitMapGenEvent.EventType.STRONGHOLD);
        this.mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(new MapGenMineshaft(), InitMapGenEvent.EventType.MINESHAFT);
        
        this.oceanMonumentGenerator = (StructureOceanMonument)TerrainGen.getModdedMapGen(new StructureOceanMonument(), InitMapGenEvent.EventType.OCEAN_MONUMENT);
        this.woodlandMansionGenerator = (WoodlandMansion)TerrainGen.getModdedMapGen(new WoodlandMansion(this.chunkGenerator), InitMapGenEvent.EventType.WOODLAND_MANSION);
        this.scatteredFeatureGenerator = (MapGenScatteredFeature)TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), InitMapGenEvent.EventType.SCATTERED_FEATURE);

        this.biomeInjector = new BiomeInjector(this, this.biomeProvider.getBiomeSource(), this.buildBiomeInjectorRules());
        this.biomeProvider.setChunkSource(this);
        
        this.initialChunkCache = new ChunkCache<>("initial_chunk_primer", this::provideInitialChunkPrimerContainer);
        
        this.beachOctaveNoise = Optional.empty();
        this.surfaceOctaveNoise = Optional.empty();
        this.forestOctaveNoise = Optional.empty();
        
        // Set default cloud height
        this.setCloudHeight(this.worldHeight - 20);
    }
    
    /**
     * Create initial chunk given chunk coordinates.
     * Used to sample for biome injection and creation of initial biome map.
     * Called in {@link #provideInitialChunkPrimerContainer(int, int) provideInitialChunkPrimerContainer} and subsequently {@link #provideChunk(int, int) provideChunk}.
     *
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     */
    public abstract void provideInitialChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Create processed chunk given chunk coordinates.
     * This exists to allow terrain to be modified or regenerated for village placement, which needs to occur after {@link #provideInitialChunk(ChunkPrimer, int, int) provideInitialChunk} so villages can be placed correctly.
     * This does not need to be implemented and can be left empty, if you don't wish to do additional processing.
     * Called in {@link #provideChunk(int, int) provideChunk}.
     *
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     */
    public abstract void provideProcessedChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);

    /**
     * Build surface for given chunk primer and chunk coordinates.
     * Called in {@link #provideChunk(int, int) provideChunk}.
     *
     * @param biomes Biome array for chunk
     * @param chunkPrimer Chunk primer
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * 
     */
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    /**
     * Sample height at given x/z coordinate.
     *
     * @param x x-coordinate in block coordinates.
     * @param z z-coordinate in block coordinates.
     * @param type {@link HeightmapChunk.Type}.
     * @return The y-coordinate of top block at x/z.
     */
    public abstract int getHeight(int x, int z, HeightmapChunk.Type type);
    
    /**
     * Creates an entire chunk, with terrain, surfaces, caves, biomes, and structure placements generated.
     * Called in {@link ModernBetaChunkGenerator#generateChunk(int, int) generateChunk}.
     *
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return The completed chunk.
     */
    public Chunk provideChunk(int chunkX, int chunkZ) {
        // Retrieve chunk primer from cache
        ChunkPrimerContainer chunkContainer = this.initialChunkCache.get(chunkX, chunkZ);
        ChunkPrimer chunkPrimer = chunkContainer.chunkPrimer;
        Biome[] biomes = chunkContainer.biomes;
        
        if (!this.skipChunk(chunkX, chunkZ)) {
            // Generate village feature placements here, for structure weight sampling
            if (this.mapFeaturesEnabled) {
                if (this.settings.useVillages) {
                    this.villageGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
            }
            
            boolean villageGenerated = !this.componentCache.get(chunkX, chunkZ).getComponents().isEmpty();
            
            // Generate processed chunk
            this.provideProcessedChunk(chunkPrimer, chunkX, chunkZ);
            
            // Populate biome-specific surface
            this.provideSurface(biomes, chunkPrimer, chunkX, chunkZ);
            
            // Post-process biome map, after surface generation
            if (this.biomeInjector != null) {
                this.biomeInjector.injectBiomes(biomes, chunkPrimer, this, chunkX, chunkZ, BiomeInjectionStep.POST_SURFACE);
            }
            
            // Carve terrain
            if (this.settings.useCaves && !villageGenerated) {
                this.caveCarver.generate(this.world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useRavines && !villageGenerated) {
                this.ravineCarver.generate(this.world, chunkX, chunkZ, chunkPrimer);
            }
            
            // Generate map feature placements
            if (this.mapFeaturesEnabled) {
                if (this.settings.useMineShafts) {
                    this.mineshaftGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
                
                if (this.settings.useStrongholds) {
                    this.strongholdGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
                
                if (this.settings.useTemples) {
                    this.scatteredFeatureGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
                
                if (this.settings.useMonuments) {
                    this.oceanMonumentGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
                
                if (this.settings.useMansions) {
                    this.woodlandMansionGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
            }
        }
        
        // Generate final chunk
        Chunk chunk = new Chunk(this.world, chunkPrimer, chunkX, chunkZ);
        
        // Set biome map in chunk
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)Biome.getIdForBiome(biomes[i]);
        }
        
        chunk.generateSkylightMap();
        return chunk;
    }
    
    /**
     * Populates the chunk with generated structures and biome feature decorations (i.e. trees, plants, lakes, etc.).
     * Called in {@link ModernBetaChunkGenerator#populate(int, int) populate}.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public void populateChunk(int chunkX, int chunkZ) {
        // Prune outer chunks for finite worlds
        this.pruneChunk(chunkX, chunkZ);
        
        BlockFalling.fallInstantly = true;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        boolean hasVillageGenerated = false;
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        if (!this.skipChunk(chunkX, chunkZ)) {
            Biome biome = this.world.getBiome(mutablePos.setPos(startX + 16, 0, startZ + 16));
            
            this.random.setSeed(this.world.getSeed());
            long randomLong0 = (random.nextLong() / 2L) * 2L + 1L;
            long randomLong1 = (random.nextLong() / 2L) * 2L + 1L;
            this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ this.world.getSeed());

            ForgeEventFactory.onChunkPopulate(true, this.chunkGenerator, this.world, this.random, chunkX, chunkZ, false);
            
            // Actually generate map features here
            if (this.mapFeaturesEnabled) {
                if (this.settings.useMineShafts) {
                    this.mineshaftGenerator.generateStructure(this.world, this.random, chunkPos);
                }
                
                if (this.settings.useVillages) {
                    hasVillageGenerated = this.villageGenerator.generateStructure(this.world, this.random, chunkPos);
                }
                
                if (this.settings.useStrongholds) {
                    this.strongholdGenerator.generateStructure(this.world, this.random, chunkPos);
                }
                
                if (this.settings.useTemples) {
                    this.scatteredFeatureGenerator.generateStructure(this.world, this.random, chunkPos);
                }
                
                if (this.settings.useMonuments) {
                    this.oceanMonumentGenerator.generateStructure(this.world, this.random, chunkPos);
                }
                
                if (this.settings.useMansions) {
                    this.woodlandMansionGenerator.generateStructure(this.world, this.random, chunkPos);
                }
            }
            
            // Reset seed for generation accuracy
            this.random.setSeed(this.world.getSeed());
            randomLong0 = (this.random.nextLong() / 2L) * 2L + 1L;
            randomLong1 = (this.random.nextLong() / 2L) * 2L + 1L;
            this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ this.world.getSeed());
            
            // Generate lakes, dungeons
            
            if (!hasVillageGenerated &&
                this.settings.useWaterLakes && 
                TerrainGen.populate(this.chunkGenerator, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAKE)
            ) {
                if (random.nextInt(this.settings.waterLakeChance) == 0) { // Default: 4
                    int x = startX + this.random.nextInt(16) + 8;
                    int y = this.random.nextInt(this.settings.height);
                    int z = startZ + this.random.nextInt(16) + 8;
                    
                    (new WorldGenLakes(Blocks.WATER)).generate(this.world, this.random, mutablePos.setPos(x, y, z));
                }
            }
            
            if (!hasVillageGenerated &&
                this.settings.useLavaLakes &&
                TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAVA)
            ) {
                if (random.nextInt(this.settings.lavaLakeChance / 10) == 0) { // Default: 80 / 10 = 8
                    int x = startX + this.random.nextInt(16) + 8;
                    int y = this.random.nextInt(this.random.nextInt(this.settings.height - 8) + 8);
                    int z = startZ + this.random.nextInt(16) + 8;
                    
                    if (y < 64 || this.random.nextInt(10) == 0) {
                        (new WorldGenLakes(Blocks.LAVA)).generate(this.world, this.random, mutablePos.setPos(x, y, z));
                    }
                }
            }
            
            if (this.settings.useDungeons && 
                TerrainGen.populate(this.chunkGenerator, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.DUNGEON)
            ) {
                for (int i = 0; i < this.settings.dungeonChance; i++) {
                    int x = startX + this.random.nextInt(16) + 8;
                    int y = this.random.nextInt(this.settings.height);
                    int z = startZ + this.random.nextInt(16) + 8;
                    
                    new WorldGenDungeons().generate(this.world, this.random, mutablePos.setPos(x, y, z));
                }
            }
            
            // Generate biome decorations
            biome.decorate(this.world, this.random, new BlockPos(startX, 0, startZ));
            
            // Generate animals
            if (TerrainGen.populate(this.chunkGenerator, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
                WorldEntitySpawner.performWorldGenSpawning(this.world, biome, startX + 8, startZ + 8, 16, 16, this.random);
            }
        }
        
        // Get biome provider to check for climate sampler
        ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)this.world.getBiomeProvider();
        BiomeSource biomeSource = biomeProvider.getBiomeSource();

        // Generate snow / ice
        if (TerrainGen.populate(this.chunkGenerator, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ICE)) {
            for(int localX = 0; localX < 16; localX++) {
                for(int localZ = 0; localZ < 16; localZ++) {
                    // Adding 8 is important to prevent runaway chunk loading
                    int x = localX + startX + 8; 
                    int z = localZ + startZ + 8;
                    int y = this.world.getPrecipitationHeight(mutablePos.setPos(x, 0, z)).getY();

                    Biome biome = this.biomeProvider.getBiome(mutablePos);
                    BlockPos blockPosDown = mutablePos.setPos(x, y, z).down();
                    
                    boolean canSetIce = false;
                    boolean canSetSnow = false;
                    
                    if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleForFeatureGeneration()) {
                        double temp = ((ClimateSampler)biomeSource).sample(x, z).temp();
                        temp = temp - ((double)(y - 64) / 64.0) * 0.3;
                        
                        canSetIce = BiomeBeta.canSetIceBeta(this.world, blockPosDown, false, temp);
                        canSetSnow = BiomeBeta.canSetSnowBeta(this.world, mutablePos, temp);
                        
                    } else if (biome instanceof ModernBetaBiome) {
                        ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
                        double temp = (double)biome.getDefaultTemperature();
                        
                        canSetIce = modernBetaBiome.canSetIce(this.world, blockPosDown, false, temp);
                        canSetSnow = modernBetaBiome.canSetSnow(this.world, mutablePos, temp);
                        
                    } else {
                        canSetIce = this.world.canBlockFreezeWater(blockPosDown);
                        canSetSnow = this.world.canSnowAt(mutablePos, true);
                        
                    }
                    
                    if (canSetIce) this.world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                    if (canSetSnow) this.world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                }
            }
        }
        
        ForgeEventFactory.onChunkPopulate(false, this.chunkGenerator, this.world, this.random, chunkX, chunkZ, false);
        
        BlockFalling.fallInstantly = false;
    }
    
    /**
     * Generates the Ocean Monument structure, in a given chunk.
     * Called in {@link ModernBetaChunkGenerator#generateStructures(Chunk, int, int) generateStructures}.
     * 
     * @param chunk 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return Whether the ocean monument generated.
     */
    public boolean generateStructures(Chunk chunk, int chunkX, int chunkZ) {
        if (this.skipChunk(chunkX, chunkZ)) {
            return false;
        }
        
        boolean generated = false;
        
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunk.getInhabitedTime() < 3600L) {
            generated |= this.oceanMonumentGenerator.generateStructure(this.world, this.random, new ChunkPos(chunkX, chunkZ));
        }
        
        return generated;
    }
    
    /**
     * Gets a list of possible mob spawns from the biome retrieved at a given position and creature type.
     * Called in {@link ModernBetaChunkGenerator#getPossibleCreatures(EnumCreatureType, BlockPos) getPossibleCreatures}.
     * 
     * @param enumCreatureType The creature type, listed in {@link EnumCreatureType}
     * @param blockPos The block position, used to retrieve the biome.
     * @return List of possible mob spawns from the biome at the given position.
     */
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType, BlockPos blockPos) {
        if (this.skipChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return ImmutableList.of();
        }
        
        Biome biome = this.world.getBiome(blockPos);
        
        if (this.mapFeaturesEnabled) {
            if (enumCreatureType == EnumCreatureType.MONSTER &&
                this.settings.useTemples &&
                this.scatteredFeatureGenerator.isSwampHut(blockPos)
            ) {
                return this.scatteredFeatureGenerator.getMonsters();
            }
            
            if (enumCreatureType == EnumCreatureType.MONSTER &&
                this.settings.useMonuments &&
                this.oceanMonumentGenerator.isPositionInStructure(this.world, blockPos)
            ) {
                return this.oceanMonumentGenerator.getMonsters();
            }
        }
        
        List<Biome.SpawnListEntry> spawnEntries = new ArrayList<>(biome.getSpawnableList(enumCreatureType));
        ModernBetaBiomeMobs.modifySpawnList(spawnEntries, enumCreatureType, biome, this.settings);
        
        return spawnEntries;
    }
    
    /**
     * Used to test if a given block position is in a structure.
     * Called in {@link ModernBetaChunkGenerator#isInsideStructure(World, String, BlockPos) isInsideStructure}.
     * 
     * @param world
     * @param structureName The structure name (i.e. "Village", "Stronghold", etc.).
     * @param blockPos The block position used to test.
     * @return Whether the block position overlaps a structure.
     */
    public boolean isInsideStructure(World world, String structureName, BlockPos blockPos) {
        if (this.skipChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return false;
        }
        
        if (!this.mapFeaturesEnabled) {
            return false;
        }
        
        if (this.settings.useStrongholds && "Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.isInsideStructure(blockPos);
        }
        
        if (this.settings.useMansions && "Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
            return this.woodlandMansionGenerator.isInsideStructure(blockPos);
        }
        
        if (this.settings.useMonuments && "Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.isInsideStructure(blockPos);
        }
        
        if (this.settings.useVillages && "Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.isInsideStructure(blockPos);
        }
        
        if (this.settings.useMineShafts && "Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.isInsideStructure(blockPos);
        }
        
        if (this.settings.useTemples && "Temple".equals(structureName) && this.scatteredFeatureGenerator != null) {
            return this.scatteredFeatureGenerator.isInsideStructure(blockPos);
        }

        return false;
    }
    
    /**
     * Finds the location of the nearest specified structure.
     * Called in {@link ModernBetaChunkGenerator#getNearestStructurePos(World, String, BlockPos, boolean) getNearestStructurePos}.
     * 
     * @param world
     * @param structureName The structure name (i.e. "Village", "Stronghold", etc.).
     * @param blockPos The initial block position.
     * @param findUnexplored
     * @return The block position of the nearest specified structure.
     */
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos blockPos, boolean findUnexplored) {
        if (this.skipChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return null;
        }
        
        if (!this.mapFeaturesEnabled) {
            return null;
        }
        
        if (this.settings.useStrongholds && "Stronghold".equals(structureName) && this.strongholdGenerator != null) {
            return this.strongholdGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if (this.settings.useMansions && "Mansion".equals(structureName) && this.woodlandMansionGenerator != null) {
            return this.woodlandMansionGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if (this.settings.useMonuments && "Monument".equals(structureName) && this.oceanMonumentGenerator != null) {
            return this.oceanMonumentGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if (this.settings.useVillages && "Village".equals(structureName) && this.villageGenerator != null) {
            return this.villageGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if (this.settings.useMineShafts && "Mineshaft".equals(structureName) && this.mineshaftGenerator != null) {
            return this.mineshaftGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        if (this.settings.useTemples && "Temple".equals(structureName) && this.scatteredFeatureGenerator != null) {
            return this.scatteredFeatureGenerator.getNearestStructurePos(world, blockPos, findUnexplored);
        }
        
        return null;
    }
    
    /**
     * Called in {@link ModernBetaChunkGenerator#recreateStructures(Chunk, int, int) recreateStructures}.
     * 
     * @param chunk
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    public void recreateStructures(Chunk chunk, int chunkX, int chunkZ) {
        if (this.skipChunk(chunkX, chunkZ)) {
            return;
        }
        
        if (!this.mapFeaturesEnabled) {
            return;
        }
        
        if (this.settings.useMineShafts) {
            this.mineshaftGenerator.generate(this.world, chunkX, chunkZ, null);
        }
        
        if (this.settings.useVillages) {
            this.villageGenerator.generate(this.world, chunkX, chunkZ, null);
        }
        
        if (this.settings.useStrongholds) {
            this.strongholdGenerator.generate(this.world, chunkX, chunkZ, null);
        }
        
        if (this.settings.useTemples) {
            this.scatteredFeatureGenerator.generate(this.world, chunkX, chunkZ, null);
        }
        
        if (this.settings.useMonuments) {
            this.oceanMonumentGenerator.generate(this.world, chunkX, chunkZ, null);
        }
        
        if (this.settings.useMansions) {
            this.woodlandMansionGenerator.generate(this.world, chunkX, chunkZ, null);
        }
    }

    
    /**
     * Gets post-biome injection biome array for a given chunk.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return The biome array for the given chunk.
     */
    public Biome[] getBiomes(int chunkX, int chunkZ) {
        return this.initialChunkCache.get(chunkX, chunkZ).biomes;
    }

    /**
     * Gets the chunk source sea level set in the chunk generator settings.
     * 
     * @return The chunk source sea level.
     */
    public int getSeaLevel() {
        return this.settings.seaLevel;
    }
    
    /**
     * Gets the default fluid for the chunk source, either water or lava.
     * 
     * @return The default fluid blockstate.
     */
    public IBlockState getDefaultFluid() {
        return this.defaultFluid;
    }
    
    /**
     * Gets the chunk generator settings.
     * 
     * @return The {@link ModernBetaGeneratorSettings} settings.
     */
    public ModernBetaGeneratorSettings getGeneratorSettings() {
        return this.settings;
    }
    
    
    /**
     * Gets the spawn locator used to initially place the player spawn.
     * 
     * @return The spawn locator, {@link SpawnLocator#DEFAULT} by default. 
     */
    public SpawnLocator getSpawnLocator() {
        return SpawnLocator.DEFAULT;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for beach generation.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getBeachOctaveNoise() {
        return this.beachOctaveNoise;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for surface generation.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getSurfaceOctaveNoise() {
        return this.surfaceOctaveNoise;
    }
    
    /**
     * Gets the PerlinOctaveNoise sampler used for tree placement.
     * 
     * @return An optional containing the noise sampler, may be null.
     */
    public Optional<PerlinOctaveNoise> getForestOctaveNoise() {
        return this.forestOctaveNoise;
    }
    
    /**
     * Gets the associated world object.
     * 
     * @return The world object.
     */
    public World getWorld() {
        return this.world;
    }
    
    /**
     * Gets the cache containing all nearby chunks with references to village components.
     * 
     * @return The chunk cache for chunks containing village components.
     */
    public ChunkCache<ComponentChunk> getComponentCache() {
        return this.componentCache;
    }

    /**
     * Sets the cloud height in the {@link ModernBetaWorldType} world type.
     * 
     * @param cloudHeight y-coordinate of the new cloud height, in block coordinates.
     */
    protected void setCloudHeight(int cloudHeight) {
        ModernBetaWorldType.INSTANCE.setCloudHeight(cloudHeight);
    }
    
    /**
     * Indicate whether the chunk at the given coordinates should be skipped.
     * This is called in several generation methods and used by {@link FiniteChunkSource}.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return Whether the chunk should be skipped.
     */
    protected boolean skipChunk(int chunkX, int chunkZ) {
        return false;
    }
    
    /**
     * Prunes the chuck at the given coordinates.
     * This is used by {@link FiniteChunkSource}.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     */
    protected void pruneChunk(int chunkX, int chunkZ) { }
    
    /**
     * Sets the default PerlinOctaveNoise sampler used for beach generation for the default surface generator in {@link ModernBetaBiome}.
     * 
     * @param beachOctaveNoise The noise sampler, may be null.
     */
    protected void setBeachOctaveNoise(PerlinOctaveNoise beachOctaveNoise) {
        this.beachOctaveNoise = Optional.ofNullable(beachOctaveNoise);
        
        // Set beach noise for builtin Modern Biome surface builder
        ModernBetaBiome.setBeachOctaveNoise(beachOctaveNoise);
    }

    /**
     * Sets the PerlinOctaveNoise sampler used for surface generation.
     * 
     * @param surfaceOctaveNoise The noise sampler, may be null.
     */
    protected void setSurfaceOctaveNoise(PerlinOctaveNoise surfaceOctaveNoise) {
        this.surfaceOctaveNoise = Optional.ofNullable(surfaceOctaveNoise);
    }
    
    /**
     * Sets the PerlinOctaveNoise sampler used for tree placement.
     * 
     * @param forestOctaveNoise The noise sampler, may be null.
     */
    protected void setForestOctaveNoise(PerlinOctaveNoise forestOctaveNoise) {
        this.forestOctaveNoise = Optional.ofNullable(forestOctaveNoise);
    }

    /**
     * Builds the ruleset used for biome injection.
     * 
     * @return The built biome injection rules.
     */
    protected BiomeInjectionRules buildBiomeInjectorRules() {
        boolean replaceOceans = this.getGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getGeneratorSettings().replaceBeachBiomes;
        
        BiomeInjectionRules.Builder builder = new BiomeInjectionRules.Builder();
        
        Predicate<BiomeInjectionContext> deepOceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), DEEP_OCEAN_MIN_DEPTH);
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.pos.getY(), OCEAN_MIN_DEPTH);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.pos.getY()) && this.isBeachBlock(context.state);
            
        if (replaceBeaches && this.biomeProvider.getBiomeSource() instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)this.biomeProvider.getBiomeSource();
            
            builder.add(beachPredicate, biomeResolverBeach::getBeachBiome, BiomeInjectionStep.POST_SURFACE);
        }
        
        if (replaceOceans && this.biomeProvider.getBiomeSource() instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)this.biomeProvider.getBiomeSource();

            builder.add(deepOceanPredicate, biomeResolverOcean::getDeepOceanBiome, BiomeInjectionStep.PRE_SURFACE);
            builder.add(oceanPredicate, biomeResolverOcean::getOceanBiome, BiomeInjectionStep.PRE_SURFACE);
        }
        
        return builder.build();
    }

    /**
     * Tests whether a given height is where a beach generates.
     * 
     * @param topHeight y-coordinate of the highest block for a particular position.
     * @return Whether the given height for a position is at the depth where beaches generated.
     */
    protected boolean atBeachDepth(int topHeight) {
        int seaLevel = this.getSeaLevel();
        
        return topHeight >= seaLevel - 4 && topHeight <= seaLevel + 1;
    }

    /**
     * Tests whether a given block state is a beach (sand) block.
     * 
     * @param blockState Block state to be tested.
     * @return Whether the given block state is a sand block.
     */
    protected boolean isBeachBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        
        // Only handle sand beaches,
        // due to limitation of heightmap cache.
        return block == Blocks.SAND;
    }
    
    /**
     * Tests whether a given height is at ocean depth.
     * 
     * @param topHeight y-coordinate of the highest block for a particular position.
     * @param oceanDepth The height depth to test the height against.
     * @return Whether the given height is below the ocean depth value.
     */
    protected boolean atOceanDepth(int topHeight, int oceanDepth) {
        return topHeight < this.getSeaLevel() - oceanDepth;
    }

    /**
     * Tests whether a given block state is the default fluid block.
     * 
     * @param blockState Block state to be tested.
     * @return Whether the given block state is the default fluid block.
     */
    protected boolean isFluidBlock(IBlockState blockState) {
        return blockState.getBlock() == this.defaultFluid.getBlock();
    }
    
    /**
     * Generates the initial chunk and modified biome array after biome injection.
     * 
     * @param chunkX x-coordinate in chunk coordinates
     * @param chunkZ z-coordinate in chunk coordinates
     * @return A container with the base chunk primer and modified biome array.
     */
    private ChunkPrimerContainer provideInitialChunkPrimerContainer(int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        Biome[] biomes = new Biome[256];
        
        // Generate base terrain
        this.provideInitialChunk(chunkPrimer, chunkX, chunkZ);
        
        // Generate base biome map
        this.biomeProvider.getBaseBiomes(biomes, startX, startZ, 16, 16);
        
        // Post-process biome map, before surface generation
        if (this.biomeInjector != null) {
            this.biomeInjector.injectBiomes(biomes, chunkPrimer, this, chunkX, chunkZ, BiomeInjectionStep.PRE_SURFACE);
        }
        
        return new ChunkPrimerContainer(chunkPrimer, biomes);
    }
    
    private static class ChunkPrimerContainer {
        public final ChunkPrimer chunkPrimer;
        public final Biome[] biomes;
        
        /**
         * Constructs a container that holds a chunk primer and biome array.
         * 
         * @param chunkPrimer
         * @param biomes
         */
        public ChunkPrimerContainer(ChunkPrimer chunkPrimer, Biome[] biomes) {
            this.chunkPrimer = chunkPrimer;
            this.biomes = biomes;
        }
    }
}
