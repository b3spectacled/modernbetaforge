package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorMapGenBase;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStronghold;

@Mixin(MapGenStronghold.class)
public class MixinMapGenStronghold {
    @Shadow private ChunkPos[] structureCoords;
    
    @Inject(method = "generatePositions", at = @At("TAIL"), cancellable = true)
    private void injectGeneratePositions(CallbackInfo info) {
        AccessorMapGenBase accessor = (AccessorMapGenBase)this;
        
        ChunkProviderServer chunkProviderServer = (ChunkProviderServer)accessor.getWorld().getChunkProvider();
        IChunkGenerator chunkGenerator = chunkProviderServer.chunkGenerator;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource() instanceof FiniteChunkSource) {
            FiniteChunkSource chunkSource = (FiniteChunkSource)((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            
            Random random = new Random();
            random.setSeed(accessor.getWorld().getSeed());
            
            int chunkWidth = chunkSource.getLevelWidth() >> 4;
            int chunkLength = chunkSource.getLevelLength() >> 4;
            
            int buffer = Math.min(Math.min(chunkWidth, chunkLength) / 2, 4);
            
            int chunkX = MathHelper.clamp(random.nextInt(chunkWidth), buffer, chunkWidth - buffer) - chunkWidth / 2;
            int chunkZ = MathHelper.clamp(random.nextInt(chunkLength), buffer, chunkWidth - buffer) - chunkLength / 2;
            
            this.structureCoords[0] = new ChunkPos(chunkX, chunkZ);
        }
    }
}
