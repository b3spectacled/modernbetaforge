package mod.bespectacled.modernbetaforge.world.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.structure.WoodlandMansion;

public class BetaWoodlandMansion extends WoodlandMansion {
    public static final List<Biome> ALLOWED_BIOMES;

    public BetaWoodlandMansion(ChunkGeneratorOverworld chunkGeneratorOverworld) {
        super(chunkGeneratorOverworld);
    }
    
    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalChunkX = chunkX;
        int originalChunkZ = chunkZ;
        
        if (originalChunkX < 0) {
            originalChunkX -= 79;
        }
        
        if (originalChunkZ < 0) {
            originalChunkZ -= 79;
        }
        
        int structureX = originalChunkX / 80;
        int structureZ = originalChunkZ / 80;
        
        Random random = this.world.setRandomSeed(structureX, structureZ, 10387319);
        
        structureX *= 80;
        structureZ *= 80;
        
        structureX += (random.nextInt(60) + random.nextInt(60)) / 2;
        structureZ += (random.nextInt(60) + random.nextInt(60)) / 2;
        
        if (chunkX == structureX && chunkZ == structureZ) {
            boolean validBiome = this.world.getBiomeProvider().areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 32, ALLOWED_BIOMES);
            
            if (validBiome) {
                return true;
            }
        }
        
        return false;
    }

    static {
        ALLOWED_BIOMES = Arrays.<Biome>asList(
            ModernBetaBiomeHolders.BETA_SEASONAL_FOREST,
            Biomes.ROOFED_FOREST,
            Biomes.MUTATED_ROOFED_FOREST
        );
    }
}
