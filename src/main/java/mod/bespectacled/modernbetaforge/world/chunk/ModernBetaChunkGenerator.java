package mod.bespectacled.modernbetaforge.world.chunk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.DebugUtil;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ComponentChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeMobs;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjector;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaStructures;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureOceanMonument;
import net.minecraft.world.gen.structure.WoodlandMansion;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ModernBetaChunkGenerator extends ChunkGeneratorOverworld {
    private static final int MAX_RENDER_DISTANCE_AREA = 1024;
    
    private final World world;
    private final Random random;
    private final ChunkSource chunkSource;
    private final ModernBetaGeneratorSettings settings;

    private final ModernBetaBiomeProvider biomeProvider;
    private final BiomeInjector biomeInjector;
    
    private final ChunkCache<ChunkPrimerContainer> initialChunkCache;
    private final ChunkCache<ComponentChunk> componentCache;
    
    private final Map<ResourceLocation, MapGenStructure> structureMap;
    private final Map<ResourceLocation, MapGenBase> carverMap;
    
    private final List<WorldGenerator> customFeatures;
    
    public ModernBetaChunkGenerator(World world, String generatorOptions) {
        super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions);
        
        ModernBetaGeneratorSettings settings = generatorOptions != null ?
            ModernBetaGeneratorSettings.build(generatorOptions) :
            ModernBetaGeneratorSettings.build();
        
        this.world = world;
        this.random = new Random(world.getSeed());
        this.chunkSource = ModernBetaRegistries.CHUNK_SOURCE
            .get(new ResourceLocation(settings.chunkSource))
            .apply(world.getSeed(), settings);
        this.settings = settings;

        this.biomeProvider = (ModernBetaBiomeProvider)world.getBiomeProvider();
        this.biomeProvider.setChunkGenerator(this);
        
        this.biomeInjector = new BiomeInjector(
            this.chunkSource,
            this.biomeProvider.getBiomeSource(),
            this.chunkSource.buildBiomeInjectorRules(this.biomeProvider)
        );

        this.initialChunkCache = new ChunkCache<>("initial_chunk", this::provideInitialChunkPrimerContainer);
        this.componentCache = new ChunkCache<>("components", MAX_RENDER_DISTANCE_AREA, ComponentChunk::new);
        
        this.structureMap = this.initStructures(this, settings, world.getWorldInfo().isMapFeaturesEnabled());
        this.carverMap = this.initCarvers(settings);
        
        this.customFeatures = ModernBetaRegistries.FEATURE
            .getValues()
            .stream()
            .map(feature -> feature.apply(this.chunkSource, this.settings))
            .collect(Collectors.toCollection(LinkedList<WorldGenerator>::new));

        // Important for correct structure spawning when y < seaLevel, e.g. villages, monuments
        world.setSeaLevel(this.chunkSource.getSeaLevel());
        
        DebugUtil.resetDebug(DebugUtil.SECTION_GEN_CHUNK);
    }
    
    /*
     * Handled in generateChunk, but also used by WoodlandMansion.
     * 
     */
    @Override
    public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer chunkPrimer) {
        // Retrieve chunk primer from cache
        ChunkPrimerContainer chunkContainer = this.initialChunkCache.get(chunkX, chunkZ);
        ChunkPrimer containerPrimer = chunkContainer.chunkPrimer;
        
        // Generate village feature placements here, for structure weight sampling
        if (this.structureMap.containsKey(ModernBetaStructures.VILLAGE)) {
            this.structureMap.get(ModernBetaStructures.VILLAGE).generate(this.world, chunkX, chunkZ, containerPrimer);
        }
        
        // Generate processed chunk
        List<StructureComponent> structureComponents = componentCache.get(chunkX, chunkZ).getComponents();
        this.chunkSource.provideProcessedChunk(this.world, containerPrimer, chunkX, chunkZ, structureComponents);
        
        // Copy chunk data into chunkPrimer parameter
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < 255; ++y) {
                    IBlockState blockState = containerPrimer.getBlockState(x, y, z);
                    
                    if (blockState == BlockStates.AIR) {
                        continue;
                    }
                    
                    chunkPrimer.setBlockState(x, y, z, blockState);
                }
            }
        }
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        DebugUtil.startDebug(DebugUtil.SECTION_GEN_CHUNK);

        ChunkPrimer chunkPrimer = new ChunkPrimer();
        this.setBlocksInChunk(chunkX, chunkZ, chunkPrimer);
        
        Biome[] biomes = this.initialChunkCache.get(chunkX, chunkZ).biomes;
        
        if (!this.chunkSource.skipChunk(chunkX, chunkZ)) {
            // Flag is village component has generated in this chunk
            boolean villageGenerated = !this.componentCache.get(chunkX, chunkZ).getComponents().isEmpty();
            
            // Remove component chunk now that terrain has generated.
            this.componentCache.remove(chunkX, chunkZ);
            
            // Populate biome-specific surface
            this.chunkSource.provideSurface(this.world, biomes, chunkPrimer, chunkX, chunkZ);
            
            // Post-process biome map, after surface generation
            if (this.biomeInjector != null) {
                this.biomeInjector.injectBiomes(this.world, biomes, chunkPrimer, this.chunkSource, chunkX, chunkZ, BiomeInjectionStep.POST_SURFACE);
            }
            
            // Carve terrain
            for (MapGenBase carver : this.carverMap.values()) {
                if (!villageGenerated) {
                    carver.generate(this.world, chunkX, chunkZ, chunkPrimer);
                }
            }

            // Generate map feature placements
            for (Entry<ResourceLocation, MapGenStructure> structureEntry : this.structureMap.entrySet()) {
                if (!structureEntry.getKey().equals(ModernBetaStructures.VILLAGE)) {
                    structureEntry.getValue().generate(this.world, chunkX, chunkZ, chunkPrimer);
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
        DebugUtil.endDebug(DebugUtil.SECTION_GEN_CHUNK);
        
        return chunk;
    }

    @Override
    public void populate(int chunkX, int chunkZ) {
        // Prune outer chunks for finite worlds
        this.chunkSource.pruneChunk(this.world, chunkX, chunkZ);
        
        BlockFalling.fallInstantly = true;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        boolean hasVillageGenerated = false;
        
        if (!this.chunkSource.skipChunk(chunkX, chunkZ)) {
            Biome biome = this.world.getBiome(mutablePos.setPos(startX + 16, 0, startZ + 16));
            this.random.setSeed(this.world.getSeed());
            long randomLong0 = (random.nextLong() / 2L) * 2L + 1L;
            long randomLong1 = (random.nextLong() / 2L) * 2L + 1L;
            this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ this.world.getSeed());

            ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, chunkX, chunkZ, false);
            
            // Actually generate map features here
            for (Entry<ResourceLocation, MapGenStructure> structureEntry : this.structureMap.entrySet()) {
                if (structureEntry.getKey().equals(ModernBetaStructures.VILLAGE)) {
                    hasVillageGenerated = structureEntry.getValue().generateStructure(this.world, this.random, chunkPos);
                } else {
                    structureEntry.getValue().generateStructure(this.world, this.random, chunkPos);
                }
            }
            
            // Reset seed for generation accuracy
            this.random.setSeed(this.world.getSeed());
            randomLong0 = (this.random.nextLong() / 2L) * 2L + 1L;
            randomLong1 = (this.random.nextLong() / 2L) * 2L + 1L;
            this.random.setSeed((long)chunkX * randomLong0 + (long)chunkZ * randomLong1 ^ this.world.getSeed());
            
            // Generate lakes, dungeons
            
            boolean populateWaterLakes = TerrainGen.populate(this, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAKE);
            if (this.settings.useWaterLakes &&  populateWaterLakes && !hasVillageGenerated ) {
                ModernBetaBiomeDecorator.populateWaterLakes(this.world, this.random, this.settings, mutablePos, chunkX, chunkZ);
            }
            
            boolean populateLavaLakes = TerrainGen.populate(this, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAVA);
            if (this.settings.useLavaLakes && populateLavaLakes && !hasVillageGenerated) {
                ModernBetaBiomeDecorator.populateLavaLakes(this.world, this.random, this.settings, mutablePos, chunkX, chunkZ);
            }
            
            boolean populateDungeons = TerrainGen.populate(this, world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.DUNGEON);
            if (this.settings.useDungeons && populateDungeons) {
                ModernBetaBiomeDecorator.populateDungeons(this.world, this.random, this.settings, mutablePos, chunkX, chunkZ);
            }
            
            // Generate biome features
            biome.decorate(this.world, this.random, new BlockPos(startX, 0, startZ));
            
            // Generate custom features
            if (TerrainGen.populate(this, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.CUSTOM)) {
                for (WorldGenerator feature : this.customFeatures) {
                    feature.generate(this.world, this.random, mutablePos.setPos(startX, 0, startZ));
                }
            }
            
            // Generate animals
            if (TerrainGen.populate(this, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
                WorldEntitySpawner.performWorldGenSpawning(this.world, biome, startX + 8, startZ + 8, 16, 16, this.random);
            }
        }
        
        // Generate snow / ice
        if (TerrainGen.populate(this, this.world, this.random, chunkX, chunkZ, hasVillageGenerated, PopulateChunkEvent.Populate.EventType.ICE)) {
            ModernBetaBiomeDecorator.populateSnowIce(this.world, this.random, this.biomeProvider, mutablePos, chunkX, chunkZ);
        }
        
        ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, chunkX, chunkZ, false);
        
        BlockFalling.fallInstantly = false;
    }
    
    /*
     * Handled in generateChunk
     */
    @Override
    public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer chunkPrimer, Biome[] biomes) {}

    @Override
    public boolean generateStructures(Chunk chunk, int chunkX, int chunkZ) {
        if (this.chunkSource.skipChunk(chunkX, chunkZ)) {
            return false;
        }
        
        boolean generated = false;
        
        if (this.structureMap.containsKey(ModernBetaStructures.MONUMENT) && chunk.getInhabitedTime() < 3600L) {
            generated |= this.structureMap
                .get(ModernBetaStructures.MONUMENT)
                .generateStructure(this.world, this.random, new ChunkPos(chunkX, chunkZ));
        }
        
        return generated;
    }

    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        if (this.chunkSource.skipChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return ImmutableList.of();
        }
        
        Biome biome = this.world.getBiome(pos);
        
        if (creatureType == EnumCreatureType.MONSTER && this.structureMap.containsKey(ModernBetaStructures.TEMPLE)) {
            MapGenScatteredFeature feature = (MapGenScatteredFeature)this.structureMap.get(ModernBetaStructures.TEMPLE);
            
            if (feature.isSwampHut(pos)) {
                return feature.getMonsters();
            }
        }
        
        if (creatureType == EnumCreatureType.MONSTER && this.structureMap.containsKey(ModernBetaStructures.MONUMENT)) {
            StructureOceanMonument feature = (StructureOceanMonument)this.structureMap.get(ModernBetaStructures.MONUMENT);
            
            if (feature.isPositionInStructure(this.world, pos)) {
                return feature.getMonsters();
            }
        }
        
        List<Biome.SpawnListEntry> spawnEntries = new ArrayList<>(biome.getSpawnableList(creatureType));
        ModernBetaBiomeMobs.modifySpawnList(spawnEntries, creatureType, biome, this.settings);
        
        return spawnEntries;
    }

    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored) {
        if (this.chunkSource.skipChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return null;
        }

        if (this.structureMap.isEmpty()) {
            return null;
        }
        
        ResourceLocation structureKey = new ResourceLocation(structureName.toLowerCase());
        if (this.structureMap.containsKey(structureKey)) {
            return this.structureMap.get(structureKey).getNearestStructurePos(world, pos, findUnexplored);
        }
        
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunk, int chunkX, int chunkZ) {
        if (this.chunkSource.skipChunk(chunkX, chunkZ)) {
            return;
        }

        if (this.structureMap.isEmpty()) {
            return;
        }

        for (MapGenStructure structure : this.structureMap.values()) {
            structure.generate(this.world, chunkX, chunkZ, null);
        }
    }

    @Override
    public boolean isInsideStructure(World world, String structureName, BlockPos pos) {
        if (this.chunkSource.skipChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            return false;
        }

        if (this.structureMap.isEmpty()) {
            return false;
        }
        
        ResourceLocation structureKey = new ResourceLocation(structureName.toLowerCase());
        if (this.structureMap.containsKey(structureKey)) {
            return this.structureMap.get(structureKey).isInsideStructure(pos);
        }

        return false;
    }
    
    public ChunkCache<ComponentChunk> getComponentCache() {
        return this.componentCache;
    }
    
    public Biome[] getBiomes(int chunkX, int chunkZ) {
        return this.initialChunkCache.get(chunkX, chunkZ).biomes;
    }
    
    public ChunkSource getChunkSource() {
        return this.chunkSource;
    }
    
    public ModernBetaGeneratorSettings getGeneratorSettings() {
        return this.settings;
    }

    private Map<ResourceLocation, MapGenStructure> initStructures(
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaGeneratorSettings settings,
        boolean mapFeaturesEnabled
    ) {
        Map<ResourceLocation, MapGenStructure> structureMap = new LinkedHashMap<>();
        
        if (mapFeaturesEnabled) {
            if (settings.useVillages) {
                structureMap.put(
                    ModernBetaStructures.VILLAGE,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new MapGenVillage(), EventType.VILLAGE)
                );
            }
            
            if (settings.useStrongholds) {
                structureMap.put(
                    ModernBetaStructures.STRONGHOLD,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new MapGenStronghold(), EventType.STRONGHOLD)
                );
            }
            
            if (settings.useMineShafts) {
                structureMap.put(
                    ModernBetaStructures.MINESHAFT,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new MapGenMineshaft(), EventType.MINESHAFT)
                );
            }
            
            if (settings.useMonuments) {
                structureMap.put(
                    ModernBetaStructures.MONUMENT,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new StructureOceanMonument(), EventType.OCEAN_MONUMENT)
                );
            }
            
            if (settings.useMansions) {
                structureMap.put(
                    ModernBetaStructures.MANSION,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new WoodlandMansion(chunkGenerator), EventType.WOODLAND_MANSION)
                );
            }
            
            if (settings.useTemples) {
                structureMap.put(
                    ModernBetaStructures.TEMPLE,
                    (MapGenStructure)TerrainGen.getModdedMapGen(new MapGenScatteredFeature(), EventType.SCATTERED_FEATURE)
                );
            }
        }
        
        return structureMap;
    }
    
    private Map<ResourceLocation, MapGenBase> initCarvers(ModernBetaGeneratorSettings settings) {
        Map<ResourceLocation, MapGenBase> carverMap = new LinkedHashMap<>();
        
        if (settings.useCaves) {
            carverMap.put(
                new ResourceLocation("cave"),
                TerrainGen.getModdedMapGen(
                    ModernBetaRegistries.CAVE_CARVER
                        .get(new ResourceLocation(settings.caveCarver))
                        .apply(this.getChunkSource(), settings),
                    InitMapGenEvent.EventType.CAVE
                )
            );
        }
        
        if (settings.useRavines) {
            carverMap.put(
                new ResourceLocation("ravine"),
                TerrainGen.getModdedMapGen(new MapGenRavine(), InitMapGenEvent.EventType.RAVINE)
            );
        }
        
        return carverMap;
    }
    
    private ChunkPrimerContainer provideInitialChunkPrimerContainer(int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPrimer chunkPrimer = new ChunkPrimer();
        Biome[] biomes = new Biome[256];
        
        // Generate base terrain
        this.chunkSource.provideInitialChunk(this.world, chunkPrimer, chunkX, chunkZ);
        
        // Generate base biome map
        this.biomeProvider.getBaseBiomes(biomes, startX, startZ, 16, 16);
        
        // Post-process biome map, before surface generation
        if (this.biomeInjector != null) {
            this.biomeInjector.injectBiomes(this.world, biomes, chunkPrimer, this.chunkSource, chunkX, chunkZ, BiomeInjectionStep.PRE_SURFACE);
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
