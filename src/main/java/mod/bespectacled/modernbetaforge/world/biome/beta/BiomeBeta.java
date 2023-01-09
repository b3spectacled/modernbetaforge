package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Optional;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeBeta extends ModernBetaBiome {
    protected static final WorldGenBirchTree BIRCH_TREE_FEATURE = new WorldGenBirchTree(false, false);
    protected static final WorldGenTaiga1 PINE_FEATURE_1 = new WorldGenTaiga1();
    protected static final WorldGenTaiga2 PINE_FEATURE_2 = new WorldGenTaiga2(false);
    
    protected int skyColor;

    private Optional<ClimateSampler> climateSampler;
    private Optional<SkyClimateSampler> skyClimateSampler;
    
    public BiomeBeta(BiomeProperties properties) {
        super(properties);

        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
        
        this.skyColor = -1;
    }
    
    public void resetClimateSamplers() {
        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
    }
    
    public void setClimateSamplers(ClimateSampler climateSampler, SkyClimateSampler skyClimateSampler) {
        this.climateSampler = Optional.ofNullable(climateSampler);
        this.skyClimateSampler = Optional.ofNullable(skyClimateSampler);
    }
    
    public Optional<ClimateSampler> getClimateSampler() {
        return this.climateSampler;
    }
    
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return this.getModdedBiomeDecorator(new BiomeDecoratorBeta());
    }
    
    @SideOnly(Side.CLIENT)
    public int getSkyColorByTemp(float originalTemp) {
        BlockPos blockPos = Minecraft.getMinecraft().player.getPosition();
        
        if (this.skyClimateSampler.isPresent() && this.skyClimateSampler.get().sampleSkyColor()) {
            float temp = (float)this.skyClimateSampler.get().sampleSkyTemp(blockPos.getX(), blockPos.getZ());
            temp /= 3F;
            temp = MathHelper.clamp(temp, -1F, 1F);
            
            return MathHelper.hsvToRGB(0.6222222F - temp * 0.05F, 0.5F + temp * 0.1F, 1.0F);
        }
        
        if (this.skyColor != -1) {
            return this.skyColor;
        }
        
        return super.getSkyColorByTemp(originalTemp);
    }
    
    @SideOnly(Side.CLIENT)
    public int getGrassColorAtPos(BlockPos blockPos) {
        if (this.climateSampler.isPresent() && this.climateSampler.get().sampleBiomeColor()) {
            Clime clime = this.climateSampler.get().sample(blockPos.getX(), blockPos.getZ());
            
            return ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
        }
        
        return super.getGrassColorAtPos(blockPos);
    }
    
    @SideOnly(Side.CLIENT)
    public int getFoliageColorAtPos(BlockPos blockPos) {
        if (this.climateSampler.isPresent() && this.climateSampler.get().sampleBiomeColor()) {
            Clime clime = this.climateSampler.get().sample(blockPos.getX(), blockPos.getZ());
            
            return ColorizerFoliage.getFoliageColor(clime.temp(), clime.rain());
        }
        
        return super.getFoliageColorAtPos(blockPos);
    }
    
    public static boolean canSetIce(World world, BlockPos blockPos, boolean doWaterCheck, double temp) {
        if (temp >= 0.5D) {
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
        if (temp >= 0.5D) {
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
