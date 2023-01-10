package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Iterator;
import java.util.Random;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;
import net.minecraft.world.gen.feature.WorldGenFossils;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeBetaDesert extends BiomeBeta {
    public BiomeBetaDesert() {
        super(new BiomeProperties("Beta Desert")
            .setTemperature(1.0F)
            .setRainfall(0.0F)
            .setRainDisabled()
        );
        
        this.topBlock = BlockStates.SAND;
        this.fillerBlock = BlockStates.SAND;

        // Should always clear to prevent passive mobs from spawning in desert
        this.spawnableCreatureList.clear();
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 4, 2, 3));
            
            Iterator<SpawnListEntry> monsterIterator = this.spawnableMonsterList.iterator();
            while (monsterIterator.hasNext()) {
                SpawnListEntry spawnListEntry = monsterIterator.next();
                
                if (spawnListEntry.entityClass == EntityZombie.class || spawnListEntry.entityClass == EntityZombieVillager.class) {
                    monsterIterator.remove();
                }
            }
            
            this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 19, 4, 4));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityZombieVillager.class, 1, 1, 1));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityHusk.class, 80, 4, 4));
        }
        
        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }
    
    @Override
    public void decorate(World world, Random random, BlockPos startPos) {
        super.decorate(world, random, startPos);
        
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
        ChunkPos chunkPos = new ChunkPos(startPos);
        
        if (settings.useDesertWells && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.DESERT_WELL) && random.nextInt(1000) == 0) {
            int x = random.nextInt(16) + 8;
            int z = random.nextInt(16) + 8;
            BlockPos structurePos = world.getHeight(startPos.add(x, 0, z)).up();
            
            new WorldGenDesertWells().generate(world, random, structurePos);
        }
        
        if (settings.useFossils && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FOSSIL) && random.nextInt(64) == 0) {
            new WorldGenFossils().generate(world, random, startPos);
        }
    }
}
