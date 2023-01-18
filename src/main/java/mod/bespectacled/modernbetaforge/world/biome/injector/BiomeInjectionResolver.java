package mod.bespectacled.modernbetaforge.world.biome.injector;

import net.minecraft.world.biome.Biome;

@FunctionalInterface
public interface BiomeInjectionResolver {
    public static final BiomeInjectionResolver DEFAULT = (x, y, z) -> null;
    
    public Biome apply(int x, int y, int z);
}