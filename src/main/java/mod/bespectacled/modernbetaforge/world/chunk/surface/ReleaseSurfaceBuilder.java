package mod.bespectacled.modernbetaforge.world.chunk.surface;

import java.util.Random;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class ReleaseSurfaceBuilder extends SurfaceBuilder {
    public ReleaseSurfaceBuilder(ChunkSource chunkSource, ModernBetaGeneratorSettings settings) {
        super(chunkSource, settings);
    }

    @Override
    public void provideSurface(World world, Biome[] biomes, ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        
        Random random = this.createSurfaceRandom(chunkX, chunkZ);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                Biome biome = biomes[localX + localZ * 16];
                this.useCustomSurfaceBuilder(world, biome, chunkPrimer, random, x, z, true);
            }
        }
    }
}
