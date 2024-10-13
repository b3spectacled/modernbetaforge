package mod.bespectacled.modernbetaforge.world.biome.biomes.beta;

import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BiomeBeta extends ModernBetaBiome {
    protected int skyColor;
    
    public BiomeBeta(BiomeProperties properties) {
        super(properties);
        
        this.skyColor = -1;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorBeta());
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float temp) {
        BlockPos blockPos = Minecraft.getMinecraft().player.getPosition();
        
        if (BetaColorSampler.INSTANCE.canSampleSkyColor()) {
            return BetaColorSampler.INSTANCE.getSkyColor(blockPos);
        }
        
        if (this.skyColor != -1) {
            return this.skyColor;
        }
        
        return super.getSkyColorByTemp(temp);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        if (BetaColorSampler.INSTANCE.canSampleBiomeColor()) {
            return BetaColorSampler.INSTANCE.getGrassColor(blockPos);
        }
        
        return super.getGrassColorAtPos(blockPos);
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        if (BetaColorSampler.INSTANCE.canSampleBiomeColor()) {
            return BetaColorSampler.INSTANCE.getFoliageColor(blockPos);
        }
        
        return super.getFoliageColorAtPos(blockPos);
    }
    
    public static boolean canSetIce(World world, BlockPos blockPos, boolean doWaterCheck, double temp) {
        if (temp >= 0.5) {
            return false;
        }
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, blockPos) < 10) {
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            boolean isWater = block == Blocks.WATER || block == Blocks.FLOWING_WATER;
            
            if (isWater && blockState.getValue(BlockLiquid.LEVEL) == 0) {
                if (!doWaterCheck) {
                    return true;
                }
                
                boolean submerged = 
                    isWater(world, blockPos.west()) && 
                    isWater(world, blockPos.east()) && 
                    isWater(world, blockPos.north()) && 
                    isWater(world, blockPos.south());
                
                if (!submerged) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canSetSnow(World world, BlockPos blockPos, double temp) {
        if (temp >= 0.5) {
            return false;
        }
        if (blockPos.getY() >= 0 && blockPos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, blockPos) < 10) {
            IBlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock().isAir(blockState, world, blockPos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, blockPos)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isWater(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos).getMaterial() == Material.WATER;
    }
}
