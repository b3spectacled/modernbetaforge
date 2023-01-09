package mod.bespectacled.modernbetaforge.world.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSource;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ModernBetaStructureOceanMonument extends StructureOceanMonument {
    public static final List<Biome> WATER_BIOMES;
    public static final List<Biome> SPAWN_BIOMES;
    
    private final ChunkSource chunkSource;
    private final int spacing;
    private final int separation;
    
    public ModernBetaStructureOceanMonument(ChunkSource chunkSource) {
        super();
        
        this.chunkSource = chunkSource;
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
        
        BiomeProvider biomeProvider = this.world.getBiomeProvider();
        
        if (biomeProvider instanceof ModernBetaBiomeProvider) {
            ModernBetaBiomeProvider modernBetaBiomeProvider = (ModernBetaBiomeProvider)this.world.getBiomeProvider();
            
            if (chunkX == structureX && chunkZ == structureZ) {
                if (!modernBetaBiomeProvider.areInjectedBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 16, SPAWN_BIOMES, this.chunkSource)) {
                    return false;
                }
                
                boolean validBiome = modernBetaBiomeProvider.areInjectedBiomesViable(chunkX * 16 + 8, chunkZ * 16 + 8, 29, WATER_BIOMES, this.chunkSource);
                
                if (validBiome) {
                    return true;
                }
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
