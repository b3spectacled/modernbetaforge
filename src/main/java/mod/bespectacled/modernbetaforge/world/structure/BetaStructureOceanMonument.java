package mod.bespectacled.modernbetaforge.world.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class BetaStructureOceanMonument extends StructureOceanMonument {
    public static final List<Biome> WATER_BIOMES;
    public static final List<Biome> SPAWN_BIOMES;
    
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
        WATER_BIOMES = Arrays.<Biome>asList(
            ModernBetaBiomeHolders.BETA_FROZEN_OCEAN,
            ModernBetaBiomeHolders.BETA_OCEAN,
            Biomes.OCEAN,
            Biomes.DEEP_OCEAN,
            Biomes.RIVER,
            Biomes.FROZEN_OCEAN,
            Biomes.FROZEN_RIVER
        );
        
        SPAWN_BIOMES = Arrays.<Biome>asList(
            ModernBetaBiomeHolders.BETA_OCEAN,
            Biomes.OCEAN,
            Biomes.DEEP_OCEAN
        );
    }
}
