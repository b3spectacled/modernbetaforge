package mod.bespectacled.modernbetaforge.world.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureStart;

public class ModernBetaMapGenScatteredFeature extends MapGenScatteredFeature {
    private static final List<Biome> ALLOWED_BIOMES;
    
    private final int maxDistanceBetweenScatteredFeatures;

    public ModernBetaMapGenScatteredFeature() {
        this.maxDistanceBetweenScatteredFeatures = 32;
    }
    
    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalChunkX = chunkX;
        int originalChunkZ = chunkZ;

        if (chunkX < 0) {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        if (chunkZ < 0) {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int structureX = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int structureZ = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        
        Random random = this.world.setRandomSeed(structureX, structureZ, 14357617);
        
        structureX = structureX * this.maxDistanceBetweenScatteredFeatures;
        structureZ = structureZ * this.maxDistanceBetweenScatteredFeatures;
        structureX = structureX + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);
        structureZ = structureZ + random.nextInt(this.maxDistanceBetweenScatteredFeatures - 8);

        if (originalChunkX == structureX && originalChunkZ == structureZ) {
            int x = originalChunkX * 16 + 8;
            int z = originalChunkZ * 16 + 8;

            // Biome biome = this.chunkSource.getInjectedBiomeAtBlock(x, z); Really laggy for some reason, let's just not do this.
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(x, 0, z));

            if (biome == null) {
                return false;
            }

            for (Biome allowedBiome : ALLOWED_BIOMES) {
                if (biome == allowedBiome) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.world, this.rand, chunkX, chunkZ);
    }
    
    public static class Start extends MapGenScatteredFeature.Start {
        public Start() { }

        public Start(World world, Random random, int chunkX, int chunkZ) {
            this(world, random, chunkX, chunkZ, world.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        public Start(World world, Random random, int chunkX, int chunkZ, Biome biome) {
            super(world, random, chunkX, chunkZ, biome); // Use super constructor to handle placement for vanilla biomes.
            
            if (biome == ModernBetaBiomeHolders.BETA_RAINFOREST) {
                this.components.add(new ComponentScatteredFeaturePieces.JunglePyramid(random, chunkX * 16, chunkZ * 16));
                
            } else if (biome == ModernBetaBiomeHolders.BETA_SWAMPLAND) {
                this.components.add(new ComponentScatteredFeaturePieces.SwampHut(random, chunkX * 16, chunkZ * 16));
                
            } else if (biome == ModernBetaBiomeHolders.BETA_DESERT) {
                this.components.add(new ComponentScatteredFeaturePieces.DesertPyramid(random, chunkX * 16, chunkZ * 16));
                
            } else if (biome == ModernBetaBiomeHolders.BETA_TUNDRA || biome == ModernBetaBiomeHolders.BETA_TAIGA) {
                this.components.add(new ComponentScatteredFeaturePieces.Igloo(random, chunkX * 16, chunkZ * 16));
                
            }

            this.updateBoundingBox();
        }
    }
    
    static {
        ALLOWED_BIOMES = Arrays.<Biome>asList(
            ModernBetaBiomeHolders.BETA_DESERT,
            ModernBetaBiomeHolders.BETA_RAINFOREST,
            ModernBetaBiomeHolders.BETA_SWAMPLAND,
            ModernBetaBiomeHolders.BETA_TUNDRA,
            ModernBetaBiomeHolders.BETA_TAIGA,
            Biomes.DESERT,
            Biomes.DESERT_HILLS,
            Biomes.JUNGLE,
            Biomes.JUNGLE_HILLS,
            Biomes.SWAMPLAND,
            Biomes.ICE_PLAINS,
            Biomes.COLD_TAIGA
        );
    }
}
