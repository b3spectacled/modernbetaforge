package mod.bespectacled.modernbetaforge.util;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Vector4f;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DrawUtil {
    private enum TerrainType {
        GRASS(COLOR_GRASS, true),
        SAND(COLOR_SAND, true),
        SNOW(COLOR_SNOW, true),
        STONE(COLOR_STONE, true),
        TERRACOTTA(COLOR_TERRACOTTA, true),
        MYCELIUM(COLOR_MYCELIUM, true),
        WATER(COLOR_WATER),
        FIRE(COLOR_FIRE),
        ICE(COLOR_ICE),
        VOID(COLOR_VOID),
        MARKER(COLOR_CENTER);
        
        private final int color;
        private final boolean snowy; 
        
        private TerrainType(int color) {
            this(color, false);
        }
        
        private TerrainType(int color, boolean snowy) {
            this.color = color;
            this.snowy = snowy;
        }
    }

    private static final int COLOR_GRASS = MathUtil.convertARGBComponentsToInt(255, 127, 178, 56);
    private static final int COLOR_SAND = MathUtil.convertARGBComponentsToInt(255, 247, 233, 163);
    private static final int COLOR_SNOW = MathUtil.convertARGBComponentsToInt(255, 255, 255, 255);
    private static final int COLOR_WATER = MathUtil.convertARGBComponentsToInt(255, 64, 64, 255);
    private static final int COLOR_ICE = MathUtil.convertARGBComponentsToInt(255, 160, 160, 255);
    private static final int COLOR_FIRE = MathUtil.convertARGBComponentsToInt(255, 255, 0, 0);
    private static final int COLOR_VOID = MathUtil.convertARGBComponentsToInt(0, 0, 0, 0);
    private static final int COLOR_STONE = MathUtil.convertARGBComponentsToInt(255, 112, 112, 112);
    private static final int COLOR_TERRACOTTA = MathUtil.convertARGBComponentsToInt(255, 216, 127, 51);
    private static final int COLOR_MYCELIUM = MathUtil.convertARGBComponentsToInt(255, 127, 63, 178);
    private static final int COLOR_CENTER = MathUtil.convertARGBComponentsToInt(255, 255, 0, 0);
    
    public static BufferedImage createBiomeMap(BiFunction<Integer, Integer, Biome> biomeFunc, int size, Consumer<Float> progressTracker) {
        return createBiomeMap(biomeFunc, size, size, false, progressTracker);
    }
    
    public static BufferedImage createTerrainMap(ChunkSource chunkSource, ModernBetaBiomeProvider biomeProvider, SurfaceBuilder surfaceBuilder, int size, Consumer<Float> progressTracker) {
        return createTerrainMap(chunkSource, biomeProvider, surfaceBuilder, size, size, false, progressTracker);
    }
    
    public static BufferedImage createBiomeMap(BiFunction<Integer, Integer, Biome> biomeFunc, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        int offsetX = width / 2;
        int offsetZ = length / 2;
        
        for (int localX = 0; localX < width; ++localX) {
            for (int localZ = 0; localZ < length; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;
    
                TerrainType terrainType = TerrainType.GRASS;
                Biome biome = biomeFunc.apply(x, z);

                terrainType = getTerrainTypeByBiome(biome, terrainType);
                terrainType = getTerrainTypeBySnowiness(biome, terrainType);
                
                if (drawMarkers) {
                    terrainType = getTerrainTypeByMarker(x, z, terrainType);
                }
                
                int biomeColor = MathUtil.convertRGBtoARGB(biome.getGrassColorAtPos(mutablePos.setPos(x, 0, z)));
                int color = terrainType == TerrainType.GRASS ? biomeColor : terrainType.color;
                
                Vector4f colorVec = MathUtil.convertARGBIntToVector4f(color);
                if (terrainType == TerrainType.GRASS) {
                    colorVec = scale(colorVec, 0.71f);
                }
                Vector4f mapColor = scale(colorVec, 0.86f);
                
                image.setRGB(localX, localZ, terrainType == TerrainType.MARKER ? TerrainType.MARKER.color : MathUtil.convertARGBVector4fToInt(mapColor));
            }
        }
        
        return image;
    }

    public static BufferedImage createTerrainMap(ChunkSource chunkSource, ModernBetaBiomeProvider biomeProvider, SurfaceBuilder surfaceBuilder, int width, int length, boolean drawMarkers, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        // Used for surface sampling, yes it won't be accurate.
        Random random = new Random(chunkSource.getSeed());
        
        int offsetX = width / 2;
        int offsetZ = length / 2;
        
        for (int localX = 0; localX < width; ++localX) {
            float progress = localX / (float)width;
            progressTracker.accept(progress);
            
            for (int localZ = 0; localZ < length; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;
                int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                Biome biome = biomeProvider.getBiome(mutablePos.setPos(x, 0, z));

                TerrainType terrainType = getBaseTerrainType(chunkSource, surfaceBuilder, x, z, height, biome, random);
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                terrainType = getTerrainTypeBySnowiness(biome, terrainType);
                
                if (drawMarkers) {
                    terrainType = getTerrainTypeByMarker(x, z, terrainType);
                }

                int color = getTerrainTypeColor(mutablePos.setPos(x, height, z), biome, biomeProvider.getBiomeSource(), true, terrainType);
                Vector4f colorVec = MathUtil.convertARGBIntToVector4f(color);
                
                if (terrainType == TerrainType.GRASS) {
                    colorVec = scale(colorVec, 0.71f);
                }
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
                
                image.setRGB(localX, localZ, terrainType == TerrainType.MARKER ? TerrainType.MARKER.color : MathUtil.convertARGBVector4fToInt(mapColor));
            }
        }
        
        return image;
    }
    
    public static BufferedImage createTerrainMapForPreview(ChunkSource chunkSource, BiomeSource biomeSource, SurfaceBuilder surfaceBuilder, int size, Consumer<Float> progressTracker) {
        return createTerrainMapForPreview(chunkSource, biomeSource, surfaceBuilder, size, false, progressTracker);
    }
    
    public static BufferedImage createTerrainMapForPreview(ChunkSource chunkSource, BiomeSource biomeSource, SurfaceBuilder surfaceBuilder, int size, boolean useBiomeColors, Consumer<Float> progressTracker) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        // Used for surface sampling, yes it won't be accurate.
        Random random = new Random(chunkSource.getSeed());
        
        int offsetX = size / 2;
        int offsetZ = size / 2;
        
        for (int localX = 0; localX < size; ++localX) {
            float progress = localX / (float)size;
            progressTracker.accept(progress);
            
            for (int localZ = 0; localZ < size; ++localZ) {
                int x = localX - offsetX;
                int z = localZ - offsetZ;
                int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                Biome biome = biomeSource.getBiome(x, z);

                TerrainType terrainType = getBaseTerrainType(chunkSource, surfaceBuilder, x, z, height, biome, random);
                terrainType = getTerrainTypeByBiome(biome, terrainType);
                terrainType = getTerrainTypeBySnowiness(biome, terrainType);

                mutablePos.setPos(x, height, z);
                int color = getTerrainTypeColor(mutablePos, biome, biomeSource, useBiomeColors, terrainType);
                Vector4f colorVec = MathUtil.convertARGBIntToVector4f(color);
                
                if (useBiomeColors && terrainType == TerrainType.GRASS) {
                    colorVec = scale(colorVec, 0.71f);
                }
                
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
        return x != 0 && z == 0;
    }
    
    private static Vector4f scale(Vector4f vector, float factor) {
        Vector4f newVector = new Vector4f(vector);
        
        newVector.y *= factor;
        newVector.z *= factor;
        newVector.w *= factor;
        
        return newVector;
    }
    
    private static TerrainType getBaseTerrainType(ChunkSource chunkSource, SurfaceBuilder surfaceBuilder, int x, int z, int height, Biome biome, Random random) {
        TerrainType terrainType = TerrainType.GRASS;
        
        if (chunkSource instanceof FiniteChunkSource && ((FiniteChunkSource)chunkSource).inWorldBounds(x, z)) {
            FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
            int offsetX = finiteChunkSource.getLevelWidth() / 2;
            int offsetZ = finiteChunkSource.getLevelLength() / 2;
            
            x += offsetX;
            z += offsetZ;
            
            Block block = finiteChunkSource.getLevelBlock(x, height, z);
            Block blockAbove = finiteChunkSource.getLevelBlock(x, height + 1, z);
            
            if (block == Blocks.SAND) {
                terrainType = TerrainType.SAND;
            } else if (block == Blocks.GRAVEL || block == Blocks.STONE) {
                terrainType = TerrainType.STONE;
            }
            
            if (blockAbove == Blocks.WATER) {
                terrainType = chunkSource.getGeneratorSettings().useLavaOceans ? TerrainType.FIRE : TerrainType.WATER;
            }
            
        } else {
            boolean generatesBeaches = surfaceBuilder.generatesBeaches(x, z, random);
            boolean generatesGravelBeaches = surfaceBuilder.generatesGravelBeaches(x, z, random);
            int surfaceDepth = surfaceBuilder.sampleSurfaceDepth(x, z, random);
            
            if (surfaceBuilder.generatesBasin(surfaceDepth)) {
                terrainType = TerrainType.STONE;
                
            } else if (surfaceBuilder.atBeachDepth(height)) {
                if (generatesGravelBeaches) {
                    terrainType = TerrainType.STONE;
                    height--;
                }
                
                if (generatesBeaches) {
                    terrainType = TerrainType.SAND;
                }
            }
            
            if (height < chunkSource.getSeaLevel() - 1) {
                terrainType = chunkSource.getGeneratorSettings().useLavaOceans ? TerrainType.FIRE : TerrainType.WATER;
            }
        }
        
        if (height <= 0) {
            terrainType = TerrainType.VOID;
        }
        
        return terrainType;
    }
    
    private static TerrainType getTerrainTypeByBiome(Biome biome, TerrainType terrainType) {
        if (terrainType == TerrainType.GRASS) {
            if (BiomeDictionary.hasType(biome, Type.MUSHROOM)) {
                terrainType = TerrainType.MYCELIUM;
                
            } else if (BiomeDictionary.hasType(biome, Type.MESA)) {
                terrainType = TerrainType.TERRACOTTA;
            
            } else if (BiomeDictionary.hasType(biome, Type.SANDY) || BiomeDictionary.hasType(biome, Type.BEACH)) {
                terrainType = TerrainType.SAND;
                
            }
        }

        return terrainType;
    }
    
    private static TerrainType getTerrainTypeBySnowiness(Biome biome, TerrainType terrainType) {
        if (terrainType.snowy) {
            if (BiomeDictionary.hasType(biome, Type.SNOWY)) {
                terrainType = TerrainType.SNOW;
            }
        }
        
        if (terrainType == TerrainType.WATER) {
            if (BiomeDictionary.hasType(biome, Type.SNOWY)) {
                terrainType = TerrainType.ICE;
            }
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
    
    private static int getTerrainTypeColor(BlockPos blockPos, Biome biome, BiomeSource biomeSource, boolean useBiomeColors, TerrainType terrainType) {
        int color = terrainType.color;
        
        if (useBiomeColors && terrainType == TerrainType.GRASS) {
            color =  biome.getGrassColorAtPos(blockPos);
            
            if (biomeSource instanceof ClimateSampler) {
                ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                
                if (climateSampler.sampleBiomeColor()) {
                    Clime clime = climateSampler.sample(blockPos.getX(), blockPos.getZ());
                    color = ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
                }
            }
            
            color = MathUtil.convertRGBtoARGB(color);
        }
        
        return color;
    }
}