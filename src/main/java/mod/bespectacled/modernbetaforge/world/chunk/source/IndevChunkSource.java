package mod.bespectacled.modernbetaforge.world.chunk.source;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoiseCombined;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevTheme;
import mod.bespectacled.modernbetaforge.world.chunk.indev.IndevType;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class IndevChunkSource extends FiniteChunkSource {
    private final IndevTheme levelTheme;
    private final IndevType levelType;
    private final int seaLevel;
    
    private PerlinOctaveNoiseCombined lowOctaveNoise;
    private PerlinOctaveNoiseCombined highOctaveNoise;
    private PerlinOctaveNoise selectorOctaveNoise;
    private PerlinOctaveNoise islandOctaveNoise;
    private PerlinOctaveNoiseCombined erodeSelectorOctaveNoise;
    private PerlinOctaveNoiseCombined erodeOctaveNoise;
    private PerlinOctaveNoise soilOctaveNoise;
    private PerlinOctaveNoise floatingOctaveNoise;
    private PerlinOctaveNoise sandOctaveNoise;
    private PerlinOctaveNoise gravelOctaveNoise;
    
    private int waterLevel;
    private int groundLevel;
    
    public IndevChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaGeneratorSettings chunkGeneratorSettings
    ) {
        super(world, chunkGenerator, chunkGeneratorSettings);
        
        this.levelTheme = IndevTheme.fromId(settings.levelTheme);
        this.levelType = IndevType.fromId(settings.levelType);
        this.seaLevel = this.levelType == IndevType.FLOATING ? 0 : this.levelHeight - 32;

        int cloudHeight = this.levelType == IndevType.FLOATING ?
            -16 :
            this.levelTheme == IndevTheme.PARADISE ?
                this.levelHeight + 64 :
                this.levelHeight + 2;
        this.setCloudHeight(cloudHeight);
        
        // Set world sea level for the Indev type
        int worldSeaLevel = this.levelHeight - 32;
        
        if (this.levelType == IndevType.FLOATING) {
            worldSeaLevel = 0;
        } else if (this.levelType == IndevType.INLAND) {
            worldSeaLevel = this.levelHeight - 48;
        }
        
        this.world.setSeaLevel(worldSeaLevel);
    }
    
    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    protected void pregenerateTerrain() {
        int layers = this.levelType == IndevType.FLOATING ? (this.levelHeight - 64) / 48 + 1 : 1;
        
        for (int layer = 0; layer < layers; ++layer) { 
            this.waterLevel = this.levelHeight - 32 - layer * 48;
            this.groundLevel = this.waterLevel - 2;
            
            this.lowOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
            this.highOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
            this.selectorOctaveNoise = new PerlinOctaveNoise(this.random, 6, false);
            this.islandOctaveNoise = new PerlinOctaveNoise(this.random, 2, false);
            
            this.erodeSelectorOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
            this.erodeOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
            
            this.soilOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
            this.floatingOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
            
            this.sandOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
            this.gravelOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
            
            this.raiseLevel();
            this.erodeLevel();
            this.soilLevel();
            this.growLevel();
        }
        
        if (this.settings.useIndevCaves)
            this.carveLevel();
        
        this.oreLevel();
        this.meltLevel();
        this.updateLevel();
        this.waterLevel();
        this.plantLevel();
        this.assembleLevel();
    }

    @Override
    protected void generateBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
         switch(this.levelType) {
            case ISLAND: 
                this.generateWaterBorder(chunkPrimer, chunkX, chunkZ);
                break;
            case INLAND:
                this.generateWorldBorder(chunkPrimer, chunkX, chunkZ);
                break;
            case FLOATING:
                break;
        }
    }
    
    @Override
    protected int getBorderHeight(int x, int z, HeightmapChunk.Type type) {
        int seaLevel = this.getSeaLevel();
        
        switch(this.levelType) {
            case ISLAND:
                return type == HeightmapChunk.Type.OCEAN ? seaLevel - 1 : seaLevel - 10;
            case INLAND:
                return seaLevel;
            case FLOATING:
                return 0;
        }
        
        return seaLevel;
    }

    private void raiseLevel() {
        this.setPhase("Raising");
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            double normalizedX = Math.abs((x / (this.levelWidth - 1.0) - 0.5) * 2.0);
            
            for (int z = 0; z < this.levelLength; ++z) {
                double normalizedZ = Math.abs((z / (this.levelLength - 1.0) - 0.5) * 2.0);
                
                double heightLow = this.lowOctaveNoise.sample(x * 1.3f, z * 1.3f) / 6.0 - 4.0;
                double heightHigh = this.highOctaveNoise.sample(x * 1.3f, z * 1.3f) / 5.0 + 10.0 - 4.0;
                double heightSelector = this.selectorOctaveNoise.sampleXY(x, z) / 8.0;
                
                if (heightSelector > 0.0) {
                    heightHigh = heightLow;
                }
                
                double height = Math.max(heightLow, heightHigh) / 2.0;
                
                if (this.levelType == IndevType.ISLAND) {
                    double islandRadius = Math.sqrt(normalizedX * normalizedX + normalizedZ * normalizedZ) * 1.2;
                    islandRadius = Math.min(islandRadius, islandOctaveNoise.sampleXY(x * 0.05f, z * 0.05f) / 4.0 + 1.0);
                    islandRadius = Math.max(islandRadius, Math.max(normalizedX, normalizedZ));
                    
                    if (islandRadius > 1.0) {
                        islandRadius = 1.0;
                    } else if (islandRadius < 0.0) {
                        islandRadius = 0.0;
                    }
                    
                    islandRadius *= islandRadius;
                    height = height * (1.0 - islandRadius) - islandRadius * 10.0 + 5.0;
                    
                    if (height < 0.0) {
                        height -= height * height * 0.20000000298023224;
                    }
                            
                            
                } else if (height < 0.0) {
                    height *= 0.8;
                }
                
                this.levelHeightmap[x + z * this.levelWidth] = (int)height;
            }
        }
    }
    
    private void erodeLevel() {
        this.setPhase("Eroding");
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                double erodeSelector = this.erodeSelectorOctaveNoise.sample(x << 1, z << 1) / 8.0;
                int erodeNoise = this.erodeOctaveNoise.sample(x << 1, z << 1) > 0.0 ? 1 : 0;
            
                if (erodeSelector > 2.0) {
                    int height = this.levelHeightmap[x + z * this.levelWidth];
                    height = ((height - erodeNoise) / 2 << 1) + erodeNoise;
                    
                    this.levelHeightmap[x + z * this.levelWidth] = height;
                }
            }
        }
    }
    
    private void soilLevel() {
        this.setPhase("Soiling");
        int seaLevel = this.waterLevel;
        MutableBlockPos blockPos = new MutableBlockPos();
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            double normalizedX = Math.abs((x / (this.levelWidth - 1.0) - 0.5) * 2.0);
            int worldX = x - this.levelWidth / 2;
            
            for (int z = 0; z < this.levelLength; ++z) {
                double normalizedZ = Math.max(normalizedX, Math.abs(z / (this.levelLength - 1.0) - 0.5) * 2.0);
                int worldZ = z - this.levelLength / 2;
                
                normalizedZ = normalizedZ * normalizedZ * normalizedZ;

                int dirtDepth = (int)(this.soilOctaveNoise.sampleXY(x, z) / 24.0) - 4;
                int dirtThreshold = this.levelHeightmap[x + z * this.levelWidth] + seaLevel;
         
                int stoneThreshold = dirtDepth + dirtThreshold;
                this.levelHeightmap[x + z * this.levelWidth] = Math.max(dirtThreshold, stoneThreshold);
             
                if (this.levelHeightmap[x + z * this.levelWidth] > this.levelHeight - 2) {
                    this.levelHeightmap[x + z * this.levelWidth] = this.levelHeight - 2;
                }
             
                if (this.levelHeightmap[x + z * this.levelWidth] <= 0) {
                    this.levelHeightmap[x + z * this.levelWidth] = 1;
                }
             
                double floatingNoise = floatingOctaveNoise.sampleXY(x * 2.3, z * 2.3) / 24.0;
             
                // Rounds out the bottom of terrain to form floating islands
                int roundedHeight = (int)(Math.sqrt(Math.abs(floatingNoise)) * Math.signum(floatingNoise) * 20.0) + seaLevel;
                roundedHeight = (int)(roundedHeight * (1.0 - normalizedZ) + normalizedZ * this.levelHeight);
             
                if (roundedHeight > seaLevel) {
                    roundedHeight = this.levelHeight;
                }
                
                blockPos.setPos(worldX, 0, worldZ);
                for (int y = 0; y < this.levelHeight; ++y) {
                    Block block = Blocks.AIR;
                     
                    if (y <= dirtThreshold)
                        block = Blocks.DIRT;
                     
                    if (y <= stoneThreshold)
                        block = Blocks.STONE;
                     
                    if (this.levelType == IndevType.FLOATING && y < roundedHeight)
                        block = Blocks.AIR;

                    Block existingBlock = this.getLevelBlock(x, y, z);
                     
                    if (existingBlock.equals(Blocks.AIR)) {
                        this.setLevelBlock(x, y, z, block);
                    }
                }
            }
        }
    }
    
    private void growLevel() {
        this.setPhase("Growing");
        int surfaceLevel = this.waterLevel - 1;
        
        if (this.levelTheme == IndevTheme.PARADISE)
            surfaceLevel += 2;
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                boolean genSand = sandOctaveNoise.sampleXY(x, z) > 8.0;
                boolean genGravel = gravelOctaveNoise.sampleXY(x, z) > 12.0;
                
                if (this.levelType == IndevType.ISLAND) {
                    genSand = sandOctaveNoise.sampleXY(x, z) > -8.0;
                }
                
                if (this.levelTheme == IndevTheme.PARADISE) {
                    genSand = sandOctaveNoise.sampleXY(x, z) > -32.0;
                }
                
                if (this.levelTheme == IndevTheme.WOODS) {
                    genSand = sandOctaveNoise.sampleXY(x, z) > -8.0;
                }

                int height = levelHeightmap[x + z * this.levelWidth];
                Block blockUp = this.getLevelBlock(x, height + 1, z);
                
                if ((blockUp == this.defaultFluid.getBlock() || blockUp == Blocks.AIR) && height <= this.waterLevel - 1 && genGravel) {
                    this.setLevelBlock(x, height, z, Blocks.GRAVEL);
                }
     
                if (blockUp == Blocks.AIR) {
                    Block surfaceBlock = null;
                    
                    if (height <= surfaceLevel && genSand) {
                        surfaceBlock = Blocks.SAND;
                    }
                    
                    if (this.getLevelBlock(x, height, z) != Blocks.AIR && surfaceBlock != null) {
                        this.setLevelBlock(x, height, z, surfaceBlock);
                    }
                }
            }
        }
    }
    
    private void carveLevel() {
        this.setPhase("Carving");
        
        int caveCount = this.levelWidth * this.levelLength * this.levelHeight / 256 / 64 << 1;
        
        for (int i = 0; i < caveCount; ++i) {
            this.setPhaseProgress(i / (float)(caveCount - 1));
            float caveX = this.random.nextFloat() * (float)this.levelWidth;
            float caveY = this.random.nextFloat() * (float)this.levelHeight;
            float caveZ = this.random.nextFloat() * (float)this.levelLength;
    
            int caveLen = (int)((this.random.nextFloat() + this.random.nextFloat()) * 200f);
            
            float theta = this.random.nextFloat() * (float)Math.PI * 2.0f;
            float deltaTheta = 0.0f;
            float phi = this.random.nextFloat() * (float)Math.PI * 2.0f;
            float deltaPhi = 0.0f;
            
            float caveRadius = this.random.nextFloat() * this.random.nextFloat();
            
            for (int len = 0; len < caveLen; ++len) {
                caveX += MathHelper.sin(theta) * MathHelper.cos(phi);
                caveZ += MathHelper.cos(theta) * MathHelper.cos(phi);
                caveY += MathHelper.sin(phi);
                
                theta += deltaTheta * 0.2f;
                deltaTheta *= 0.9f;
                deltaTheta += this.random.nextFloat() - this.random.nextFloat();
                phi += deltaPhi * 0.5f;
                phi *= 0.5f;
                deltaPhi *= 0.75f;
                deltaPhi += this.random.nextFloat() - this.random.nextFloat();
                
                if (this.random.nextFloat() >= 0.25f) {
                    float centerX = caveX + (this.random.nextFloat() * 4.0f - 2.0f) * 0.2f;
                    float centerY = caveY + (this.random.nextFloat() * 4.0f - 2.0f) * 0.2f;
                    float centerZ = caveZ + (this.random.nextFloat() * 4.0f - 2.0f) * 0.2f;
                    
                    float radius = ((float)this.levelHeight - centerY) / (float)this.levelHeight;
                    radius = 1.2f + (radius * 3.5f + 1.0f) * caveRadius;
                    radius = radius * MathHelper.sin((float)len * (float)Math.PI / (float)caveLen);
                    
                    this.fillOblateSpheroid(centerX, centerY, centerZ, radius, Blocks.AIR);
                }
            }
        }
    }
    
    private void oreLevel() {
        // Given 256x256x64 level:
        // 1000 coal count = 2560 attempts < y51
        // 800 iron count = 2048 attempts < y38
        // 500 gold count = 1280 attempts < y25
        // 800 diamond count = 2048 attempts < y12
        
        this.generateDummyOre(Blocks.COAL_ORE, 1000, 10, (this.levelHeight << 2) / 5);
        this.generateDummyOre(Blocks.IRON_ORE, 800, 8, this.levelHeight * 3 / 5);
        this.generateDummyOre(Blocks.GOLD_ORE, 500, 6, (this.levelHeight << 1) / 5);
        this.generateDummyOre(Blocks.DIAMOND_ORE, 800, 2, this.levelHeight / 5);
    }

    private void meltLevel() {
        this.setPhase("Melting");
        long totalFlooded = 0;
        
        int lavaSourceCount = this.levelWidth * this.levelLength * this.levelHeight / 2000;
        for (int i = 0; i < lavaSourceCount; ++i) {
            if (i % 100 == 0) {
                this.setPhaseProgress(i / (float)(lavaSourceCount - 1));
            }
            
            int randX = this.random.nextInt(this.levelWidth);
            int randY = Math.min(
                Math.min(this.random.nextInt(this.groundLevel), this.random.nextInt(this.groundLevel)),
                Math.min(this.random.nextInt(this.groundLevel), this.random.nextInt(this.groundLevel))
            );
            int randZ = this.random.nextInt(this.levelLength);

            Vec3d[] floodedPositions = new Vec3d[MAX_FLOODS];
            int numFlooded = this.flood(randX, randY, randZ, PLACEHOLDER_BLOCK, Blocks.AIR, floodedPositions);
            
            boolean contained = numFlooded > 0 && numFlooded < MAX_FLOODS;
            if (contained) {
                totalFlooded += numFlooded;
            }

            int ndx = 0;
            while (ndx < MAX_FLOODS && floodedPositions[ndx] != null) {
                Vec3d pos = floodedPositions[ndx++];
                
                this.setLevelBlock((int)pos.x, (int)pos.y, (int)pos.z, contained ? Blocks.LAVA : Blocks.AIR);
            }
        }

        ModernBeta.log(Level.DEBUG, String.format("Flood filled %d tiles", totalFlooded));
    }
    
    private void updateLevel() {
        if (this.levelType == IndevType.FLOATING) {
            this.groundLevel = -128;
            this.waterLevel = this.groundLevel + 1;
        } else if (this.levelType != IndevType.ISLAND) {
            this.groundLevel = this.waterLevel + 1;
            this.waterLevel = this.groundLevel - 16;
        } else {
            this.groundLevel = this.waterLevel - 9;
        }
    }

    private void waterLevel() {
        this.setPhase("Watering");
        long totalFlooded = 0;
        
        Block fluidBlock = this.defaultFluid.getBlock();
    
        int waterSourceCount = this.levelWidth * this.levelLength * this.levelHeight / 1000;
        for (int i = 0; i < waterSourceCount; ++i) {
            if (i % 100 == 0) {
                this.setPhaseProgress(i / (float)(waterSourceCount - 1));
            }
            
            int randX = this.random.nextInt(this.levelWidth);
            int randY = this.random.nextInt(this.levelHeight);
            int randZ = this.random.nextInt(this.levelLength);

            Vec3d[] floodedPositions = new Vec3d[MAX_FLOODS];
            int numFlooded = this.flood(randX, randY, randZ, PLACEHOLDER_BLOCK, Blocks.AIR, floodedPositions);
            
            boolean contained = numFlooded > 0 && numFlooded < MAX_FLOODS;
            if (contained) {
                totalFlooded += numFlooded;
            }

            int ndx = 0;
            while (ndx < MAX_FLOODS && floodedPositions[ndx] != null) {
                Vec3d pos = floodedPositions[ndx++];
                
                this.setLevelBlock((int)pos.x, (int)pos.y, (int)pos.z, contained ? fluidBlock : Blocks.AIR);
            }
        }
        
        ModernBeta.log(Level.DEBUG, String.format("Flood filled %d tiles", totalFlooded));
        
        if (this.levelType != IndevType.FLOATING) {
            for (int x = 0; x < this.levelWidth; ++x) {
                totalFlooded += this.flood(x, this.waterLevel - 1, 0, fluidBlock, Blocks.AIR);
                totalFlooded += this.flood(x, this.waterLevel - 1, this.levelLength - 1, fluidBlock, Blocks.AIR);
            }
            
            for (int z = 0; z < this.levelLength; ++z) {
                totalFlooded += this.flood(this.levelWidth - 1, this.waterLevel - 1, z, fluidBlock, Blocks.AIR);
                totalFlooded += this.flood(0, this.waterLevel - 1, z, fluidBlock, Blocks.AIR);
            }
        }
        
        ModernBeta.log(Level.DEBUG, String.format("Flood filled %d tiles, including edges", totalFlooded));
    }

    private void plantLevel() {
        this.setPhase("Planting");
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                for (int y = 0; y < this.levelHeight - 2; ++y) {
                    Block block = this.getLevelBlock(x, y, z);
                    Block blockUp = this.getLevelBlock(x, y + 1, z);

                    if (block.equals(Blocks.DIRT) && blockUp.equals(Blocks.AIR)) {
                        this.setLevelBlock(x, y, z, Blocks.GRASS);
                    }
                }
            }
        }
    }
    
    /*
     * This method handles bottom layer bedrock/level generation,
     * and outer wall layer for the level.
     * 
     */
    private void assembleLevel() {
        this.setPhase("Assembling");
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                for (int y = 0; y < this.levelHeight; ++y) {
                    Block block = Blocks.AIR;
                    
                    if (y <= 1 && y < this.groundLevel - 1 && this.getLevelBlock(x, y + 1, z) == Blocks.AIR) {
                        block = Blocks.LAVA;
                        
                    } else if (y < this.groundLevel - 1) {
                        block = Blocks.BEDROCK;
                        
                    } else if (y < this.groundLevel) { // Handles Inland levels
                        if (this.groundLevel > this.waterLevel && this.defaultFluid.getBlock() == Blocks.WATER) {
                            block = Blocks.GRASS;
                            
                        } else { // Handles Island levels
                            block = Blocks.DIRT;
                            
                        }
                    } else if (y < this.waterLevel) { // Handles Island levels
                        block = this.defaultFluid.getBlock();
                    }
                    
                    this.setLevelBlock(x, y, z, block);
                    
                    if (y == 1 && x != 0 && z != 0 && x != this.levelWidth - 1 && z != this.levelLength - 1) {
                        y = this.levelHeight - 2;
                    }
                }
            }
        }
    }
    
    private void generateDummyOre(Block block, int count, int size, int height) {
        int attempts = this.levelWidth * this.levelLength * this.levelHeight / 256 / 64 * count / 100;
        for (int i = 0; i < attempts; ++i) {
            this.random.nextFloat();
            float randY = this.random.nextFloat() * this.levelHeight;
            this.random.nextFloat();
            
            if (randY < height) {
                int randSize = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0f * size / 100.0f);
                this.random.nextFloat();
                this.random.nextFloat();
                
                for (int j = 0; j < randSize; ++j) {
                    this.random.nextFloat();
                    this.random.nextFloat();
                    this.random.nextFloat();
                    this.random.nextFloat();
                }
            }
        }
    }

    private void generateWorldBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int groundLevel = this.getSeaLevel();
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = this.biomeProvider.getBiomeSource().getBiome(startX + x, startZ + z);
                
                for (int y = 0; y < this.levelHeight; ++y) {
                    if (y < groundLevel) {
                        chunkPrimer.setBlockState(x, y, z, BlockStates.BEDROCK);
                    } else if (y == groundLevel) {
                        chunkPrimer.setBlockState(x, y, z, biome.topBlock);
                    }
                }
            }
        }
    }
    
    private void generateWaterBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int seaLevel = this.getSeaLevel();
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = this.biomeProvider.getBiomeSource().getBiome(startX + x, startZ + z);
                
                for (int y = 0; y < this.levelHeight; ++y) {
                    if (y < seaLevel - 10) {
                        chunkPrimer.setBlockState(x, y, z, BlockStates.BEDROCK);
                    } else if (y == seaLevel - 10) {
                        chunkPrimer.setBlockState(x, y, z, biome.fillerBlock);
                    } else if (y < seaLevel) {
                        chunkPrimer.setBlockState(x, y, z, this.defaultFluid);
                    }
                }
            }
        }
    }
}
