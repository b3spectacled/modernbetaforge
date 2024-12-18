package mod.bespectacled.modernbetaforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Mixin(World.class)
public abstract class MixinWorld {
    @Inject(method = "getCloudColorBody", at = @At("HEAD"), cancellable = true, remap = false)
    private void injectGetCloudColorBody(float partialTicks, CallbackInfoReturnable<Vec3d> info) {
        WorldClient world = (WorldClient)(Object)this;
        Biome biome = world.getBiome(Minecraft.getMinecraft().getRenderViewEntity().getPosition());

        if (biome != null && biome instanceof ModernBetaBiome) {
            ModernBetaBiome modernBetaBiome = (ModernBetaBiome)biome;
            int cloudColor = modernBetaBiome.getCloudColor();
            
            if (cloudColor != -1) {
                Vec3d cloudColorVec = MathUtil.convertColorIntToVec3d(cloudColor);
                float r = (float)cloudColorVec.x;
                float g = (float)cloudColorVec.y;
                float b = (float)cloudColorVec.z;
                
                float celestialAngle = world.getCelestialAngle(partialTicks);
                celestialAngle = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
                celestialAngle = MathHelper.clamp(celestialAngle, 0.0F, 1.0F);
                
                float rainStrength = world.getRainStrength(partialTicks);
                if (rainStrength > 0.0F) {
                    float colorAddition = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
                    float delta = 1.0F - rainStrength * 0.95F;
                    
                    r = r * delta + colorAddition * (1.0F - delta);
                    g = g * delta + colorAddition * (1.0F - delta);
                    b = b * delta + colorAddition * (1.0F - delta);
                    
                }

                r *= (celestialAngle * 0.9F + 0.1F);
                g *= (celestialAngle * 0.9F + 0.1F);
                b *= (celestialAngle * 0.85F + 0.15F);
                
                float thunderStrength = world.getThunderStrength(partialTicks);
                if (thunderStrength > 0.0F) {
                    float colorAddition = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;
                    float delta = 1.0F - thunderStrength * 0.95F;
                    
                    r = r * delta + colorAddition * (1.0F - delta);
                    g = g * delta + colorAddition * (1.0F - delta);
                    b = b * delta + colorAddition * (1.0F - delta);
                    
                }

                info.setReturnValue(new Vec3d(r, g, b));
            }
        }
    }
}
