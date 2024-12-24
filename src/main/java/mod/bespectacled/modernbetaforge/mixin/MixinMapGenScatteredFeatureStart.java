package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorStructureStart;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ComponentChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces.JunglePyramid;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

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
            ModernBetaChunkGenerator modernBetaChunkGenerator = ((ModernBetaChunkGenerator)chunkGenerator);
            ChunkCache<ComponentChunk> componentCache = modernBetaChunkGenerator.getComponentCache();
            
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();
            if (chunkSource instanceof NoiseChunkSource) {
                AccessorStructureStart accessor = (AccessorStructureStart)this;
                
                int numComponents = 0;
                for (StructureComponent component : accessor.getComponents()) {
                    if (!(component instanceof JunglePyramid)) {
                        continue;
                    }
    
                    StructureBoundingBox box = component.getBoundingBox();
                    
                    int minChunkX = box.minX >> 4;
                    int minChunkZ = box.minZ >> 4;
                    int maxChunkX = box.maxX >> 4;
                    int maxChunkZ = box.maxZ >> 4;
                    
                    for (int cZ = minChunkZ - 1; cZ <= maxChunkZ + 1; ++cZ) {
                        for (int cX = minChunkX - 1; cX <= maxChunkX + 1; ++cX) {
                            componentCache.get(cX, cZ).addComponent(component);
                        }
                    }
                    numComponents++;
                }
                
                ModernBeta.log(Level.DEBUG, String.format("Start at %d/%d. Number of temple components: %d", startX, startZ, numComponents));
            }
        }
    }
}
