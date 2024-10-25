package mod.bespectacled.modernbetaforge.world.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.feature.WorldGenFancyOak;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ModernBetaBiome extends Biome {
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
    
    protected static final SpawnListEntry SHEEP = new Biome.SpawnListEntry(EntitySheep.class, 12, 4, 4);
    protected static final SpawnListEntry PIG = new Biome.SpawnListEntry(EntityPig.class, 10, 4, 4);
    protected static final SpawnListEntry CHICKEN = new Biome.SpawnListEntry(EntityChicken.class, 10, 4, 4);
    protected static final SpawnListEntry COW = new Biome.SpawnListEntry(EntityCow.class, 8, 4, 4);
    
    protected static final SpawnListEntry WOLF_FOREST = new SpawnListEntry(EntityWolf.class, 5, 4, 4);
    protected static final SpawnListEntry WOLF_TAIGA = new SpawnListEntry(EntityWolf.class, 8, 4, 4);

    protected static final SpawnListEntry SPIDER = new Biome.SpawnListEntry(EntitySpider.class, 100, 4, 4);
    protected static final SpawnListEntry ZOMBIE = new Biome.SpawnListEntry(EntityZombie.class, 95, 4, 4);
    protected static final SpawnListEntry SKELETON = new Biome.SpawnListEntry(EntitySkeleton.class, 100, 4, 4);
    protected static final SpawnListEntry CREEPER = new Biome.SpawnListEntry(EntityCreeper.class, 100, 4, 4);
    protected static final SpawnListEntry SLIME = new Biome.SpawnListEntry(EntitySlime.class, 100, 4, 4);
    
    protected static final SpawnListEntry ENDERMAN = new Biome.SpawnListEntry(EntityEnderman.class, 10, 1, 4);
    protected static final SpawnListEntry WITCH = new Biome.SpawnListEntry(EntityWitch.class, 5, 1, 1);
    protected static final SpawnListEntry ZOMBIE_VILLAGER = new Biome.SpawnListEntry(EntityZombieVillager.class, 5, 1, 1);
    
    protected final List<SpawnListEntry> additionalMonsters;
    protected final List<SpawnListEntry> additionalCreatures;
    protected final List<SpawnListEntry> additionalWolves;
    
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

        this.populateSpawnableCreatures();
        this.populateSpawnableMonsters();
        this.populateAdditionalCreatures();
        this.populateAdditionalMonsters();
        this.populateAdditionalWolves();
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
    
    public List<SpawnListEntry> getAdditionalCreatures() {
        return this.additionalCreatures;
    }

    public List<SpawnListEntry> getAdditionalMonsters() {
        return this.additionalMonsters;
    }
    
    public List<SpawnListEntry> getAdditionalWolves() {
        return this.additionalWolves;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        return (random.nextInt(10) == 0) ? BIG_TREE_FEATURE : TREE_FEATURE;
    }
    
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        return this.getRandomTreeFeature(random);
    }
    
    protected void populateAdditionalCreatures() { }

    protected void populateAdditionalMonsters() {
        this.additionalMonsters.add(ENDERMAN);
        this.additionalMonsters.add(WITCH);
        this.additionalMonsters.add(ZOMBIE_VILLAGER);
    }
    
    protected void populateAdditionalWolves() { }
    
    protected void populateSpawnableCreatures() {
        this.spawnableCreatureList.clear();
        
        this.spawnableCreatureList.add(SHEEP);
        this.spawnableCreatureList.add(PIG);
        this.spawnableCreatureList.add(CHICKEN);
        this.spawnableCreatureList.add(COW);
    }

    protected void populateSpawnableMonsters() {
        this.spawnableMonsterList.clear();
        
        this.spawnableMonsterList.add(SPIDER);
        this.spawnableMonsterList.add(ZOMBIE);
        this.spawnableMonsterList.add(SKELETON);
        this.spawnableMonsterList.add(CREEPER);
        this.spawnableMonsterList.add(SLIME);
    }
}
