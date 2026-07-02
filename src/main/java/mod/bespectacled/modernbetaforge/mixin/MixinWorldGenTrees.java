package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import mod.bespectacled.modernbetaforge.compat.ModCompat;
import mod.bespectacled.modernbetaforge.compat.futuremc.CompatFutureMC;
import mod.bespectacled.modernbetaforge.compat.futuremc.WorldGenBeeNest;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTrees;

@Mixin(WorldGenTrees.class)
public class MixinWorldGenTrees {
    @Inject(method = "generate", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void injectGenerate(World world, Random random, BlockPos pos, CallbackInfoReturnable<Boolean> info, int height, boolean canGenerate) {
        if (ModCompat.isModLoaded(CompatFutureMC.MOD_ID) && world.getBiome(pos) instanceof ModernBetaBiome) {
            WorldGenBeeNest.generateForOak(world, random, pos, height);
        }
    }
}
