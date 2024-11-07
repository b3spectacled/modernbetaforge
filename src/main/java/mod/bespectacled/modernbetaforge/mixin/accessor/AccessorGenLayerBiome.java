package mod.bespectacled.modernbetaforge.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.layer.GenLayerBiome;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

@Mixin(GenLayerBiome.class)
public interface AccessorGenLayerBiome {
    @Accessor
    List<BiomeEntry>[] getBiomes();
    
    @Accessor("biomes")
    void setBiomes(List<BiomeEntry>[] biomes);
}
