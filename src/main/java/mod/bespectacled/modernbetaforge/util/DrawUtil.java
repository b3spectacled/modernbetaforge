package mod.bespectacled.modernbetaforge.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DrawUtil {
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
    
    public static BufferedImage createBiomeMap(BiFunction<Integer, Integer, Biome> biomeFunc, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_RGB);
        
        int offsetX = width / 2;
        int offsetZ = length / 2;
        
        for (int localX = 0; localX < width; ++localX) {
            for (int localZ = 0; localZ < length; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;

                TerrainType terrainType = TerrainType.LAND;
                Biome biome = biomeFunc.apply(x, z);
                
                if (BiomeDictionary.hasType(biome, Type.OCEAN) || BiomeDictionary.hasType(biome, Type.RIVER)) {
                    terrainType = TerrainType.WATER;
                }
                
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                if (drawMarkers) {
                    terrainType = getTerrainTypeByMarker(x, z, terrainType);
                }
                
                image.setRGB(localX, localZ, terrainType.color);
            }
        }
        
        return rotateImage(image);
    }
    
    public static BufferedImage createTerrainMap(World world, ChunkSource chunkSource, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
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
                int height = chunkSource.getHeight(world, x, z, HeightmapChunk.Type.SURFACE);
                
                if (height < chunkSource.getSeaLevel() - 1) {
                    terrainType = TerrainType.WATER;
                }
                
                if (height == 0) {
                    terrainType = TerrainType.VOID;
                }
                
                Biome biome = world.getBiomeProvider().getBiome(mutablePos.setPos(x, 0, z));
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                if (drawMarkers) {
                    terrainType = getTerrainTypeByMarker(x, z, terrainType);
                }
                
                image.setRGB(localX, localZ, terrainType.color);
            }
        }
        
        return rotateImage(image);
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
    
    private static TerrainType getTerrainTypeByBiome(Biome biome, TerrainType terrainType) {
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
        
        return terrainType;
    }
    
    private static TerrainType getTerrainTypeByMarker(int x, int z, TerrainType terrainType) {
        if (inCenter(x, z)) {
            terrainType = TerrainType.MARKER;
        }
        
        if (onAxisX(x, z) && z % 10 == 0) {
            terrainType = TerrainType.MARKER;
        }
        
        if (onAxisZ(x, z) && x % 10 == 0) {
            terrainType = TerrainType.MARKER;
        }
        
        return terrainType;
    }
    
    private static BufferedImage rotateImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage rotatedImage = new BufferedImage(width, height, image.getType());
        Graphics2D graphic = rotatedImage.createGraphics();
        graphic.rotate(Math.toRadians(180), width / 2, height / 2);
        graphic.drawImage(image, null, 0, 0);
        
        return rotatedImage;
    }
}
