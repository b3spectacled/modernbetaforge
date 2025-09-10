package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.Random;
import java.util.function.BiFunction;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class StructureSimMonument extends StructureSim {
    private final int spacing;
    private final int separation;

    public StructureSimMonument(long seed) {
        super(seed);
        
        this.spacing = 32;
        this.separation = 5;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        int startChunkX = chunkX;
        int startChunkZ = chunkZ;

        if (chunkX < 0) {
            chunkX -= this.spacing - 1;
        }

        if (chunkZ < 0){
            chunkZ -= this.spacing - 1;
        }

        int monumentChunkX = chunkX / this.spacing;
        int monumentChunkZ = chunkZ / this.spacing;
        Random random = this.setWorldSeed(monumentChunkX, monumentChunkZ, 10387313);
        monumentChunkX = monumentChunkX * this.spacing;
        monumentChunkZ = monumentChunkZ * this.spacing;
        monumentChunkX = monumentChunkX + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
        monumentChunkZ = monumentChunkZ + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;

        if (startChunkX == monumentChunkX && startChunkZ == monumentChunkZ) {
            int x = (startChunkX << 4) + 8;
            int z = (startChunkZ << 4) + 8;
            
            if (!ModernBetaBiomeProvider.areBiomesViable(x, z, 16, StructureOceanMonument.SPAWN_BIOMES, biomeFunc)) {
                return false;
            }

            return ModernBetaBiomeProvider.areBiomesViable(x, z, 29, StructureOceanMonument.WATER_BIOMES, biomeFunc);
        }

        return false;
    }
}
