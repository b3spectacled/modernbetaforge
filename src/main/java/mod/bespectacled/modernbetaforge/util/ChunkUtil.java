package mod.bespectacled.modernbetaforge.util;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkUtil {
    private enum TerrainType {
        LAND(COLOR_LAND),
        SAND(COLOR_SAND),
        WATER(COLOR_WATER),
        VOID(COLOR_VOID),
        CENTER(COLOR_CENTER);
        
        private int color;
        
        private TerrainType(int color) {
            this.color = color;
        }
    }
    
    private static final int COLOR_LAND = MathUtil.convertColorComponentsToInt(127, 178, 56);
    private static final int COLOR_SAND = MathUtil.convertColorComponentsToInt(247, 233, 163);
    private static final int COLOR_WATER = MathUtil.convertColorComponentsToInt(64, 64, 255);
    private static final int COLOR_VOID = MathUtil.convertColorComponentsToInt(0, 0, 0);
    private static final int COLOR_CENTER = MathUtil.convertColorComponentsToInt(255, 0, 0);
    
    public static BufferedImage createTerrainMap(World world, ChunkSource chunkSource, int width, int length, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_RGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        int offsetX = width / 2;
        int offsetZ = length / 2;
        
        for (int localX = 0; localX < width; ++localX) {
            float progress = localX / (float)width;
            progressTracker.accept(progress);
            
            for (int localZ = 0; localZ < length; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;

                TerrainType terrainType = TerrainType.LAND;
                
                if (chunkSource instanceof ReleaseChunkSource) {
                    Biome biome = ((ReleaseChunkSource)chunkSource).getNoiseBiome(x, z);
                    
                    if (BiomeDictionary.hasType(biome, Type.OCEAN) || BiomeDictionary.hasType(biome, Type.RIVER)) {
                        terrainType = TerrainType.WATER;
                    }
                    
                } else {
                    int height = chunkSource.getHeight(world, x, z, HeightmapChunk.Type.SURFACE);
                    
                    if (height < chunkSource.getSeaLevel() - 1) {
                        terrainType = TerrainType.WATER;
                    }
                    
                    if (height == 0) {
                        terrainType = TerrainType.VOID;
                    }
                    
                }
                
                Biome biome = world.getBiomeProvider().getBiome(mutablePos.setPos(x, 0, z));
                if (terrainType == TerrainType.LAND && (BiomeDictionary.hasType(biome, Type.SANDY) || BiomeDictionary.hasType(biome, Type.BEACH))) {
                    terrainType = TerrainType.SAND;
                }
                
                if (x > -5 && x < 5 && z > -5 && z < 5) {
                    terrainType = TerrainType.CENTER;
                }
                
                image.setRGB(localX, localZ, terrainType.color);
            }
        }
        
        return image;
    }
}
