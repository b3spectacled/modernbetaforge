package mod.bespectacled.modernbetaforge.mixin.accessor;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.ChunkGeneratorHell;

@Mixin(ChunkGeneratorHell.class)
public interface AccessorChunkGeneratorHell {
    @Accessor
    Random getRand();
}
