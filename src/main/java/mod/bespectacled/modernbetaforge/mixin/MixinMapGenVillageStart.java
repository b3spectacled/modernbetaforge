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
            ModernBetaChunkGenerator modernBetaChunkGenerator = ((ModernBetaChunkGenerator)chunkGenerator);
            
            ChunkSource chunkSource = modernBetaChunkGenerator.getChunkSource();
            if (chunkSource instanceof NoiseChunkSource) {
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
                    
                    for (int componentChunkZ = minChunkZ - 1; componentChunkZ <= maxChunkZ + 1; ++componentChunkZ) {
                        for (int componentChunkX = minChunkX - 1; componentChunkX <= maxChunkX + 1; ++componentChunkX) {
                            modernBetaChunkGenerator.cacheStructureComponent(componentChunkX, componentChunkZ, component);
                        }
                    }
                    numComponents++;
                }
                
                ModernBeta.log(Level.DEBUG, String.format("Start at %d/%d. Number of village components: %d", x, z, numComponents));
            }
        }
    }           
}
