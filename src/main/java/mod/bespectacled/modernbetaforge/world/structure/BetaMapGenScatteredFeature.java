package mod.bespectacled.modernbetaforge.world.structure;

import java.util.Random;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureStart;

public class BetaMapGenScatteredFeature extends MapGenScatteredFeature {
    public BetaMapGenScatteredFeature() {
        super();
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new Start(this.world, this.rand, chunkX, chunkZ);
    }
    
    public static class Start extends MapGenScatteredFeature.Start {
        public Start() {}

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
}
