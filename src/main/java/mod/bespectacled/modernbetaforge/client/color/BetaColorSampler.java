package mod.bespectacled.modernbetaforge.client.color;

import java.util.Optional;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.SkyClimateSampler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;

public class BetaColorSampler {
    public static final BetaColorSampler INSTANCE = new BetaColorSampler();
    
    private Optional<ClimateSampler> climateSampler;
    private Optional<SkyClimateSampler> skyClimateSampler;
    
    private BetaColorSampler() {
        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
    }
    
    public void resetClimateSamplers() {
        this.climateSampler = Optional.empty();
        this.skyClimateSampler = Optional.empty();
    }
    
    public void setClimateSamplers(ClimateSampler climateSampler, SkyClimateSampler skyClimateSampler) {
        this.climateSampler = Optional.ofNullable(climateSampler);
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
        
        return ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
    }
    
    public int getFoliageColor(BlockPos blockPos) {
        Clime clime = this.climateSampler.get().sample(blockPos.getX(), blockPos.getZ());
        
        return ColorizerFoliage.getFoliageColor(clime.temp(), clime.rain());
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

        return ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
    }
    
    public boolean canSampleSkyColor() {
        return this.skyClimateSampler.isPresent() && this.skyClimateSampler.get().sampleSkyColor();
    }
    
    public boolean canSampleBiomeColor() {
        return this.climateSampler.isPresent() && this.climateSampler.get().sampleBiomeColor();
    }
}
