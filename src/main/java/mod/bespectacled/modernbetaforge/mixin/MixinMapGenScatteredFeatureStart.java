package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorStructureStart;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;

@Mixin(MapGenScatteredFeature.Start.class)
public abstract class MixinMapGenScatteredFeatureStart {
    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;IILnet/minecraft/world/biome/Biome;)V", at = @At("TAIL"))
    private void injectStart(World world, Random random, int chunkX, int chunkZ, Biome biome, CallbackInfo info) {
        AccessorStructureStart start = (AccessorStructureStart)this;
        
        if (biome == ModernBetaBiomeHolders.BETA_RAINFOREST) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.JunglePyramid(random, chunkX * 16, chunkZ * 16));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_SWAMPLAND) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.SwampHut(random, chunkX * 16, chunkZ * 16));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_DESERT) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.DesertPyramid(random, chunkX * 16, chunkZ * 16));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_TUNDRA || biome == ModernBetaBiomeHolders.BETA_TAIGA) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.Igloo(random, chunkX * 16, chunkZ * 16));
            
        }
        
        start.invokeUpdateBoundingBox();
    }
}
