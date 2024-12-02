package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeBetaForest extends BiomeBeta {
    private static final WorldGenBirchTree BIRCH_TREE_FEATURE = new WorldGenBirchTree(false, false);
    
    public BiomeBetaForest() {
        super(new BiomeProperties("Beta Forest")
            .setTemperature(0.7f)
            .setRainfall(0.8f)
            .setBaseHeight(BASE_HEIGHT_TEMPERATE)
            .setHeightVariation(HEIGHT_VARY_TEMPERATE)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_TEMP_SKY_COLOR;
        
        this.populateAdditionalMobs(null, true, WOLF_FOREST);
    }
    
    @Override
    public void decorate(World world, Random random, BlockPos startPos) {
        super.decorate(world, random, startPos);
        
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.build(world.getWorldInfo().getGeneratorOptions());
        ChunkPos chunkPos = new ChunkPos(startPos);
        
        if (settings.useNewFlowers && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            this.addDoublePlants(world, random, startPos, random.nextInt(5) - 3);
        }
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(5) == 0) {
            return BIRCH_TREE_FEATURE;
        }
        
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaChunkGeneratorSettings settings) {
        if (!settings.useBirchTrees)
            // Revert to pre-Beta behavior of spawning fancy oaks with 1/10 chance instead of 1/3
            return super.getRandomTreeFeature(random);
        
        return this.getRandomTreeFeature(random);
    }
    
    public void addDoublePlants(World world, Random random, BlockPos startPos, int flowerNdx) {
        for (int i = 0; i < flowerNdx; ++i) {
            int flowerType = random.nextInt(3);

            if (flowerType == 0) {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SYRINGA);
                
            } else if (flowerType == 1) {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.ROSE);
                
            } else if (flowerType == 2) {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.PAEONIA);
                
            }

            for (int j = 0; j < 5; ++j) {
                int x = random.nextInt(16) + 8;
                int y = random.nextInt(16) + 8;
                int z = random.nextInt(world.getHeight(startPos.add(x, 0, y)).getY() + 32);

                if (DOUBLE_PLANT_GENERATOR.generate(world, random, new BlockPos(startPos.getX() + x, z, startPos.getZ() + y))) {
                    break;
                }
            }
        }
    }
}
