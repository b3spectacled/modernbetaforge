package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorStructureStart;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.ComponentChunk;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

@Mixin(MapGenVillage.Start.class)
public class MixinMapGenVillageStart {
    @Inject(
        method = "<init>(Lnet/minecraft/world/World;Ljava/util/Random;III)V",
        at = @At("TAIL")
    )
    private void injectStart(World world, Random random, int x, int z, int size, CallbackInfo info) {
        WorldServer worldServer = (WorldServer)world;
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();

            if (!(chunkSource instanceof FiniteChunkSource)) {
                ChunkCache<ComponentChunk> componentCache = chunkSource.getComponentCache();
                AccessorStructureStart accessor = (AccessorStructureStart)this;
                
                int numComponents = 0;
                for (StructureComponent component : accessor.getComponents()) {
                    if (component instanceof StructureVillagePieces.Road) {
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
                
                ModernBeta.log(Level.DEBUG, String.format("Start at %d/%d. Number of village components: %d", x, z, numComponents));
            }
        }
    }           
}
