package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;

public class StructureSimVillage extends StructureSim {
    private final int distance;

    public StructureSimVillage(long seed) {
        super(seed);
        
        this.distance = 32;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        int startChunkX = chunkX;
        int startChunkZ = chunkZ;
        
        if (chunkX < 0) {
            chunkX -= this.distance - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= this.distance - 1;
        }

        int villageChunkX = chunkX / this.distance;
        int villageChunkZ = chunkZ / this.distance;
        Random random = this.setWorldSeed(villageChunkX, villageChunkZ, 10387312);
        villageChunkX = villageChunkX * this.distance;
        villageChunkZ = villageChunkZ * this.distance;
        villageChunkX = villageChunkX + random.nextInt(this.distance - 8);
        villageChunkZ = villageChunkZ + random.nextInt(this.distance - 8);

        if (startChunkX == villageChunkX && startChunkZ == villageChunkZ) {
            int x = (startChunkX << 4) + 8;
            int z = (startChunkZ << 4) + 8;
            
            return MapGenVillage.VILLAGE_SPAWN_BIOMES.contains(biomeFunc.apply(x, z));
        }

        return false;
    }

}
