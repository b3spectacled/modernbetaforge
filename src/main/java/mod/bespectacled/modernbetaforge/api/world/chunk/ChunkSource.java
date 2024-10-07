package mod.bespectacled.modernbetaforge.api.world.chunk;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverBeach;
import mod.bespectacled.modernbetaforge.api.world.biome.BiomeResolverOcean;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.spawn.SpawnLocator;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BiomeUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.SimplexOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeLists;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionResolver;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjector;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCave;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.structure.BetaStructureOceanMonument;
import mod.bespectacled.modernbetaforge.world.structure.BetaWoodlandMansion;
import mod.bespectacled.modernbetaforge.world.structure.MapGenBetaScatteredFeature;
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
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public abstract class ChunkSource {
    protected static final int OCEAN_MIN_DEPTH = 4;
    protected static final int DEEP_OCEAN_MIN_DEPTH = 16;
    
    protected final ModernBetaChunkGenerator chunkGenerator;
    protected final ModernBetaChunkGeneratorSettings settings;
    protected final ModernBetaBiomeProvider biomeProvider;
    
    protected final World world;
    protected final long seed;
    protected final boolean mapFeaturesEnabled;
    protected final Random random;

    private final MapGenBase caveCarver;
    private final MapGenBase ravineCarver; 
    
    private final MapGenStronghold strongholdGenerator;
    private final MapGenVillage villageGenerator;
    private final MapGenMineshaft mineshaftGenerator;
    
    private final MapGenBetaScatteredFeature scatteredFeatureGenerator;
    private final BetaStructureOceanMonument oceanMonumentGenerator;
    private final BetaWoodlandMansion woodlandMansionGenerator;
    
    private final Biome[] biomes = new Biome[256];
    private final SimplexOctaveNoise surfaceOctaveNoise;
    private final BiomeInjector biomeInjector;

    // Set for specifying which biomes should use their vanilla surface builders.
    // Done on per-biome basis for best mod compatibility.
    private final Set<Biome> biomesWithCustomSurfaces = new HashSet<Biome>(
        ModernBetaBiomeLists.BUILTIN_BIOMES_WITH_CUSTOM_SURFACES
    );
    
    public ChunkSource(World world, ModernBetaChunkGenerator chunkGenerator, ModernBetaChunkGeneratorSettings chunkGeneratorSettings, long seed, boolean mapFeaturesEnabled) {
        this.chunkGenerator = chunkGenerator;
        this.settings = chunkGeneratorSettings;
        this.biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
        
        this.world = world;
        this.seed = seed;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        this.random = new Random(seed);

        this.caveCarver = TerrainGen.getModdedMapGen(new MapGenBetaCave(), InitMapGenEvent.EventType.CAVE);
        this.ravineCarver = TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE);
        
        this.strongholdGenerator = (MapGenStronghold)TerrainGen.getModdedMapGen(new MapGenStronghold(), InitMapGenEvent.EventType.STRONGHOLD);
        this.villageGenerator = (MapGenVillage)TerrainGen.getModdedMapGen(new MapGenVillage(), InitMapGenEvent.EventType.VILLAGE);
        this.mineshaftGenerator = (MapGenMineshaft)TerrainGen.getModdedMapGen(new MapGenMineshaft(), InitMapGenEvent.EventType.MINESHAFT);
        
        //this.scatteredFeatureGenerator = (ModernBetaMapGenScatteredFeature)TerrainGen.getModdedMapGen(new ModernBetaMapGenScatteredFeature(), InitMapGenEvent.EventType.SCATTERED_FEATURE);
        //this.oceanMonumentGenerator = (ModernBetaStructureOceanMonument)TerrainGen.getModdedMapGen(new ModernBetaStructureOceanMonument(this), InitMapGenEvent.EventType.OCEAN_MONUMENT);
        //this.woodlandMansionGenerator = (ModernBetaWoodlandMansion)TerrainGen.getModdedMapGen(new ModernBetaWoodlandMansion(chunkGenerator), InitMapGenEvent.EventType.WOODLAND_MANSION);
        
        // To avoid mod incompatibilities, do not replace with modded structures
        // TODO: Figure out a better way to handle this, maybe event handlers.
        this.scatteredFeatureGenerator = new MapGenBetaScatteredFeature();
        this.oceanMonumentGenerator = new BetaStructureOceanMonument();
        this.woodlandMansionGenerator = new BetaWoodlandMansion(chunkGenerator);
        
        // Init custom/vanilla surface info
        this.surfaceOctaveNoise = new SimplexOctaveNoise(new Random(seed), 4);
        this.biomesWithCustomSurfaces.addAll(
            Arrays.asList(ModernBetaConfig.generatorOptions.biomesWithCustomSurfaces)
                .stream()
                .map(id -> BiomeUtil.getBiome(id, "custom surface config"))
                .collect(Collectors.toList())
        );

        // Init biome injector
        this.biomeInjector = new BiomeInjector(this, this.biomeProvider.getBiomeSource());
        this.initBiomeInjector();
        this.biomeInjector.buildRules();
        
        this.biomeProvider.setChunkSource(this);

        // Important for correct structure spawning when y < seaLevel, e.g. villages
        this.world.setSeaLevel(this.settings.seaLevel);
    }
    
    public abstract void provideBaseChunk(ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract void provideSurface(Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ);
    
    public abstract int getHeight(int x, int z, HeightmapChunk.Type type);
    
    public Chunk provideChunk(int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        
        // Generate base terrain
        this.provideBaseChunk(chunkPrimer, chunkX, chunkZ);
        
        // Generate biome map
        this.world.getBiomeProvider().getBiomes(this.biomes, startX, startZ, 16, 16);
        
        // Populate biome-specific surface
        this.provideSurface(this.biomes, chunkPrimer, chunkX, chunkZ);
        
        // Post-process biome map
        if (this.biomeInjector != null) {
            this.biomeInjector.getInjectedBiomes(this.biomes, chunkPrimer, this, chunkX, chunkZ);
        }
        
        // Carve terrain
        if (this.settings.useCaves) {
            this.caveCarver.generate(this.world, chunkX, chunkZ, chunkPrimer);
        }
        
        if (this.settings.useRavines) {
            this.ravineCarver.generate(this.world, chunkX, chunkZ, chunkPrimer);
        }
        
        // Generate map feature placements
        if (this.mapFeaturesEnabled) {
            if (this.settings.useMineShafts) {
                this.mineshaftGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
            }
            
            if (this.settings.useVillages) {
                this.villageGenerator.generate(this.world, chunkX, chunkZ, chunkPrimer);
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
        
        // Generate final chunk
        Chunk chunk = new Chunk(this.world, chunkPrimer, chunkX, chunkZ);
        
        // Set biome map in chunk
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
           biomeArray[i] = (byte)Biome.getIdForBiome(this.biomes[i]); 
        }
        
        chunk.generateSkylightMap();
        return chunk;
    }
    
    public void populateChunk(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        boolean hasVillageGenerated = false;
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        Biome biome = this.world.getBiome(new BlockPos(startX + 16, 0, startZ + 16));
        
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
                int y = this.random.nextInt(128);
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
                int y = this.random.nextInt(this.random.nextInt(120) + 8);
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
                int y = this.random.nextInt(128);
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
        
        // Get biome provider to check for climate sampler
        ModernBetaBiomeProvider biomeProvider = (ModernBetaBiomeProvider)this.world.getBiomeProvider();

        // Generate snow / ice
        if (TerrainGen.populate(this.chunkGenerator, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ICE)) {
            for(int dX = 0; dX < 16; dX++) {
                for(int dZ = 0; dZ < 16; dZ++) {
                    // Adding 8 is important to prevent runaway chunk loading
                    int x = dX + startX + 8; 
                    int z = dZ + startZ + 8;
                    int y = this.world.getPrecipitationHeight(mutablePos.setPos(x, 0, z)).getY();

                    BlockPos blockPosDown = mutablePos.setPos(x, y, z).down();
                    
                    if (biomeProvider.getBiomeSource() instanceof ClimateSampler) {
                        ClimateSampler climateSampler = (ClimateSampler)biomeProvider.getBiomeSource();
                        
                        double temp = climateSampler.sample(x, z).temp();
                        temp = temp - ((double)(y - 64) / 64.0) * 0.3;
                        
                        if (BiomeBeta.canSetIce(this.world, blockPosDown, false, temp)) {
                            this.world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                        }
                        
                        if (BiomeBeta.canSetSnow(this.world, mutablePos, temp)) {
                            this.world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    } else {
                        if (this.world.canBlockFreezeWater(blockPosDown)) {
                            this.world.setBlockState(blockPosDown, Blocks.ICE.getDefaultState(), 2);
                        }
                        
                        if (this.world.canSnowAt(mutablePos, true)) {
                            this.world.setBlockState(mutablePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        
        ForgeEventFactory.onChunkPopulate(false, this.chunkGenerator, this.world, this.random, chunkX, chunkZ, false);
        
        BlockFalling.fallInstantly = false;
    }
    
    public boolean generateStructures(Chunk chunk, int chunkX, int chunkZ) {
        boolean generated = false;
        
        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunk.getInhabitedTime() < 3600L) {
            generated |= this.oceanMonumentGenerator.generateStructure(this.world, this.random, new ChunkPos(chunkX, chunkZ));
        }
        
        return generated;
    }
    
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType, BlockPos blockPos) {
        Biome biome = this.world.getBiome(blockPos);
        
        if (this.mapFeaturesEnabled) {
            if (enumCreatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.isSwampHut(blockPos)) {
                return this.scatteredFeatureGenerator.getMonsters();
            }
            
            if (enumCreatureType == EnumCreatureType.MONSTER &&
                this.settings.useMonuments &&
                this.oceanMonumentGenerator.isPositionInStructure(this.world, blockPos)
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
        
        return ("Temple".equals(structureName) && this.scatteredFeatureGenerator != null) ?
            this.scatteredFeatureGenerator.getNearestStructurePos(world, blockPos, findUnexplored) :
            null;
    }
    
    public void recreateStructures(Chunk chunk, int chunkX, int chunkZ) {
        if (this.mapFeaturesEnabled) {
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
    }
    
    public int getSeaLevel() {
        return this.settings.seaLevel;
    }
    
    public Biome getCachedInjectedBiome(int x, int z) {
        if (this.biomeInjector != null) {
            return this.biomeInjector.getCachedInjectedBiome(x, z);
        }
        
        return null;
    }
    
    public ModernBetaChunkGeneratorSettings getChunkGeneratorSettings() {
        return this.settings;
    }
    
    public SpawnLocator getSpawnLocator() {
        return SpawnLocator.DEFAULT;
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
        if (this.biomesWithCustomSurfaces.contains(biome)) {
            double surfaceNoise = this.surfaceOctaveNoise.sample(x, z, 0.0625, 0.0625, 1.0);
            
            // Reverse x/z because ??? why is it done this way in the surface gen code ????
            // Special surfaces won't properly generate if x/z are provided in the correct order, because WTF?!
            biome.genTerrainBlocks(this.world, random, chunkPrimer, z, x, surfaceNoise);
            
            return true;
        }
        
        return false;
    }
    
    protected void initBiomeInjector() {
        boolean replaceOceans = this.getChunkGeneratorSettings().replaceOceanBiomes;
        boolean replaceBeaches = this.getChunkGeneratorSettings().replaceBeachBiomes;
        
        Predicate<BiomeInjectionContext> oceanPredicate = context -> 
            this.atOceanDepth(context.topPos.getY(), OCEAN_MIN_DEPTH);
            
        Predicate<BiomeInjectionContext> beachPredicate = context ->
            this.atBeachDepth(context.topPos.getY()) && this.isBeachBlock(context.topState);
            
        if (replaceBeaches && this.biomeProvider.getBiomeSource() instanceof BiomeResolverBeach) {
            BiomeResolverBeach biomeResolverBeach = (BiomeResolverBeach)this.biomeProvider.getBiomeSource();
            
            this.addBiomeInjectorRule(beachPredicate, biomeResolverBeach::getBeachBiome, "beach");
        }
        
        if (replaceOceans && this.biomeProvider.getBiomeSource() instanceof BiomeResolverOcean) {
            BiomeResolverOcean biomeResolverOcean = (BiomeResolverOcean)this.biomeProvider.getBiomeSource();
            
            this.addBiomeInjectorRule(oceanPredicate, biomeResolverOcean::getOceanBiome, "ocean");
        }
    }
    
    protected void addBiomeInjectorRule(Predicate<BiomeInjectionContext> rule, BiomeInjectionResolver resolver, String id) {
        this.biomeInjector.addRule(rule, resolver, id);
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
    
    private boolean atOceanDepth(int topHeight, int oceanDepth) {
        return topHeight < this.getSeaLevel() - oceanDepth;
    }
}
