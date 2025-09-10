package mod.bespectacled.modernbetaforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorMapGenBase;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaStructures;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStronghold;

@Mixin(MapGenStronghold.class)
public abstract class MixinMapGenStronghold {
    @Shadow private ChunkPos[] structureCoords;
    
    @Inject(method = "generatePositions", at = @At("TAIL"), cancellable = true)
    private void injectGeneratePositions(CallbackInfo info) {
        AccessorMapGenBase accessor = (AccessorMapGenBase)this;
        
        ChunkProviderServer chunkProviderServer = (ChunkProviderServer)accessor.getWorld().getChunkProvider();
        IChunkGenerator chunkGenerator = chunkProviderServer.chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource() instanceof FiniteChunkSource) {
            FiniteChunkSource chunkSource = (FiniteChunkSource)((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            
            this.structureCoords[0] = ModernBetaStructures.getFiniteStrongholdPosition(chunkSource);
        }
    }
    
}
