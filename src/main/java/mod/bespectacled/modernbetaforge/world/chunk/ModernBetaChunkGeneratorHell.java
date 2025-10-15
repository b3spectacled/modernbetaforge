package mod.bespectacled.modernbetaforge.world.chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeDecorator;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBetaCaveHell;
import mod.bespectacled.modernbetaforge.world.feature.OreType;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenHellSpring;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ModernBetaChunkGeneratorHell extends ChunkGeneratorHell {
    private static final Predicate<IBlockState> NETHERRACK_PREDICATE = BlockMatcher.forBlock(Blocks.NETHERRACK);
    
    private final ModernBetaGeneratorSettings settings;
    private final World world;
    private final boolean mapFeaturesEnabled;
    
    private final WorldGenFire fireFeature;
    private final WorldGenGlowStone1 glowstoneFeature1;
    private final WorldGenGlowStone2 glowstoneFeature2;
    private final WorldGenerator quartzOreFeature;
    private final WorldGenerator magmaOreFeature;
    private final WorldGenHellLava lavaPocketFeature;
    private final WorldGenHellSpring lavaSpringFeature;
    private final WorldGenBush brownMushroomFeature;
    private final WorldGenBush redMushroomFeature;
   
    private final MapGenNetherBridge netherFortressGenerator;
    private final MapGenBase netherCaveCarver;
    
    public ModernBetaChunkGeneratorHell(World world, boolean mapFeaturesEnabled, long seed, String generatorOptions) {
        super(world, mapFeaturesEnabled, seed);
        
        this.settings = ModernBetaGeneratorSettings.build(generatorOptions);
        this.world = world;
        this.mapFeaturesEnabled = mapFeaturesEnabled;
        
        OreType oreType = OreType.fromId(this.settings.oreType);
        
        this.fireFeature = new WorldGenFire();
        this.glowstoneFeature1 = new WorldGenGlowStone1();
        this.glowstoneFeature2 = new WorldGenGlowStone2();
        this.quartzOreFeature = ModernBetaBiomeDecorator.createMinable(BlockStates.QUARTZ_ORE, settings.quartzSize, NETHERRACK_PREDICATE, oreType);
        this.magmaOreFeature = ModernBetaBiomeDecorator.createMinable(BlockStates.MAGMA, settings.magmaSize, NETHERRACK_PREDICATE, oreType);
        this.lavaPocketFeature = new WorldGenHellLava(Blocks.FLOWING_LAVA, true);
        this.lavaSpringFeature = new WorldGenHellSpring(Blocks.FLOWING_LAVA);
        this.brownMushroomFeature = new WorldGenBush(Blocks.BROWN_MUSHROOM);
        this.redMushroomFeature = new WorldGenBush(Blocks.RED_MUSHROOM);
       
        this.netherFortressGenerator = (MapGenNetherBridge)TerrainGen.getModdedMapGen(new MapGenNetherBridge(), InitMapGenEvent.EventType.NETHER_BRIDGE);
        this.netherCaveCarver = TerrainGen.getModdedMapGen(new MapGenBetaCaveHell(), InitMapGenEvent.EventType.NETHER_CAVE);
    }
    
    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        // Grab random from ChunkGeneratorHell superclass and set for surface generation
        this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

        // Generate base terrain and biome-specific surface
        this.prepareHeights(chunkX, chunkZ, chunkprimer);
        this.buildSurfaces(chunkX, chunkZ, chunkprimer);

        // Carve terrain
        if (this.settings.useNetherCaves)
            this.netherCaveCarver.generate(this.world, chunkX, chunkZ, chunkprimer);

        // Generate map feature placements
        if (this.mapFeaturesEnabled) {
            if (this.settings.useFortresses) {
                this.netherFortressGenerator.generate(this.world, chunkX, chunkZ, chunkprimer);
            }
        }

        // Generate final chunk
        Chunk chunk = new Chunk(this.world, chunkprimer, chunkX, chunkZ);

        // Generate biome map
        Biome[] biomes = this.world.getBiomeProvider().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
        
        // Set biome map in chunk
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
           biomeArray[i] = (byte)Biome.getIdForBiome(biomes[i]); 
        }

        chunk.resetRelightChecks();
        return chunk;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void populate(int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
        BlockPos startPos = new BlockPos(startX, 0, startZ);
        MutableBlockPos mutablePos = new MutableBlockPos(startPos);
        
        Biome biome = this.world.getBiome(mutablePos.setPos(startX + 16, 0, startZ + 16));
        Random random = this.rand;
        
        ForgeEventFactory.onChunkPopulate(true, this, this.world, random, chunkX, chunkZ, false);
     
        // Actually generate map features here
        if (this.mapFeaturesEnabled) {
            if (this.settings.useFortresses) {
                this.netherFortressGenerator.generateStructure(this.world, random, chunkpos);
            }
        }

        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_LAVA)) {
            for (int i = 0; i < 8; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(120) + 4;
                int z = startZ + random.nextInt(16) + 8;
                
                this.lavaSpringFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.FIRE)) {
            for (int i = 0; i < random.nextInt(random.nextInt(10) + 1) + 1; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(120) + 4;
                int z = startZ + random.nextInt(16) + 8;
                
                this.fireFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.GLOWSTONE)) {
            for (int i = 0; i < random.nextInt(random.nextInt(10) + 1); ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(120) + 4;
                int z = startZ + random.nextInt(16) + 8;
                
                this.glowstoneFeature1.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
    
            for (int i = 0; i < 10; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                this.glowstoneFeature2.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        ForgeEventFactory.onChunkPopulate(false, this, this.world, random, chunkX, chunkZ, false);
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(this.world, random, chunkpos));

        if (TerrainGen.decorate(this.world, random, chunkpos, DecorateBiomeEvent.Decorate.EventType.SHROOM)) {
            if (random.nextBoolean()) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                this.brownMushroomFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
            

            if (random.nextBoolean()) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(128);
                int z = startZ + random.nextInt(16) + 8;
                
                this.redMushroomFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.generateOre(this.world, random, this.quartzOreFeature, startPos, OreGenEvent.GenerateMinable.EventType.QUARTZ)) {
            for (int i = 0; i < this.settings.quartzCount; ++i) {
                int x = startX + random.nextInt(16);
                int y = random.nextInt(108) + 10;
                int z = startZ + random.nextInt(16);
                
                this.quartzOreFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        if (TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_MAGMA)) {
            int netherSeaLevel = this.world.getSeaLevel() / 2 + 1;
            
            for (int i = 0; i < this.settings.magmaCount; ++i) {
                int x = startX + random.nextInt(16);
                int y = netherSeaLevel - 5 + random.nextInt(10);
                int z = startZ + random.nextInt(16);
                
                this.magmaOreFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }
        
        if (this.settings.useLavaPockets && TerrainGen.populate(this, this.world, random, chunkX, chunkZ, false, PopulateChunkEvent.Populate.EventType.NETHER_LAVA2)) {
            for (int i = 0; i < 16; ++i) {
                int x = startX + random.nextInt(16) + 8;
                int y = random.nextInt(108) + 10;
                int z = startZ + random.nextInt(16) + 8;
                
                this.lavaPocketFeature.generate(this.world, random, mutablePos.setPos(x, y, z));
            }
        }

        biome.decorate(this.world, random, new BlockPos(startX, 0, startZ));
        
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(this.world, random, startPos));

        BlockFalling.fallInstantly = false;
    }
    
    @Override
    public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        List<SpawnListEntry> spawnEntries = new ArrayList<>(this.getPossibleCreaturesInclStructures(creatureType, pos));
        
        if (!this.settings.spawnNewMonsterMobs) {
            Iterator<SpawnListEntry> iterator = spawnEntries.iterator();
            
            while (iterator.hasNext()) {
                SpawnListEntry spawnEntry = iterator.next();

                if (spawnEntry.entityClass == EntityMagmaCube.class ||
                    spawnEntry.entityClass == EntityEnderman.class ||
                    spawnEntry.entityClass == EntityWitherSkeleton.class
                ) {
                    iterator.remove();
                }
            }
        }
        
        return spawnEntries;
    }
    
    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        if (!this.mapFeaturesEnabled) {
            return false;
        }
        
        if (this.settings.useFortresses && "Fortress".equals(structureName) && this.netherFortressGenerator != null) {
            return this.netherFortressGenerator.isInsideStructure(pos);
        }
        
        return false;
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World world, String structureName, BlockPos position, boolean findUnexplored) {
        if (!this.mapFeaturesEnabled) {
            return null;
        }
        
        if (this.settings.useFortresses && "Fortress".equals(structureName) && this.netherFortressGenerator != null) {
            return this.netherFortressGenerator.getNearestStructurePos(world, position, findUnexplored);
        }
        
        return null;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {
        if (!this.mapFeaturesEnabled) {
            return;
        }
        
        if (this.settings.useFortresses) {
            this.netherFortressGenerator.generate(this.world, x, z, null);
        }
    }
    
    private List<SpawnListEntry> getPossibleCreaturesInclStructures(EnumCreatureType creatureType, BlockPos pos) {
        if (creatureType == EnumCreatureType.MONSTER) {
            if (this.mapFeaturesEnabled && this.settings.useFortresses && this.netherFortressGenerator.isInsideStructure(pos)) {
                return this.netherFortressGenerator.getSpawnList();
            }

            if (this.mapFeaturesEnabled &&
                this.settings.useFortresses &&
                this.netherFortressGenerator.isPositionInStructure(this.world, pos) &&
                this.world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICK
            ) {
                return this.netherFortressGenerator.getSpawnList();
            }
        }

        return this.world.getBiome(pos).getSpawnableList(creatureType);
    }
}
