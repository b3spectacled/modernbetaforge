package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeBetaPlains extends BiomeBeta {
    public BiomeBetaPlains() {
        super(new BiomeProperties("Beta Plains")
            .setTemperature(1.0f)
            .setRainfall(0.4f)
            .setBaseHeight(BASE_HEIGHT_LOW)
            .setHeightVariation(HEIGHT_VARY_LOW)
        );

        this.skyColor = ModernBetaBiomeColors.BETA_WARM_SKY_COLOR;
        
        this.populateAdditionalMobs(EnumCreatureType.CREATURE, false, HORSE_PLAINS, DONKEY_PLAINS);
    }
    
    @Override
    public EnumFlowerType pickRandomFlower(Random random, BlockPos blockPos) {
        double flowerNoise = Biome.GRASS_COLOR_NOISE.getValue(blockPos.getX() / 200.0, blockPos.getZ() / 200.0);
        
        if (flowerNoise > -0.2) {
            int randFlower = random.nextInt(4);
            
            switch (randFlower) {
                case 0: return BlockFlower.EnumFlowerType.ORANGE_TULIP;
                case 1: return BlockFlower.EnumFlowerType.RED_TULIP;
                case 2: return BlockFlower.EnumFlowerType.PINK_TULIP;
                default: return BlockFlower.EnumFlowerType.WHITE_TULIP;
            }
        } else {
            return random.nextInt(3) == 1 ? EnumFlowerType.HOUSTONIA : EnumFlowerType.OXEYE_DAISY;
        }
    }
    
    @Override
    public void decorate(World world, Random random, BlockPos startPos) {
        super.decorate(world, random, startPos);
        
        ModernBetaGeneratorSettings settings = ModernBetaGeneratorSettings.build(world.getWorldInfo().getGeneratorOptions());
        ChunkPos chunkPos = new ChunkPos(startPos);
        
        if (settings.useNewFlowers && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            for (int i = 0; i < 4; ++i) {
                int dX = random.nextInt(16) + 8;
                int dZ = random.nextInt(16) + 8;
                
                int height = world.getHeight(startPos.add(dX, 0, dZ)).getY() + 32;
                if (height > 0) {
                    int dY = random.nextInt(height);
                    BlockPos blockPos = startPos.add(dX, dY, dZ);
                    
                    EnumFlowerType enumFlowerType = this.pickRandomFlower(random, blockPos);
                    BlockFlower blockFlower = enumFlowerType.getBlockType().getBlock();
                    
                    if (blockFlower.getDefaultState().getMaterial() != Material.AIR) {
                        this.decorator.flowerGen.setGeneratedBlock(blockFlower, enumFlowerType);
                        this.decorator.flowerGen.generate(world, random, blockPos);
                    }
                }
            }
        }
    }
}
