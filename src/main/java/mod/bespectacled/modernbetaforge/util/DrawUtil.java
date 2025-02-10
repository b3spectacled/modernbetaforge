package mod.bespectacled.modernbetaforge.util;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.function.Consumer;

import org.lwjgl.util.vector.Vector4f;

import mod.bespectacled.modernbetaforge.api.world.biome.climate.ClimateSampler;
import mod.bespectacled.modernbetaforge.api.world.biome.climate.Clime;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.NoiseSurfaceBuilder;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBeta;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.Vec3d;
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
    
    public static BufferedImage createTerrainMapForPreview(
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        SurfaceBuilder surfaceBuilder,
        BiomeInjectionRules injectionRules,
        int size,
        Consumer<Float> progressTracker
    ) {
        return createTerrainMapForPreview(chunkSource, biomeSource, surfaceBuilder, injectionRules, size, false, progressTracker);
    }
    
    public static BufferedImage createTerrainMapForPreview(
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        SurfaceBuilder surfaceBuilder,
        BiomeInjectionRules injectionRules,
        int size,
        boolean useBiomeBlend,
        Consumer<Float> progressTracker
    ) {
        // Make sure to reset climate samplers if world was previously loaded.
        BetaColorSampler.INSTANCE.resetClimateSamplers();
        
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        Random random = new Random(chunkSource.getSeed());
        
        int chunkWidth = size >> 4;
        int chunkLength = size >> 4;
        
        int offsetX = size / 2;
        int offsetZ = size / 2;
        
        for (int chunkX = 0; chunkX < chunkWidth; ++chunkX) {
            int startX = chunkX << 4;
            progressTracker.accept(chunkX / (float)chunkWidth);
            
            for (int chunkZ = 0; chunkZ < chunkLength; ++chunkZ) {
                int startZ = chunkZ << 4;
                
                random = surfaceBuilder.createSurfaceRandom(chunkX, chunkZ);
                
                for (int localX = 0; localX < 16; ++localX) {
                    int x = startX + localX - offsetX;
                    int imageX = startX + localX;
                   
                    for (int localZ = 0; localZ < 16; ++localZ) {
                        int z = startZ + localZ - offsetZ;
                        int imageY = startZ + localZ;
                        
                        int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                        int oceanHeight = chunkSource.getHeight(x, z, HeightmapChunk.Type.OCEAN);
                        Biome biome = biomeSource.getBiome(x, z);
                        mutablePos.setPos(x, oceanHeight, z);

                        TerrainType terrainType = getBaseTerrainType(chunkSource, surfaceBuilder, x, z, height, biome, random);
                        terrainType = getTerrainTypeByBiome(biome, terrainType);
                        terrainType = getTerrainTypeBySnowiness(mutablePos, biome, biomeSource, terrainType);

                        int color = getTerrainTypeColor(mutablePos, biome, chunkSource, biomeSource, injectionRules, useBiomeBlend, terrainType);
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
                        
                        image.setRGB(imageX, imageY, MathUtil.convertARGBVector4fToInt(mapColor));
                    }
                }
            }
        }
        
        return image;
    }
    
    public static BiomeInjectionContext createInjectionContext(ChunkSource chunkSource, int x, int z, Biome biome) {
        int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
        boolean inWater = height < chunkSource.getSeaLevel() - 1;
        
        if (chunkSource instanceof FiniteChunkSource && ((FiniteChunkSource)chunkSource).inWorldBounds(x, z)) {
            FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
            int offsetX = finiteChunkSource.getLevelWidth() / 2;
            int offsetZ = finiteChunkSource.getLevelLength() / 2;
                
            x += offsetX;
            z += offsetZ;
            
            Block blockAbove = finiteChunkSource.getLevelBlock(x, height + 1, z);
            
            inWater = blockAbove == Blocks.WATER;
        }
        
        IBlockState stateAbove = inWater ? BlockStates.WATER : BlockStates.AIR;
        
        return new BiomeInjectionContext(new BlockPos(x, height, z), BlockStates.STONE, stateAbove, biome);
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
        
        if (chunkSource instanceof FiniteChunkSource) {
            FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
            
            if (finiteChunkSource.inWorldBounds(x, z)) {
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
                    terrainType = getTerrainTypeByFluid(chunkSource);
                }
                
            } else if (height < chunkSource.getSeaLevel() - 1) {
                terrainType = getTerrainTypeByFluid(chunkSource);
            }
            
        } else if (surfaceBuilder instanceof NoiseSurfaceBuilder && !surfaceBuilder.isCustomSurface(biome)) {
            NoiseSurfaceBuilder noiseSurfaceBuilder = (NoiseSurfaceBuilder)surfaceBuilder;
            
            boolean isBeach = noiseSurfaceBuilder.isBeach(x, z, random);
            boolean isGravelBeach = noiseSurfaceBuilder.isGravelBeach(x, z, random);
            int surfaceDepth = noiseSurfaceBuilder.sampleSurfaceDepth(x, z, random);
            
            if (noiseSurfaceBuilder.isBasin(surfaceDepth)) {
                terrainType = TerrainType.STONE;
                
            } else if (noiseSurfaceBuilder.atBeachDepth(height)) {
                if (isGravelBeach) {
                    terrainType = TerrainType.STONE;
                    height--;
                }
                
                if (isBeach) {
                    terrainType = TerrainType.SAND;
                    
                    // Undo above height change if no gravel beaches
                    if (isGravelBeach) {
                        height++;
                    }
                }
            }
            
            if (height < chunkSource.getSeaLevel() - 1) {
                terrainType = getTerrainTypeByFluid(chunkSource);
            }
            
        } else if (height < chunkSource.getSeaLevel() - 1) {
            terrainType = getTerrainTypeByFluid(chunkSource);
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
    
    private static TerrainType getTerrainTypeBySnowiness(BlockPos blockPos, Biome biome, BiomeSource biomeSource, TerrainType terrainType) {
        if (terrainType.snowy) {
            if (canFreeze(blockPos, biome, biomeSource, 64)) {
                terrainType = TerrainType.SNOW;
            }
        }
        
        if (terrainType == TerrainType.WATER) {
            if (canFreeze(blockPos, biome, biomeSource, 64)) {
                terrainType = TerrainType.ICE;
            }
        }
        
        return terrainType;
    }
    
    private static TerrainType getTerrainTypeByFluid(ChunkSource chunkSource) {
        return chunkSource.getGeneratorSettings().useLavaOceans ? TerrainType.FIRE : TerrainType.WATER;
    }
    
    private static boolean canFreeze(BlockPos blockPos, Biome biome, BiomeSource biomeSource, int seaLevel) {
        int x = blockPos.getX();
        int y = blockPos.getY() + 1;
        int z = blockPos.getZ();
        boolean canFreeze = false;
        
        if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleForFeatureGeneration()) {
            double temp = ((ClimateSampler)biomeSource).sample(x, z).temp();
            temp = temp - ((double)(y - seaLevel) / (double)seaLevel) * 0.3;
            
            canFreeze = temp < 0.5;
            
        } else if (biome instanceof BiomeBeta) {
            double temp = biome.getDefaultTemperature();
            temp = temp - ((double)(y - seaLevel) / (double)seaLevel) * 0.3;
            
            canFreeze = temp < 0.5;
            
        } else {
            double temp = biome.getTemperature(blockPos);
            
            canFreeze = temp < 0.15;
        }
        
        return canFreeze;
    }
    
    private static int getTerrainTypeColor(BlockPos blockPos, Biome biome, ChunkSource chunkSource, BiomeSource biomeSource, BiomeInjectionRules injectionRules, boolean useBiomeBlend, TerrainType terrainType) {
        int color = terrainType.color;
        int x = blockPos.getX();
        int z = blockPos.getZ();
        
        BiomeInjectionContext context = createInjectionContext(chunkSource, x, z, biome);
        Biome injectedBiome = injectionRules.test(context, x, z, BiomeInjectionStep.PRE_SURFACE);
        biome = injectedBiome != null ? injectedBiome : biome;
        
        if (terrainType == TerrainType.GRASS) {
            color = biome.getGrassColorAtPos(blockPos);
            
            if (useBiomeBlend) {
                if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleBiomeColor()) {
                    ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                    Clime clime = climateSampler.sample(blockPos.getX(), blockPos.getZ());
                    color = ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
                    
                } else {
                    color = blendBiomeColor(blockPos, chunkSource, biomeSource, injectionRules);
                    
                }
            }
            
            color = MathUtil.convertRGBtoARGB(color);
        }
        
        return color;
    }
    
    private static int blendBiomeColor(BlockPos blockPos, ChunkSource chunkSource, BiomeSource biomeSource, BiomeInjectionRules injectionRules) {
        Vec3d color = Vec3d.ZERO;
        int centerX = blockPos.getX();
        int centerZ = blockPos.getZ();
        int blendDist = 3;
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        int blocks = 0;
        for (int x = centerX - blendDist; x <= centerX + blendDist; ++x) {
            for (int z = centerZ - blendDist; z <= centerZ + blendDist; ++z) {
                Biome biome = biomeSource.getBiome(x, z);

                BiomeInjectionContext context = createInjectionContext(chunkSource, x, z, biome);
                Biome injectedBiome = injectionRules.test(context, x, z, BiomeInjectionStep.PRE_SURFACE);
                biome = injectedBiome != null ? injectedBiome : biome;
                
                color = color.add(MathUtil.convertRGBIntToVec3d(biome.getGrassColorAtPos(mutablePos.setPos(x, 0, z))));
                blocks++;
            }
        }
        
        return MathUtil.convertRGBVec3dToInt(color.scale(1.0 / blocks));
    }
}