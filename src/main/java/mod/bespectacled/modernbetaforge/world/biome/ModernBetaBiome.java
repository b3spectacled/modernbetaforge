package mod.bespectacled.modernbetaforge.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenFancyOak;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

public abstract class ModernBetaBiome extends Biome {
    private static final List<SpawnListEntry> EMPTY_LIST = ImmutableList.of();
    
    public static final WorldGenTrees TREE_FEATURE = new WorldGenTrees(false);
    public static final WorldGenFancyOak BIG_TREE_FEATURE = new WorldGenFancyOak(false);
    
    protected static final float BASE_HEIGHT_LOW = 0.1f;
    protected static final float HEIGHT_VARY_LOW = 0.2f;
    
    protected static final float BASE_HEIGHT_TEMPERATE = 0.1f;
    protected static final float HEIGHT_VARY_TEMPERATE = 0.35f;
    
    protected static final float BASE_HEIGHT_WET = 0.1f;
    protected static final float HEIGHT_VARY_WET = 0.45f;
    
    protected static final float BASE_HEIGHT_BEACH = 0.0f;
    protected static final float HEIGHT_VARY_BEACH = 0.025f;
    
    protected static final float BASE_HEIGHT_OCEAN = -1.0f;
    protected static final float HEIGHT_VARY_OCEAN = 0.1f;
    
    protected static final SpawnListEntry SHEEP = new SpawnListEntry(EntitySheep.class, 12, 4, 4);
    protected static final SpawnListEntry PIG = new SpawnListEntry(EntityPig.class, 10, 4, 4);
    protected static final SpawnListEntry CHICKEN = new SpawnListEntry(EntityChicken.class, 10, 4, 4);
    protected static final SpawnListEntry COW = new SpawnListEntry(EntityCow.class, 8, 4, 4);
    
    protected static final SpawnListEntry WOLF_FOREST = new SpawnListEntry(EntityWolf.class, 5, 4, 4);
    protected static final SpawnListEntry WOLF_TAIGA = new SpawnListEntry(EntityWolf.class, 8, 4, 4);

    protected static final SpawnListEntry SPIDER = new SpawnListEntry(EntitySpider.class, 100, 4, 4);
    protected static final SpawnListEntry ZOMBIE = new SpawnListEntry(EntityZombie.class, 95, 4, 4);
    protected static final SpawnListEntry SKELETON = new SpawnListEntry(EntitySkeleton.class, 100, 4, 4);
    protected static final SpawnListEntry CREEPER = new SpawnListEntry(EntityCreeper.class, 100, 4, 4);
    protected static final SpawnListEntry SLIME = new SpawnListEntry(EntitySlime.class, 100, 4, 4);
    
    protected static final SpawnListEntry ENDERMAN = new SpawnListEntry(EntityEnderman.class, 10, 1, 4);
    protected static final SpawnListEntry WITCH = new SpawnListEntry(EntityWitch.class, 5, 1, 1);
    protected static final SpawnListEntry ZOMBIE_VILLAGER = new SpawnListEntry(EntityZombieVillager.class, 5, 1, 1);
    
    protected static final SpawnListEntry SQUID = new SpawnListEntry(EntitySquid.class, 10, 4, 4);
    protected static final SpawnListEntry BAT = new SpawnListEntry(EntityBat.class, 10, 8, 8);
    
    protected final List<SpawnListEntry> additionalMonsters;
    protected final List<SpawnListEntry> additionalCreatures;
    protected final List<SpawnListEntry> additionalWolves;
    protected final List<SpawnListEntry> additionalWaterCreatures;
    protected final List<SpawnListEntry> additionalCaveCreatures;
    
    protected int skyColor;
    protected int fogColor;
    
    protected int grassColor;
    protected int foliageColor;
    
    public ModernBetaBiome(BiomeProperties properties) {
        super(properties);
        
        this.skyColor = -1;
        this.fogColor = -1;
        
        this.grassColor = -1;
        this.foliageColor = -1;
        
        this.additionalMonsters = new ArrayList<>();
        this.additionalCreatures = new ArrayList<>();
        this.additionalWolves = new ArrayList<>();
        this.additionalWaterCreatures = new ArrayList<>();
        this.additionalCaveCreatures = new ArrayList<>();
        
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        
        this.populateAdditionalMobs(EnumCreatureType.MONSTER, false, ENDERMAN, WITCH, ZOMBIE_VILLAGER);
        this.populateAdditionalMobs(EnumCreatureType.WATER_CREATURE, false, SQUID);
        this.populateAdditionalMobs(EnumCreatureType.AMBIENT, false, BAT);
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        if (this.skyColor != -1)
            return this.skyColor;
        
        return super.getSkyColorByTemp(temp);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        if (this.grassColor != -1)
            return this.grassColor;
        
        return super.getGrassColorAtPos(blockPos);
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        if (this.foliageColor != -1)
            return this.foliageColor;
        
        return super.getFoliageColorAtPos(blockPos);
    }
    
    public Optional<Integer> getFogColor() {
        Optional<Integer> fogColor = Optional.empty();
        
        if (this.fogColor != -1)
            fogColor = Optional.of(this.fogColor);
        
        return fogColor;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        return (random.nextInt(10) == 0) ? BIG_TREE_FEATURE : TREE_FEATURE;
    }
    
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        return this.getRandomTreeFeature(random);
    }
    
    public List<Biome.SpawnListEntry> getAdditionalSpawnableList(EnumCreatureType creatureType, boolean addWolves) {
        if (addWolves)
            return this.additionalWolves;
        
        switch (creatureType) {
            case MONSTER:
                return this.additionalMonsters;
            case CREATURE:
                return this.additionalCreatures;
            case WATER_CREATURE:
                return this.additionalWaterCreatures;
            case AMBIENT:
                return this.additionalCaveCreatures;
            default:
                return EMPTY_LIST;
        }
    }
    
    protected void populateSpawnableMobs(EnumCreatureType creatureType, SpawnListEntry... entries) {
        @SuppressWarnings("unchecked")
        List<SpawnListEntry> spawnEntries = Arrays.asList(entries);
        
        switch (creatureType) {
            case MONSTER:
                this.spawnableMonsterList.addAll(spawnEntries);
                break;
            case CREATURE:
                this.spawnableCreatureList.addAll(spawnEntries);
                break;
            case WATER_CREATURE:
                this.spawnableWaterCreatureList.addAll(spawnEntries);
                break;
            case AMBIENT:
                this.spawnableCaveCreatureList.addAll(spawnEntries);
                break;
        }
    }
    
    protected void populateAdditionalMobs(EnumCreatureType creatureType, boolean addWolves, SpawnListEntry... entries) {
        @SuppressWarnings("unchecked")
        List<SpawnListEntry> spawnEntries = Arrays.asList(entries);
        
        if (addWolves) {
            this.additionalWolves.addAll(spawnEntries);
            return;
        }
        
        switch (creatureType) {
            case MONSTER:
                this.additionalMonsters.addAll(spawnEntries);
                break;
            case CREATURE:
                this.additionalCreatures.addAll(spawnEntries);
                break;
            case WATER_CREATURE:
                this.additionalWaterCreatures.addAll(spawnEntries);
                break;
            case AMBIENT:
                this.additionalCaveCreatures.addAll(spawnEntries);
                break;
        }
    }
}
