package mod.bespectacled.modernbetaforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorEntityRenderer;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Unique private static int modernBeta_renderDistance = 16;
    @Unique private static float modernBeta_fogWeight = calculateFogWeight(16);
    
    @Inject(method = "updateFogColor(F)V", at = @At("HEAD"))
    private void recalculateFogWeight(float partialTicks, CallbackInfo info) {
        Minecraft mc = ((AccessorEntityRenderer)this).getMC();

        if (mc.gameSettings.renderDistanceChunks != modernBeta_renderDistance) {
            modernBeta_renderDistance = mc.gameSettings.renderDistanceChunks;
            modernBeta_fogWeight = calculateFogWeight(modernBeta_renderDistance);
        }
    }
    
    @ModifyVariable(
        method = "updateFogColor(F)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getSkyColor(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/math/Vec3d;"),
        ordinal = 1
    )
    private float modifyFogWeighting(float weight) {
        Minecraft mc = ((AccessorEntityRenderer)this).getMC();
        
        if (mc.world.getWorldType() instanceof ModernBetaWorldType && ModernBetaConfig.visualOptions.useOldFogColorBlending) { 
            return modernBeta_fogWeight;
        }
        
        return weight;
    }
    
    @ModifyVariable(
        method = "updateFogColor(F)V",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getFogColor(F)Lnet/minecraft/util/math/Vec3d;"),
        ordinal = 1
    )
    private Vec3d modifyFogColor(Vec3d fogColor) {
        Minecraft mc = ((AccessorEntityRenderer)this).getMC();
        
        Entity playerEntity = mc.getRenderViewEntity();
        Biome biome = mc.world.getBiome(playerEntity.getPosition());
        
        if (biome instanceof ModernBetaBiome) {
            int biomeFogColor = ((ModernBetaBiome)biome).getFogColor();

            if (biomeFogColor != -1)
                return MathUtil.convertColorIntToVec3d(biomeFogColor);
        }
        
        return fogColor;
    }
    
    @Unique
    private static float calculateFogWeight(int renderDistance) {
        // Old fog formula with old render distance: weight = 1.0f / (float)(4 - renderDistance) 
        // where renderDistance is 0-3, 0 being 'Far' and 3 being 'Very Short'.
        
        // Change formula to: weight = 1.0f / (float)(8 - renderDistance)
        // to accommodate increase of max render distance from 16 to 32 in current versions.
        
        int clampedDistance = MathHelper.clamp(renderDistance, 4, 32);
        clampedDistance -= 4;
        clampedDistance /= 4;

        int oldRenderDistance = Math.abs(clampedDistance - 7); 
        
        float weight = 1.0f / (float)(8 - oldRenderDistance);
        weight = 1.0f - (float)Math.pow(weight, 0.25);
        
        return weight;
    }
}
