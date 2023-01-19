package mod.bespectacled.modernbetaforge.util.chunk;

import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.util.function.TriFunction;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public class BiomeChunk {
    private Biome[] biomes = new Biome[256];
    
    public BiomeChunk(int chunkX, int chunkZ, ChunkSource chunkSource, TriFunction<BiomeInjectionContext, Integer, Integer, Biome> chunkFunc) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        int ndx = 0;
        for (int x = startX; x < startX + 16; ++x) {
            for (int z = startZ; z < startZ + 16; ++z) {
                int topHeight = chunkSource.getHeight(x, z, Type.SURFACE);
                IBlockState topState = topHeight < chunkSource.getSeaLevel() ? BlockStates.WATER : BlockStates.AIR;
                BiomeInjectionContext context = new BiomeInjectionContext(topHeight, topState);
                
                this.biomes[ndx++] = chunkFunc.apply(context, x, z);
            }
        }
    }
    
    public Biome sampleBiome(int x, int z) {
        return this.biomes[(z & 0xF) + (x & 0xF) * 16];
    }
}
