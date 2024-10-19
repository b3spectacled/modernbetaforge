package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeBetaRainforest extends BiomeBeta {
    public BiomeBetaRainforest() {
        super(new BiomeProperties("Beta Rainforest")
            .setTemperature(1.0f)
            .setRainfall(1.0f)
            .setBaseHeight(0.37f)
            .setHeightVariation(0.5f)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableMonsterList.add(new SpawnListEntry(EntityOcelot.class, 2, 1, 1));
            this.spawnableCreatureList.add(new SpawnListEntry(EntityParrot.class, 40, 1, 2));
        }

        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.useJungleTrees)
            return this.getRandomTreeFeature(random);
        
        if (random.nextInt(5) == 0) {
            return new WorldGenTrees(false, 4 + random.nextInt(4), BlockStates.JUNGLE_LOG, BlockStates.JUNGLE_LEAVES, false);
        }
        
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
    
    @Override
    public void decorate(World world, Random random, BlockPos startPos) {
        super.decorate(world, random, startPos);
        
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
        ChunkPos chunkPos = new ChunkPos(startPos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        if (settings.useMelons && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.PUMPKIN)) {
            if (random.nextInt(4) == 0) {
                int x = startPos.getX() + random.nextInt(16) + 8;
                int y = random.nextInt(settings.height);
                int z = startPos.getZ() + random.nextInt(16) + 8;
                
                new WorldGenMelon().generate(world, random, mutablePos.setPos(x, y, z));
            }
        }
    }
}
