package mod.bespectacled.modernbetaforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.structure.StructureWeightSampler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

@Mixin(StructureVillagePieces.Village.class)
public class MixinStructureVillagePiecesVillage {
    @Inject(method = "getAverageGroundLevel", at = @At("TAIL"), cancellable = true)
    private void injectGetAverageGroundLevel(World world, StructureBoundingBox structureBoundingBox, CallbackInfoReturnable<Integer> info) {
        WorldServer worldServer = (WorldServer)world;
        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            
            if (chunkSource instanceof NoiseChunkSource) {
                StructureComponent component = (StructureComponent)(Object)this;
                StructureBoundingBox box = component.getBoundingBox();
                
                int height = StructureWeightSampler.getStructureHeight(worldServer, chunkSource, box);
                info.setReturnValue(height + 1);
            }
        }
    }
}
