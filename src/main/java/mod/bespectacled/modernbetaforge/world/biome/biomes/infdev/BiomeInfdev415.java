package mod.bespectacled.modernbetaforge.world.biome.biomes.infdev;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeInfdev415 extends ModernBetaBiome {
    public BiomeInfdev415() {
        super(new BiomeProperties("Infdev 415")
            .setTemperature(0.5f)
            .setRainfall(0.5f)
        );
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorInfdev415());
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float originalTemp) {
        return ModernBetaBiomeColors.INFDEV_415_SKY_COLOR;
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        return ModernBetaBiomeColors.OLD_GRASS_COLOR;
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        return ModernBetaBiomeColors.OLD_FOLIAGE_COLOR;
    }
}
