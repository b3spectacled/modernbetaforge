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
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
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
    protected final ModernBetaChunkGeneratorSettings settings;
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
    
    public ChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings chunkGeneratorSettings
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
        this.componentCache = new ChunkCache<>("structure_components", MAX_RENDER_DISTANCE_AREA, ComponentChunk::new);

        this.caveCarver = TerrainGen.getModdedMapGen(ModernBetaRegistries.CARVER.get(settings.caveCarver).apply(this.world, this, settings), InitMapGenEvent.EventType.CAVE);
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
    
    public abstract void provideInitialChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract void provideProcessedChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract int getHeight(int x, int z, HeightmapChunk.Type type);
    
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

    public Biome[] getBiomes(int chunkX, int chunkZ) {
        return this.initialChunkCache.get(chunkX, chunkZ).biomes;
    }

    public int getSeaLevel() {
        return this.settings.seaLevel;
    }
    
    public IBlockState getDefaultFluid() {
        return this.defaultFluid;
    }
    
    public ModernBetaChunkGeneratorSettings getChunkGeneratorSettings() {
        return this.settings;
    }
    
    public SpawnLocator getSpawnLocator() {
        return SpawnLocator.DEFAULT;
    }
    
    public Optional<PerlinOctaveNoise> getBeachOctaveNoise() {
        return this.beachOctaveNoise;
    }
    
    public Optional<PerlinOctaveNoise> getSurfaceOctaveNoise() {
        return this.surfaceOctaveNoise;
    }
    
    public Optional<PerlinOctaveNoise> getForestOctaveNoise() {
        return this.forestOctaveNoise;
    }
    
    public World getWorld() {
        return this.world;
    }
    
    public ChunkCache<ComponentChunk> getComponentCache() {
        return this.componentCache;
    }

    protected void setCloudHeight(int cloudHeight) {
        ModernBetaWorldType.INSTANCE.setCloudHeight(cloudHeight);
    }
    
    protected boolean skipChunk(int chunkX, int chunkZ) {
        return this.skipChunk(chunkX, chunkZ, 0);
    }
    
    protected boolean skipChunk(int chunkX, int chunkZ, int padding) {
        return false;
    }
    
    protected void pruneChunk(int chunkX, int chunkZ) { }
    
    protected void setBeachOctaveNoise(PerlinOctaveNoise beachOctaveNoise) {
        this.beachOctaveNoise = Optional.ofNullable(beachOctaveNoise);
        
        // Set beach noise for builtin Modern Biome surface builder
        ModernBetaBiome.setBeachOctaveNoise(beachOctaveNoise);
    }
    
    protected void setSurfaceOctaveNoise(PerlinOctaveNoise surfaceOctaveNoise) {
        this.surfaceOctaveNoise = Optional.ofNullable(surfaceOctaveNoise);
    }
    
    protected void setForestOctaveNoise(PerlinOctaveNoise forestOctaveNoise) {
        this.forestOctaveNoise = Optional.ofNullable(forestOctaveNoise);
    }

    protected BiomeInjectionRules buildBiomeInjectorRules() {
        boolean replaceOceans = this.getChunkGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getChunkGeneratorSettings().replaceBeachBiomes;
        
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

    protected boolean atBeachDepth(int topHeight) {
        int seaLevel = this.getSeaLevel();
        
        return topHeight >= seaLevel - 4 && topHeight <= seaLevel + 1;
    }
    
    protected boolean isBeachBlock(IBlockState blockState) {
        Block block = blockState.getBlock();
        
        // Only handle sand beaches,
        // due to limitation of heightmap cache.
        return block == Blocks.SAND;
    }
    
    protected boolean atOceanDepth(int topHeight, int oceanDepth) {
        return topHeight < this.getSeaLevel() - oceanDepth;
    }
    
    protected boolean isFluidBlock(IBlockState blockState) {
        return blockState.getBlock() == this.defaultFluid.getBlock();
    }
    
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
        
        public ChunkPrimerContainer(ChunkPrimer chunkPrimer, Biome[] biomes) {
            this.chunkPrimer = chunkPrimer;
            this.biomes = biomes;
        }
    }
}
