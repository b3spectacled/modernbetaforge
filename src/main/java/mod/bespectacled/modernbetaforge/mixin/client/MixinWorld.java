package mod.bespectacled.modernbetaforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Mixin(World.class)
public abstract class MixinWorld {
    @ModifyVariable(method = "getCloudColorBody", at = @At("STORE"), ordinal = 2, remap = false)
    private float modifyCloudColorR(float colorR) {
        WorldClient world = (WorldClient)(Object)this;
        Biome biome = world.getBiome(Minecraft.getMinecraft().getRenderViewEntity().getPosition());

        if (biome != null && biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            int cloudColor = modernBetaBiome.getCloudColor();
            
            if (cloudColor != -1) {
                return (float)MathUtil.convertColorIntToVec3d(cloudColor).x;
            }
        }
        
        return colorR;
    }
    
    @ModifyVariable(method = "getCloudColorBody", at = @At("STORE"), ordinal = 3, remap = false)
    private float modifyCloudColorG(float colorG) {
        WorldClient world = (WorldClient)(Object)this;
        Biome biome = world.getBiome(Minecraft.getMinecraft().getRenderViewEntity().getPosition());

        if (biome != null && biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            int cloudColor = modernBetaBiome.getCloudColor();
            
            if (cloudColor != -1) {
                return (float)MathUtil.convertColorIntToVec3d(cloudColor).y;
            }
        }
        
        return colorG;
    }
    
    @ModifyVariable(method = "getCloudColorBody", at = @At("STORE"), ordinal = 4, remap = false)
    private float modifyCloudColorB(float colorB) {
        WorldClient world = (WorldClient)(Object)this;
        Biome biome = world.getBiome(Minecraft.getMinecraft().getRenderViewEntity().getPosition());

        if (biome != null && biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            int cloudColor = modernBetaBiome.getCloudColor();
            
            if (cloudColor != -1) {
                return (float)MathUtil.convertColorIntToVec3d(cloudColor).z;
            }
        }
        
        return colorB;
    }
}
