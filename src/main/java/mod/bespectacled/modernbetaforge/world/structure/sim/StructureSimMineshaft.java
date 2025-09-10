package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.function.BiFunction;

import net.minecraft.world.biome.Biome;

public class StructureSimMineshaft extends StructureSim {
    private final double chance;
    
    public StructureSimMineshaft(long seed) {
        super(seed);
        
        this.chance = 0.004;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        return this.random.nextDouble() < this.chance && this.random.nextInt(80) < Math.max(Math.abs(chunkX), Math.abs(chunkZ));
    }

}
