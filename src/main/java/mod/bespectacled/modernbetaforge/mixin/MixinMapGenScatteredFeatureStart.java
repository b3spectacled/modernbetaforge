package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorStructureStart;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.structure.StructureWeightSampler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureStart;

@Mixin(MapGenScatteredFeature.Start.class)
public abstract class MixinMapGenScatteredFeatureStart {
    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;IILnet/minecraft/world/biome/Biome;)V", at = @At("TAIL"))
    private void injectStart(World world, Random random, int chunkX, int chunkZ, Biome biome, CallbackInfo info) {
        AccessorStructureStart start = (AccessorStructureStart)this;
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        if (biome == ModernBetaBiomeHolders.BETA_RAINFOREST) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.JunglePyramid(random, startX, startZ));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_SWAMPLAND) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.SwampHut(random, startX, startZ));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_DESERT) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.DesertPyramid(random, startX, startZ));
            
        } else if (biome == ModernBetaBiomeHolders.BETA_TUNDRA || biome == ModernBetaBiomeHolders.BETA_TAIGA) {
            start.getComponents().add(new ComponentScatteredFeaturePieces.Igloo(random, startX, startZ));
            
        }
        
        start.invokeUpdateBoundingBox();
        
        WorldServer worldServer = (WorldServer)world;
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            int numComponents = StructureWeightSampler.cacheStructures(
                (ModernBetaChunkGenerator)chunkGenerator,
                (StructureStart)(Object)this,
                component -> !(component instanceof JunglePyramid)
            );
            
            ModernBeta.log(Level.DEBUG, String.format("Start at %d/%d. Number of temple components: %d", startX, startZ, numComponents));
        }
    }
}
