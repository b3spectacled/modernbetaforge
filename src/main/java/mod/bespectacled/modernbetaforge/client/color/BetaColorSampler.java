package mod.bespectacled.modernbetaforge.client.color;

import java.util.Optional;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BetaColorSampler {
    public static final BetaColorSampler INSTANCE = new BetaColorSampler();
    
    private Optional<ClimateSampler> climateSampler;
    private Optional<SkyClimateSampler> skyClimateSampler;
    private int snowLineOffset;
    
    private BetaColorSampler() {
        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
        this.snowLineOffset = 64;
    }
    
    public void resetClimateSamplers() {
        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
    }
    
    public void setClimateSampler(ClimateSampler climateSampler) {
        this.setClimateSampler(climateSampler, 64);
    }
    
    public void setClimateSampler(ClimateSampler climateSampler, int snowLineOffset) {
        this.climateSampler = Optional.ofNullable(climateSampler);
        this.snowLineOffset = snowLineOffset;
    }
    
    public void setSkyClimateSampler(SkyClimateSampler skyClimateSampler) {
        this.skyClimateSampler = Optional.ofNullable(skyClimateSampler);
    }
    
    public int getSkyColor(BlockPos blockPos) {
        float temp = (float)this.skyClimateSampler.get().sampleSkyTemp(blockPos.getX(), blockPos.getZ());
        temp /= 3F;
        temp = MathHelper.clamp(temp, -1F, 1F);
        
        return MathHelper.hsvToRGB(0.6222222F - temp * 0.05F, 0.5F + temp * 0.1F, 1.0F);
    }
    
    public int getGrassColor(BlockPos blockPos) {
        Clime clime = this.climateSampler.get().sample(blockPos.getX(), blockPos.getZ());
        double temp = MathHelper.clamp(clime.temp() - getTempOffset(blockPos.getY(), this.snowLineOffset), 0.0, 1.0);
        
        return ColorizerGrass.getGrassColor(temp, clime.rain());
    }
    
    public int getFoliageColor(BlockPos blockPos) {
        Clime clime = this.climateSampler.get().sample(blockPos.getX(), blockPos.getZ());
        double temp = MathHelper.clamp(clime.temp() - getTempOffset(blockPos.getY(), this.snowLineOffset), 0.0, 1.0);
        
        return ColorizerFoliage.getFoliageColor(temp, clime.rain());
    }
    
    public int getTallGrassColor(BlockPos blockPos) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();

        long shift = x * 0x2fc20f + z * 0x5d8875 + y;
        shift = shift * shift * 0x285b825L + shift * 11L;
        x = (int) ((long) x + (shift >> 14 & 31L));
        y = (int) ((long) y + (shift >> 19 & 31L));
        z = (int) ((long) z + (shift >> 24 & 31L));
        
        Clime clime = this.climateSampler.get().sample(x, z);
        double temp = MathHelper.clamp(clime.temp() - getTempOffset(blockPos.getY(), this.snowLineOffset), 0.0, 1.0);

        return ColorizerGrass.getGrassColor(temp, clime.rain());
    }
    
    public boolean canSampleSkyColor() {
        return this.skyClimateSampler.isPresent() && this.skyClimateSampler.get().sampleSkyColor();
    }
    
    public boolean canSampleBiomeColor() {
        return this.climateSampler.isPresent() && this.climateSampler.get().sampleBiomeColor();
    }
    
    private static double getTempOffset(int y, int seaLevel) {
        boolean useHeightTempGradient = ModernBetaConfig.visualOptions.useHeightTempGradient; 
        return useHeightTempGradient ? MathHelper.clamp(1.0 - (seaLevel + 64) / (double)y, 0.0, 0.5) : 0.0;
    }
}
