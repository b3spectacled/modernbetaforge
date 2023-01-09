package mod.bespectacled.modernbetaforge.world.biome.alpha;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BiomeAlphaBase extends ModernBetaBiome {
    public BiomeAlphaBase(BiomeProperties properties) {
        super(properties);
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorAlpha());
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float originalTemp) {
        return ModernBetaBiomeColors.ALPHA_SKY_COLOR;
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
