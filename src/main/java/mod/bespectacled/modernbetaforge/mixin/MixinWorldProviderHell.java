package mod.bespectacled.modernbetaforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorWorldProvider;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorHell;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.gen.IChunkGenerator;

@Mixin(WorldProviderHell.class)
public abstract class MixinWorldProviderHell {
    @Inject(method = "createChunkGenerator", at = @At("HEAD"), cancellable = true)
    private void injectCreateChunkGenerator(CallbackInfoReturnable<IChunkGenerator> info) {
        World world = ((AccessorWorldProvider)(Object)this).getWorld();

        if (world.getWorldType() instanceof ModernBetaWorldType) {
            // Need to grab generator settings from Overworld dimension since each dimension has its own generator settings
            String generatorOptions = world.getMinecraftServer().getWorld(DimensionType.OVERWORLD.getId()).getWorldInfo().getGeneratorOptions();
            ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.buildSettings(generatorOptions);
            
            if (settings.useOldNether) {
                info.setReturnValue(new ModernBetaChunkGeneratorHell(world, world.getWorldInfo().isMapFeaturesEnabled(), world.getSeed(), generatorOptions));
            }
        }
    }
}
