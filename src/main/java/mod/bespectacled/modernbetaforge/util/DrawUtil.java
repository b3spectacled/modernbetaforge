package mod.bespectacled.modernbetaforge.util;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.ImmutableSet;

import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInTypes;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DrawUtil {
    private enum TerrainType {
        LAND(COLOR_LAND),
        SAND(COLOR_SAND),
        SNOW(COLOR_SNOW),
        WATER(COLOR_WATER),
        FIRE(COLOR_FIRE),
        ICE(COLOR_ICE),
        VOID(COLOR_VOID),
        STONE(COLOR_STONE),
        MARKER(COLOR_CENTER);
        
        private int color;
        
        private TerrainType(int color) {
            this.color = color;
        }
    }
    
    private static final Set<ResourceLocation> INVALID_CHUNK_SOURCES = ImmutableSet.<ResourceLocation>builder()
        .add(ModernBetaBuiltInTypes.Chunk.SKYLANDS.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Chunk.CLASSIC_0_0_23A.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Chunk.INDEV.getRegistryKey())
        .build();
    
    private static final Set<ResourceLocation> VALID_BEACH_SURFACES = ImmutableSet.<ResourceLocation>builder()
        .add(ModernBetaBuiltInTypes.Surface.ALPHA.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Surface.ALPHA_1_2.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Surface.BETA.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Surface.PE.getRegistryKey())
        .add(ModernBetaBuiltInTypes.Surface.INFDEV.getRegistryKey())
        .build();
    
    private static final int COLOR_LAND = MathUtil.convertARGBComponentsToInt(255, 127, 178, 56);
    private static final int COLOR_SAND = MathUtil.convertARGBComponentsToInt(255, 247, 233, 163);
    private static final int COLOR_SNOW = MathUtil.convertARGBComponentsToInt(255, 255, 255, 255);
    private static final int COLOR_WATER = MathUtil.convertARGBComponentsToInt(255, 64, 64, 255);
    private static final int COLOR_ICE = MathUtil.convertARGBComponentsToInt(255, 160, 160, 255);
    private static final int COLOR_FIRE = MathUtil.convertARGBComponentsToInt(255, 255, 0, 0);
    private static final int COLOR_VOID = MathUtil.convertARGBComponentsToInt(0, 0, 0, 0);
    private static final int COLOR_STONE = MathUtil.convertARGBComponentsToInt(255, 112, 112, 112);
    private static final int COLOR_CENTER = MathUtil.convertARGBComponentsToInt(255, 255, 0, 0);
    
    public static BufferedImage createBiomeMap(BiFunction<Integer, Integer, Biome> biomeFunc, int size, Consumer<Float> progressTracker) {
        return createBiomeMap(biomeFunc, size, size, false, progressTracker);
    }
    
    public static BufferedImage createBiomeMap(BiFunction<Integer, Integer, Biome> biomeFunc, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        
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
        
        return image;
    }

    public static BufferedImage createTerrainMap(ChunkSource chunkSource, BiomeProvider biomeProvider, int size, Consumer<Float> progressTracker) {
        return createTerrainMap(chunkSource, biomeProvider, size, size, false, progressTracker);
    }
    
    public static BufferedImage createTerrainMap(ChunkSource chunkSource, BiomeProvider biomeProvider, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
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
                int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                
                if (height < chunkSource.getSeaLevel() - 1) {
                    terrainType = chunkSource.getGeneratorSettings().useLavaOceans ? TerrainType.FIRE : TerrainType.WATER;
                }
                
                if (height == 0) {
                    terrainType = TerrainType.VOID;
                }
                
                Biome biome = biomeProvider.getBiome(mutablePos.setPos(x, 0, z));
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                
                if (drawMarkers) {
                    terrainType = getTerrainTypeByMarker(x, z, terrainType);
                }
                
                Vector4f colorVec = MathUtil.convertARGBIntToVector4f(terrainType.color);
                Vector4f mapColor = scale(colorVec, 0.86f);
                mapColor.setX(1.0f);
                
                int elevationDiff = chunkSource.getHeight(x, z + 1, HeightmapChunk.Type.SURFACE) - height;
                if (terrainType == TerrainType.ICE) {
                    elevationDiff = 0;
                }
                
                if (elevationDiff > 0) {
                    mapColor = colorVec;
                } else if (elevationDiff < 0) {
                    mapColor = scale(colorVec, 0.71f);
                }
                
                image.setRGB(localX, localZ, terrainType == TerrainType.MARKER ? TerrainType.MARKER.color : MathUtil.convertARGBVector4fToInt(mapColor));
            }
        }
        
        return image;
    }
    
    public static BufferedImage createTerrainMapForPreview(ChunkSource chunkSource, BiomeSource biomeSource, int size, Consumer<Float> progressTracker) {
        return createTerrainMapForPreview(chunkSource, biomeSource, size, false, progressTracker);
    }
    
    public static BufferedImage createTerrainMapForPreview(ChunkSource chunkSource, BiomeSource biomeSource, int size, boolean drawBeaches, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        ResourceLocation chunkSourceKey = new ResourceLocation(chunkSource.getGeneratorSettings().chunkSource);
        ResourceLocation surfaceBuilderKey = new ResourceLocation(chunkSource.getGeneratorSettings().surfaceBuilder);
        
        int offsetX = size / 2;
        int offsetZ = size / 2;
        
        for (int localX = 0; localX < size; ++localX) {
            float progress = localX / (float)size;
            progressTracker.accept(progress);
            
            for (int localZ = 0; localZ < size; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;

                TerrainType terrainType = TerrainType.LAND;
                int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                
                if (height < chunkSource.getSeaLevel() - 1) {
                    terrainType = chunkSource.getGeneratorSettings().useLavaOceans ? TerrainType.FIRE : TerrainType.WATER;
                }
                
                if (height == 0) {
                    terrainType = TerrainType.VOID;
                }
                
                boolean isBeach = terrainType == TerrainType.LAND && height < chunkSource.getSeaLevel() + 2;
                
                Biome biome = biomeSource.getBiome(x, z);
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                
                if (isBeach && isBeachSurfaceBuilder(chunkSourceKey, surfaceBuilderKey)) {
                    if (isSandyBeach(x, z, chunkSource.getBeachOctaveNoise()) && terrainType != TerrainType.SNOW) {
                        terrainType = TerrainType.SAND;
                    }
                }
                
                Vector4f colorVec = MathUtil.convertARGBIntToVector4f(terrainType.color);
                Vector4f mapColor = scale(colorVec, 0.86f);
                
                int elevationDiff = chunkSource.getHeight(x, z + 1, HeightmapChunk.Type.SURFACE) - height;
                if (terrainType == TerrainType.ICE) {
                    elevationDiff = 0;
                }
                
                if (elevationDiff > 0) {
                    mapColor = colorVec;
                } else if (elevationDiff < 0) {
                    mapColor = scale(colorVec, 0.71f);
                }
                
                image.setRGB(localX, localZ, MathUtil.convertARGBVector4fToInt(mapColor));
            }
        }
        
        return image;
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
    
    private static Vector4f scale(Vector4f vector, float factor) {
        Vector4f newVector = new Vector4f(vector);
        
        newVector.y *= factor;
        newVector.z *= factor;
        newVector.w *= factor;
        
        return newVector;
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
    
    private static boolean isSandyBeach(int x, int z, Optional<PerlinOctaveNoise> noise) {
        if (noise.isPresent()) {
            return noise.get().sample(x * 0.03125, z * 0.03125, 0.0) > 0.0;
        }
        
        return false;
    }

    private static boolean isBeachSurfaceBuilder(ResourceLocation chunkSource, ResourceLocation surfaceBuilder) {
        return !INVALID_CHUNK_SOURCES.contains(chunkSource) && VALID_BEACH_SURFACES.contains(surfaceBuilder);
    }
}
