package mod.bespectacled.modernbetaforge.world.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class BetaStructureOceanMonument extends StructureOceanMonument {
    private static final List<Biome> WATER_BIOMES;
    private static final List<Biome> SPAWN_BIOMES;
    
    private final int spacing;
    private final int separation;
    
    public BetaStructureOceanMonument() {
        super();
        
        this.spacing = 32;
        this.separation = 5;
    }
    
    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalChunkX = chunkX;
        int originalChunkZ = chunkZ;
        
        if (chunkX < 0) {
            chunkX -= this.spacing - 1;
        }
        
        if (chunkZ < 0) {
            chunkZ -= this.spacing - 1;
        }
        
        int structureX = chunkX / this.spacing;
        int structureZ = chunkZ / this.spacing;
        
        Random random = this.world.setRandomSeed(structureX, structureZ, 10387313);
        
        structureX *= this.spacing;
        structureZ *= this.spacing;
        
        structureX += (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
        structureZ += (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
        
        chunkX = originalChunkX;
        chunkZ = originalChunkZ;
        
        ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)this.world.getBiomeProvider();
        
        if (chunkX == structureX && chunkZ == structureZ) {
            if (!modernBetaBiomeProvider.areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 16, SPAWN_BIOMES)) {
                return false;
            }
            
            boolean validBiome = modernBetaBiomeProvider.areBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 29, WATER_BIOMES);
            
            if (validBiome) {
                return true;
            }
        }
        
        return false;
    }
    
    static {
        WATER_BIOMES = new ArrayList<>(Arrays.asList(ModernBetaBiomeHolders.BETA_FROZEN_OCEAN, ModernBetaBiomeHolders.BETA_OCEAN));
        WATER_BIOMES.addAll(StructureOceanMonument.WATER_BIOMES);
        
        SPAWN_BIOMES = new ArrayList<>(Arrays.asList(ModernBetaBiomeHolders.BETA_OCEAN));
        SPAWN_BIOMES.addAll(StructureOceanMonument.SPAWN_BIOMES);
    }
}
