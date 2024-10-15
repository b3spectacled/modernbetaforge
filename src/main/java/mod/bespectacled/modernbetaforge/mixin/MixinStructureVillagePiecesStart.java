package mod.bespectacled.modernbetaforge.mixin;

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mod.bespectacled.modernbetaforge.mixin.accessor.AccessorVillage;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaDesert;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaIceDesert;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSavanna;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaTaiga;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.StructureVillagePieces;

@Mixin(StructureVillagePieces.Start.class)
public abstract class MixinStructureVillagePiecesStart {
    @Inject(
        method = "<init>(Lnet/minecraft/world/biome/BiomeProvider;ILjava/util/Random;IILjava/util/List;I)V",
        at = @At("TAIL")
    )
    private void injectStart(
        BiomeProvider biomeProvider,
        int someValue,
        Random rand,
        int x,
        int z,
        List<StructureVillagePieces.PieceWeight> pieces,
        int terrainType,
        CallbackInfo info
    ) {
    	Biome biome = biomeProvider.getBiome(new BlockPos(x, 0, z), Biomes.DEFAULT);
    	AccessorVillage village = (AccessorVillage)this;
    	
    	if (biome instanceof BiomeBetaDesert || biome instanceof BiomeBetaIceDesert) {
    	    village.setStructureType(1);
    	    
    	} else if (biome instanceof BiomeBetaSavanna) {
            village.setStructureType(2);
            
    	} else if (biome instanceof BiomeBetaTaiga) {
            village.setStructureType(3);
            
    	}
    }
}
