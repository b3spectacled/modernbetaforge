package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.Random;
import java.util.function.BiFunction;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.WoodlandMansion;

public class StructureSimMansion extends StructureSim {
    public StructureSimMansion(long seed) {
        super(seed);
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        int startChunkX = chunkX;
        int startChunkZ = chunkZ;

        if (chunkX < 0) {
            chunkX -= 79;
        }

        if (chunkZ < 0) {
            chunkZ -= 79;
        }

        int mansionChunkX = chunkX / 80;
        int mansionChunkZ = chunkZ / 80;
        Random random = this.setWorldSeed(mansionChunkX, mansionChunkZ, 10387319);
        mansionChunkX = mansionChunkX * 80;
        mansionChunkZ = mansionChunkZ * 80;
        mansionChunkX = mansionChunkX + (random.nextInt(60) + random.nextInt(60)) / 2;
        mansionChunkZ = mansionChunkZ + (random.nextInt(60) + random.nextInt(60)) / 2;

        if (startChunkX == mansionChunkX && startChunkZ == mansionChunkZ) {
            int x = (startChunkX << 4) + 8;
            int z = (startChunkZ << 4) + 8;
            
            return ModernBetaBiomeProvider.areBiomesViable(x, z, 32, WoodlandMansion.ALLOWED_BIOMES, biomeFunc);
        }

        return false;
    }

}
