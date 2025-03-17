package mod.bespectacled.modernbetaforge.mixin;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.structure.StructureWeightSampler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStart;
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
            int numComponents = StructureWeightSampler.cacheStructures(
                (ModernBetaChunkGenerator)chunkGenerator,
                (StructureStart)(Object)this,
                component -> component instanceof StructureVillagePieces.Road
            );
            
            ModernBeta.log(Level.DEBUG, String.format("Start at %d/%d. Number of village components: %d", x, z, numComponents));
        }
    }           
}
