package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;

public abstract class StructureSim {
    protected final Random random;
    protected final long seed;
    private final Set<Long> generated;
    
    private int range;
    
    public StructureSim(long seed) {
        this.random = new Random();
        this.seed = seed;
        this.generated = new HashSet<>();
        
        this.range = 8;
    }
    
    public void generatePositions(int startChunkX, int startChunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        this.random.setSeed(this.seed);
        long randomLong0 = this.random.nextLong();
        long randomLong1 = this.random.nextLong();
        
        for (int chunkX = startChunkX - this.range; chunkX <= startChunkX + this.range; ++chunkX) {
            for (int chunkZ = startChunkZ - this.range; chunkZ <= startChunkZ + this.range; ++chunkZ) {
                long randomLong2 = (long)chunkX * randomLong0;
                long randomLong3 = (long)chunkZ * randomLong1;
                
                this.random.setSeed(randomLong2 ^ randomLong3 ^ this.seed);
                long chunkPos = ChunkPos.asLong(chunkX, chunkZ);

                if (!this.generated.contains(chunkPos)) {
                    this.random.nextInt();
                    
                    if (this.canSpawnStructureAtCoords(chunkX, chunkZ, biomeFunc)) {
                        this.generated.add(chunkPos);
                    }
                }
            }
        }
    }
    
    public boolean canGenerate(int chunkX, int chunkZ) {
        return this.generated.contains(ChunkPos.asLong(chunkX, chunkZ));
    }
    
    public int getRange() {
        return this.range;
    }
    
    protected abstract boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc);
    
    protected Random setWorldSeed(int seedX, int seedY, int seedZ) {
        long seed = (long)seedX * 341873128712L + (long)seedY * 132897987541L + this.seed + (long)seedZ;

        return new Random(seed);
    }
}
