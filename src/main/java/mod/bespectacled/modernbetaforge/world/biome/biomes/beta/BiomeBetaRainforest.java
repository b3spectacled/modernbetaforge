package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.entity.EnumCreatureType;
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
            .setBaseHeight(BASE_HEIGHT_WET)
            .setHeightVariation(HEIGHT_VARY_WET)
        );
        
        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
        
        this.populateAdditionalMobs(EnumCreatureType.CREATURE, false, PARROT);
        this.populateAdditionalMobs(EnumCreatureType.MONSTER, false, OCELOT);
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random) {
        if (random.nextInt(3) == 0) {
            return ModernBetaBiome.BIG_TREE_FEATURE;
        }
        
        return ModernBetaBiome.TREE_FEATURE;
    }
    
    @Override
    public WorldGenAbstractTree getRandomTreeFeature(Random random, ModernBetaGeneratorSettings settings) {
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
        
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.build(world.getWorldInfo().getGeneratorOptions());
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
