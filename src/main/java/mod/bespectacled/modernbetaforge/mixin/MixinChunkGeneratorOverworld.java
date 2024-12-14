package mod.bespectacled.modernbetaforge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import mod.bespectacled.modernbetaforge.util.DebugUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

@Mixin(ChunkGeneratorOverworld.class)
public class MixinChunkGeneratorOverworld {
    @Unique private static final boolean MODERN_BETA_DEBUG = false;
    
    @Inject(method = "generateChunk", at = @At("HEAD"))
    private void injectGenerateChunkHead(int x, int z, CallbackInfoReturnable<Chunk> info) {
        if (MODERN_BETA_DEBUG)
            DebugUtil.startDebug(DebugUtil.SECTION_GEN_CHUNK_VANILLA);
    }
    
    @Inject(method = "generateChunk", at = @At("RETURN"))
    private void injectGenerateChunkReturn(int x, int z, CallbackInfoReturnable<Chunk> info) {
        if (MODERN_BETA_DEBUG)
            DebugUtil.endDebug(DebugUtil.SECTION_GEN_CHUNK_VANILLA);
    }
}
