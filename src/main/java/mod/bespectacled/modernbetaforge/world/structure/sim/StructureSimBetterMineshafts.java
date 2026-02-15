package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.function.BiFunction;

import mod.bespectacled.modernbetaforge.compat.bettermineshafts.MapGenBetterMineshaftOptimized;
import net.minecraft.world.biome.Biome;

public class StructureSimBetterMineshafts extends StructureSim {
    public StructureSimBetterMineshafts(long seed) {
        super(seed);
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        return MapGenBetterMineshaftOptimized.canSpawnStructureAtCoords(chunkX, chunkZ, this.random, biomeFunc);
    }

}
