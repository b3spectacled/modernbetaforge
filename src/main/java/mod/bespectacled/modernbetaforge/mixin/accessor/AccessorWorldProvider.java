package mod.bespectacled.modernbetaforge.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;

@Mixin(WorldProvider.class)
public interface AccessorWorldProvider {
    @Accessor
    World getWorld();
    
    @Accessor("biomeProvider")
    void setBiomeProvider(BiomeProvider biomeProvider);
}
