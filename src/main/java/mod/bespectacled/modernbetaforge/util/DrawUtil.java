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
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSky;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
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
        NETHER(COLOR_NETHER, true),
        SOUL_SAND(COLOR_SOUL_SAND, true),
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
    private static final int COLOR_NETHER = MathUtil.convertARGBComponentsToInt(255, 112, 2, 0);
    private static final int COLOR_SOUL_SAND = MathUtil.convertARGBComponentsToInt(255, 102, 76, 51);
    private static final int COLOR_CENTER = MathUtil.convertARGBComponentsToInt(255, 255, 0, 0);
    
    public static BufferedImage createTerrainMap(
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        SurfaceBuilder surfaceBuilder,
        BiomeInjectionRules injectionRules,
        int size,
        boolean useBiomeBlend,
        Consumer<Float> progressTracker
    ) {
        return createTerrainMap(chunkSource, biomeSource, surfaceBuilder, injectionRules, 0, 0, size, size, useBiomeBlend, progressTracker);
    }
    
    public static BufferedImage createTerrainMap(
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        SurfaceBuilder surfaceBuilder,
        BiomeInjectionRules injectionRules,
        int centerX,
        int centerZ,
        int sizeX,
        int sizeZ,
        boolean useBiomeBlend,
        Consumer<Float> progressTracker
    ) {
        ChunkCache<BiomeChunk> biomeCache = new ChunkCache<>("biome_draw", (chunkX, chunkZ) -> new BiomeChunk(
            chunkX,
            chunkZ,
            (x, z) -> getInjectedBiome(x, z, chunkSource, biomeSource, surfaceBuilder, injectionRules)
        ));
        
        BufferedImage image = new BufferedImage(sizeX, sizeZ, BufferedImage.TYPE_INT_ARGB);
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        Random random = new Random(chunkSource.getSeed());
        
        int chunkWidth = sizeX >> 4;
        int chunkLength = sizeZ >> 4;
        
        int offsetX = sizeX / 2;
        int offsetZ = sizeZ / 2;
        
        Block defaultFluid = chunkSource.getDefaultFluid().getBlock();
        int snowLineOffset = chunkSource.getGeneratorSettings().snowLineOffset;
        
        for (int chunkX = 0; chunkX < chunkWidth; ++chunkX) {
            int startX = chunkX << 4;
            progressTracker.accept(chunkX / (float)chunkWidth);
            
            for (int chunkZ = 0; chunkZ < chunkLength; ++chunkZ) {
                int startZ = chunkZ << 4;
                
                random = surfaceBuilder.createSurfaceRandom(chunkX, chunkZ);
                
                for (int localX = 0; localX < 16; ++localX) {
                    int x = startX + localX - offsetX + centerX;
                    int imageX = startX + localX;
                   
                    for (int localZ = 0; localZ < 16; ++localZ) {
                        int z = startZ + localZ - offsetZ + centerZ;
                        int imageY = startZ + localZ;
                        
                        int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
                        int oceanHeight = chunkSource.getHeight(x, z, HeightmapChunk.Type.OCEAN);
                        Biome biome = biomeSource.getBiome(x, z);
                        mutablePos.setPos(x, oceanHeight, z);

                        TerrainType terrainType = getBaseTerrainType(chunkSource, surfaceBuilder, x, z, height, biome, random);
                        terrainType = getTerrainTypeByBiome(biome, terrainType);
                        terrainType = getTerrainTypeBySnowiness(mutablePos, biome, biomeSource, defaultFluid, terrainType, snowLineOffset);

                        int color = getTerrainTypeColor(mutablePos, biome, chunkSource, biomeSource, biomeCache, useBiomeBlend, terrainType);
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
    
    public static BiomeInjectionContext createInjectionContext(ChunkSource chunkSource, SurfaceBuilder surfaceBuilder, int x, int z, Biome biome) {
        int height = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
        boolean inWater = height < chunkSource.getSeaLevel() - 1;
        
        if (chunkSource instanceof FiniteChunkSource && ((FiniteChunkSource)chunkSource).inWorldBounds(x, z)) {
            FiniteChunkSource finiteChunkSource = (FiniteChunkSource)chunkSource;
            int offsetX = finiteChunkSource.getLevelWidth() / 2;
            int offsetZ = finiteChunkSource.getLevelLength() / 2;
                
            x += offsetX;
            z += offsetZ;
            
            Block blockAbove = finiteChunkSource.getLevelBlock(x, height + 1, z);
            
            inWater = blockAbove == chunkSource.getDefaultFluid().getBlock();
        }
        
        IBlockState state = chunkSource.getDefaultBlock();
        IBlockState stateAbove = inWater ? chunkSource.getDefaultFluid() : BlockStates.AIR;
        
        if (surfaceBuilder instanceof NoiseSurfaceBuilder && !surfaceBuilder.isCustomSurface(biome)) {
            NoiseSurfaceBuilder noiseSurfaceBuilder = (NoiseSurfaceBuilder)surfaceBuilder;
            
            if (noiseSurfaceBuilder.atBeachDepth(height) && noiseSurfaceBuilder.isBeach(x, z, null)) {
                state = BlockStates.SAND;
            }
        }
        
        return new BiomeInjectionContext(new BlockPos(x, height, z), state, stateAbove, biome, biome);
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
        TerrainType defaultBlock = getTerrainTypeByStone(chunkSource);
        TerrainType defaultFluid = getTerrainTypeByFluid(chunkSource);
        boolean isNether = BiomeDictionary.hasType(biome, Type.NETHER);
        
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
                
                if (blockAbove == chunkSource.getDefaultFluid().getBlock()) {
                    terrainType = defaultFluid;
                }
                
            } else if (height < chunkSource.getSeaLevel() - 1) {
                terrainType = defaultFluid;
            }
            
        } else if (surfaceBuilder instanceof NoiseSurfaceBuilder && !surfaceBuilder.isCustomSurface(biome)) {
            NoiseSurfaceBuilder noiseSurfaceBuilder = (NoiseSurfaceBuilder)surfaceBuilder;
            
            boolean isBeach = noiseSurfaceBuilder.isBeach(x, z, random);
            boolean isGravelBeach = noiseSurfaceBuilder.isGravelBeach(x, z, random);
            int surfaceDepth = noiseSurfaceBuilder.sampleSurfaceDepth(x, z, random);
            
            if (noiseSurfaceBuilder.isBasin(surfaceDepth)) {
                terrainType = defaultBlock;
                
            } else if (noiseSurfaceBuilder.atBeachDepth(height)) {
                if (isGravelBeach) {
                    terrainType = TerrainType.STONE;
                    
                    if (!isNether) {
                        height--;
                    }
                }
                
                if (isBeach) {
                    terrainType = isNether ? TerrainType.SOUL_SAND : TerrainType.SAND;
                    
                    // Undo above height change if no gravel beaches
                    if (isGravelBeach && !isNether) {
                        height++;
                    }
                }
            }
            
            if (height < chunkSource.getSeaLevel() - 1) {
                terrainType = defaultFluid;
            }
            
        } else if (height < chunkSource.getSeaLevel() - 1) {
            terrainType = defaultFluid;
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
                
            } else if (BiomeDictionary.hasType(biome, Type.NETHER)) {
                terrainType = TerrainType.NETHER;
                
            } else if (BiomeDictionary.hasType(biome, Type.END)) {
                terrainType = TerrainType.SAND;
                
            }
        }

        return terrainType;
    }
    
    private static TerrainType getTerrainTypeBySnowiness(BlockPos blockPos, Biome biome, BiomeSource biomeSource, Block defaultFluid, TerrainType terrainType, int snowLineOffset) {
        if (terrainType.snowy) {
            if (canFreeze(blockPos, biome, biomeSource, snowLineOffset)) {
                terrainType = TerrainType.SNOW;
            }
        }
        
        if (terrainType == TerrainType.WATER && defaultFluid == Blocks.WATER) {
            if (canFreeze(blockPos, biome, biomeSource, snowLineOffset)) {
                terrainType = TerrainType.ICE;
            }
        }
        
        return terrainType;
    }
    
    private static TerrainType getTerrainTypeByStone(ChunkSource chunkSource) {
        ResourceLocation defaultBlock = chunkSource.getGeneratorSettings().defaultBlock;
        
        if (defaultBlock.equals(Blocks.NETHERRACK.getRegistryName())) {
            return TerrainType.NETHER;
            
        } else if (defaultBlock.equals(Blocks.END_STONE.getRegistryName())) {
            return TerrainType.SAND;
            
        }
        
        return TerrainType.STONE;
    }

    private static TerrainType getTerrainTypeByFluid(ChunkSource chunkSource) {
        ResourceLocation defaultFluid = chunkSource.getGeneratorSettings().defaultFluid;
        
        return defaultFluid.equals(Blocks.LAVA.getRegistryName()) ? TerrainType.FIRE : TerrainType.WATER;
    }
    
    private static boolean canFreeze(BlockPos blockPos, Biome biome, BiomeSource biomeSource, int snowLineOffset) {
        int x = blockPos.getX();
        int y = blockPos.getY() + 1;
        int z = blockPos.getZ();
        boolean canFreeze = false;
        
        if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleForFeatureGeneration()) {
            double temp = ((ClimateSampler)biomeSource).sample(x, z).temp();
            temp = temp - ((double)(y - snowLineOffset) / (double)snowLineOffset) * 0.3;
            
            canFreeze = temp < 0.5;
            
        } else if (biome instanceof BiomeBetaSky) {
            double temp = biome.getDefaultTemperature();
            temp = temp - ((double)(y - snowLineOffset) / (double)snowLineOffset) * 0.3;
            
            canFreeze = temp < 0.5;
            
        } else {
            double temp = biome.getTemperature(blockPos);
            
            canFreeze = temp < 0.15;
        }
        
        return canFreeze;
    }
    
    private static int getTerrainTypeColor(
        BlockPos blockPos,
        Biome biome,
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        ChunkCache<BiomeChunk> biomeCache,
        boolean useBiomeBlend,
        TerrainType terrainType
    ) {
        int color = terrainType.color;
        int x = blockPos.getX();
        int z = blockPos.getZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        biome = biomeCache.get(chunkX, chunkZ).sample(x, z);
        
        if (terrainType == TerrainType.GRASS) {
            color = biome.getGrassColorAtPos(blockPos);
            
            if (useBiomeBlend) {
                if (biomeSource instanceof ClimateSampler && ((ClimateSampler)biomeSource).sampleBiomeColor()) {
                    ClimateSampler climateSampler = (ClimateSampler)biomeSource;
                    Clime clime = climateSampler.sample(blockPos.getX(), blockPos.getZ());
                    color = ColorizerGrass.getGrassColor(clime.temp(), clime.rain());
                    
                } else {
                    color = blendBiomeColor(blockPos, biomeCache);
                    
                }
            }
            
            color = MathUtil.convertRGBtoARGB(color);
            
        } else if (terrainType == TerrainType.WATER) {
            ResourceLocation defaultFluid = chunkSource.getGeneratorSettings().defaultFluid;
            
            if (!defaultFluid.equals(Blocks.WATER.getRegistryName())) {
                int fluidColor = ForgeRegistryUtil.getFluid(defaultFluid).getColor();
                
                if (fluidColor != -1) {
                    color = fluidColor;
                }
            }
        }
        
        return color;
    }
    
    private static int blendBiomeColor(BlockPos blockPos, ChunkCache<BiomeChunk> biomeCache) {
        Vec3d color = Vec3d.ZERO;
        int centerX = blockPos.getX();
        int centerZ = blockPos.getZ();
        int blendDist = 2;
        MutableBlockPos mutablePos = new MutableBlockPos();
        
        int blocks = 0;
        for (int x = centerX - blendDist; x <= centerX + blendDist; ++x) {
            for (int z = centerZ - blendDist; z <= centerZ + blendDist; ++z) {
                int chunkX = x >> 4;
                int chunkZ = z >> 4;
                
                Biome biome = biomeCache.get(chunkX, chunkZ).sample(x, z);
                
                color = color.add(MathUtil.convertRGBIntToVec3d(biome.getGrassColorAtPos(mutablePos.setPos(x, 0, z))));
                blocks++;
            }
        }
        
        return MathUtil.convertRGBVec3dToInt(color.scale(1.0 / blocks));
    }
    
    private static Biome[] getInjectedBiome(
        int startX,
        int startZ,
        ChunkSource chunkSource,
        BiomeSource biomeSource,
        SurfaceBuilder surfaceBuilder,
        BiomeInjectionRules injectionRules
    ) {
        Biome[] biomes = new Biome[256];
        
        int ndx = 0;
        for (int z = startZ; z < startZ + 16; ++z) {
            for (int x = startX; x < startX + 16; ++x) {
                Biome biome = biomeSource.getBiome(x, z);
                BiomeInjectionContext context = createInjectionContext(chunkSource, surfaceBuilder, x, z, biome);
                
                Biome injectedBiome = injectionRules.test(context, x, z, BiomeInjectionStep.PRE_SURFACE);
                biome = injectedBiome != null ? injectedBiome : biome;
                context.setBiome(biome);
                
                injectedBiome = injectionRules.test(context, x, z, BiomeInjectionStep.CUSTOM);
                biome = injectedBiome != null ? injectedBiome : biome;
                context.setBiome(biome);
                
                injectedBiome = injectionRules.test(context, x, z, BiomeInjectionStep.POST_SURFACE);
                biomes[ndx++] = injectedBiome != null ? injectedBiome : biome;
            }
        }
        
        return biomes;
    }
}