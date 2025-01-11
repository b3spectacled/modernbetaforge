package mod.bespectacled.modernbetaforge.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.chunk.source.ReleaseChunkSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ChunkUtil {
    private enum TerrainType {
        LAND(COLOR_LAND),
        SAND(COLOR_SAND),
        SNOW(COLOR_SNOW),
        WATER(COLOR_WATER),
        ICE(COLOR_ICE),
        VOID(COLOR_VOID),
        MARKER(COLOR_CENTER);
        
        private int color;
        
        private TerrainType(int color) {
            this.color = color;
        }
    }
    
    private static final int COLOR_LAND = MathUtil.convertColorComponentsToInt(127, 178, 56);
    private static final int COLOR_SAND = MathUtil.convertColorComponentsToInt(247, 233, 163);
    private static final int COLOR_SNOW = MathUtil.convertColorComponentsToInt(255, 255, 255);
    private static final int COLOR_WATER = MathUtil.convertColorComponentsToInt(64, 64, 255);
    private static final int COLOR_ICE = MathUtil.convertColorComponentsToInt(160, 160, 255);
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
                
                if (terrainType == TerrainType.LAND) {
                    if (BiomeDictionary.hasType(biome, Type.SANDY) || BiomeDictionary.hasType(biome, Type.BEACH)) {
                        terrainType = TerrainType.SAND;
                    } else if (BiomeDictionary.hasType(biome, Type.SNOWY)) {
                        terrainType = TerrainType.SNOW;
                    }
                }
                
                if (terrainType == TerrainType.WATER && BiomeDictionary.hasType(biome, Type.SNOWY)) {
                    terrainType = TerrainType.ICE;
                }
                
                if (inCenter(x, z)) {
                    terrainType = TerrainType.MARKER;
                }
                
                if (onAxisX(x, z) && z % 10 == 0) {
                    terrainType = TerrainType.MARKER;
                }
                
                if (onAxisZ(x, z) && x % 10 == 0) {
                    terrainType = TerrainType.MARKER;
                }
                
                image.setRGB(localX, localZ, terrainType.color);
            }
        }
        
        BufferedImage rotatedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphic = rotatedImage.createGraphics();
        graphic.rotate(Math.toRadians(-90), width / 2, length / 2);
        graphic.drawImage(image, null, 0, 0);
        
        return rotatedImage;
    }
    
    private static boolean inCenter(int x, int z) {
        return inCenter(x, z, 0, 0);
    }
    
    private static boolean inCenter(int x, int z, int offsetX, int offsetZ) {
        x += offsetX;
        z += offsetZ;
        
        if (x > 7 || x < -7 || z > 7 || z < -7) {
            return false;
        }
        
        int distance = (int)MathHelper.sqrt(x * x + z * z);
        
        if (distance > 0 && distance < 3) {
            return true;
        } else if (distance > 3 && distance < 4) {
            return false;
        } else if (distance > 4 && distance < 7) {
            return true;
        }
        
        return false;
    }
    
    private static boolean onAxisX(int x, int z) {
        return x == 0 && z != 0;
    }
    
    private static boolean onAxisZ(int x, int z) {
        return x !=0 && z ==0;
    }
}
