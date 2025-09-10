package mod.bespectacled.modernbetaforge.world.structure.sim;

import java.util.Random;
import java.util.function.BiFunction;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaStructures;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStronghold;

public class StructureSimStronghold extends StructureSim {
    private static final MapGenStronghold STRONGHOLD = new MapGenStronghold();
    
    private final ChunkSource chunkSource;
    private final ChunkPos[] structureCoords;
    private final double distance;
    private int spread;
    
    private boolean generatedPositions;

    public StructureSimStronghold(ChunkSource chunkSource) {
        super(chunkSource.getSeed());
        
        this.chunkSource = chunkSource;
        this.structureCoords = new ChunkPos[128];
        this.distance = 32.0;
        this.spread = 3;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ, BiFunction<Integer, Integer, Biome> biomeFunc) {
        if (!this.generatedPositions) {
            this.generatePositions(biomeFunc);
            this.generatedPositions = true;
        }

        for (ChunkPos chunkPos : this.structureCoords) {
            if (chunkPos != null && chunkX == chunkPos.x && chunkZ == chunkPos.z) {
                return true;
            }
        }

        return false;
    }

    private void generatePositions(BiFunction<Integer, Integer, Biome> biomeFunc) {
        Random random = new Random();
        random.setSeed(this.seed);
        
        double theta = random.nextDouble() * Math.PI * 2.0D;
        int count = 0;
        int ring = 0;

        if (this.chunkSource instanceof FiniteChunkSource) {
            this.structureCoords[0] = ModernBetaStructures.getFiniteStrongholdPosition((FiniteChunkSource)this.chunkSource);
            
        } else {
            for (int i = 0; i < this.structureCoords.length; ++i) {
                // No need to sample all Stronghold rings
                if (ring >= 1) {
                    return;
                }
                
                double a = 4.0 * this.distance + this.distance * (double)ring * 6.0 + (random.nextDouble() - 0.5) * this.distance * 2.5;
                
                int chunkX = (int)Math.round(Math.cos(theta) * a);
                int chunkZ = (int)Math.round(Math.sin(theta) * a);
                
                int x = (chunkX << 4) + 8;
                int z = (chunkZ << 4) + 8;
                
                BlockPos blockPos = ModernBetaBiomeProvider.findBiomePosition(x, z, 112, STRONGHOLD.allowedBiomes, random, biomeFunc);

                if (blockPos != null) {
                    chunkX = blockPos.getX() >> 4;
                    chunkZ = blockPos.getZ() >> 4;
                }
                
                this.structureCoords[i] = new ChunkPos(chunkX, chunkZ);

                theta += (Math.PI * 2.0) / (double)this.spread;
                count++;

                if (count == this.spread) {
                    theta += random.nextDouble() * Math.PI * 2.0;
                    count = 0;
                    ring++;
                    
                    this.spread += 2 * this.spread / (ring + 1);
                    this.spread = Math.min(this.spread, this.structureCoords.length - i);
                }
            }
        }
    }
}
