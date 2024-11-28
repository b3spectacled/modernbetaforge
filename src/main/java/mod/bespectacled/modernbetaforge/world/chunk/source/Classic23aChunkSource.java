package mod.bespectacled.modernbetaforge.world.chunk.source;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoiseCombined;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class Classic23aChunkSource extends FiniteChunkSource {
    private final PerlinOctaveNoiseCombined lowOctaveNoise;
    private final PerlinOctaveNoiseCombined highOctaveNoise;
    private final PerlinOctaveNoise selectorOctaveNoise;
    private final PerlinOctaveNoiseCombined erodeSelectorOctaveNoise;
    private final PerlinOctaveNoiseCombined erodeOctaveNoise;
    private final PerlinOctaveNoise soilOctaveNoise;
    
    private PerlinOctaveNoise sandOctaveNoise;
    private PerlinOctaveNoise gravelOctaveNoise;
    
    private final int seaLevel;

    public Classic23aChunkSource(
        World world,
        ModernBetaChunkGenerator chunkGenerator,
        ModernBetaChunkGeneratorSettings chunkGeneratorSettings,
        ModernBetaNoiseSettings noiseSettings,
        long seed,
        boolean mapFeaturesEnabled
    ) {
        super(world, chunkGenerator, chunkGeneratorSettings, noiseSettings, seed, mapFeaturesEnabled);
        
        this.lowOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
        this.highOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
        this.selectorOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        
        this.erodeSelectorOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
        this.erodeOctaveNoise = new PerlinOctaveNoiseCombined(this.random, 8, false);
        
        this.soilOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        
        this.seaLevel = this.levelHeight / 2;
        
        this.setCloudHeight(this.seaLevel + 34);
    }
    
    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    protected void pregenerateTerrain() {
        this.raiseLevel();
        this.erodeLevel();
        this.soilLevel();
        this.carveLevel();
        this.oreLevel();
        this.waterLevel();
        this.meltLevel();
        this.growLevel();
    }

    @Override
    protected void generateBorder(ChunkPrimer chunkPrimer, int chunkX, int chunkZ) {
        int seaLevel = this.getSeaLevel();
        
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 0; y < this.levelHeight; ++y) {
                    if (y < seaLevel - 2) {
                        chunkPrimer.setBlockState(x, y, z, BlockStates.BEDROCK);
                    } else if (y < seaLevel) {
                        chunkPrimer.setBlockState(x, y, z, this.defaultFluid);
                    }
                }
            }
        }
    }

    @Override
    protected int getBorderHeight(int x, int z, Type type) {
        return type == HeightmapChunk.Type.OCEAN ? seaLevel - 1 : seaLevel - 3;
    }
    
    @Override
    protected void fillOblateSpheroid(float centerX, float centerY, float centerZ, float radius, Block fillBlock) {
        for (int x = (int)(centerX - radius); x <= (int)(centerX + radius); ++x) {
            for (int y = (int)(centerY - radius); y <= (int)(centerY + radius); ++y) {
                for (int z = (int)(centerZ - radius); z <= (int)(centerZ + radius); ++z) {
                
                    float dx = (float)x - centerX;
                    float dy = (float)y - centerY;
                    float dz = (float)z - centerZ;
                    
                    if ((dx * dx + dy * dy * 2.0f + dz * dz) < radius * radius && this.inCaveBounds(x, y, z)) {
                        Block block = this.getLevelBlock(x, y, z);
                        
                        if (block == this.defaultBlock.getBlock()) {
                            this.setLevelBlock(x, y, z, fillBlock);
                        }
                    }
                }
            }
        }
    }
    
    private void raiseLevel() {
        this.logPhase("Raising");
        
        for (int x = 0; x < this.levelWidth; ++x) {
            for (int z = 0; z < this.levelLength; ++z) {
                double heightLow = this.lowOctaveNoise.sample(x * 1.3f, z * 1.3f) / 8.0 - 8.0;
                double heightHigh = this.highOctaveNoise.sample(x * 1.3f, z * 1.3f) / 6.0 + 6.0;
                double heightSelector = this.selectorOctaveNoise.sampleXY(x, z) / 8.0;
                
                if (heightSelector > 0.0) {
                    heightHigh = heightLow;
                }
                
                double height = Math.max(heightLow, heightHigh) / 2.0;
                
                if (height < 0.0) {
                    height *= 0.8;
                }
                
                this.levelHeightmap[x + z * this.levelWidth] = (int)height;
            }
        }
    }
    
    private void erodeLevel() {
        this.logPhase("Eroding");
        
        for (int x = 0; x < this.levelWidth; ++x) {
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
        this.logPhase("Soiling");
        int seaLevel = this.getSeaLevel();
        MutableBlockPos blockPos = new MutableBlockPos();
        
        for (int x = 0; x < this.levelWidth; ++x) {
            int worldX = x - this.levelWidth / 2;
            
            for (int z = 0; z < this.levelLength; ++z) {
                int worldZ = z - this.levelLength / 2;
                Biome biome = this.biomeProvider.getBiomeSource().getBiome(worldX, worldZ);
                
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
                
                blockPos.setPos(worldX, 0, worldZ);
                for (int y = 0; y < this.levelHeight; ++y) {
                    Block block = Blocks.AIR;
                     
                    if (y <= dirtThreshold)
                        block = biome.fillerBlock.getBlock();
                     
                    if (y <= stoneThreshold)
                        block = Blocks.STONE;

                    this.setLevelBlock(x, y, z, block);
                }
            }
        }
    }
    
    private void carveLevel() {
        this.logPhase("Carving");
        
        int caveCount = this.levelWidth * this.levelLength * this.levelHeight / 256 / 64;
        
        for (int i = 0; i < caveCount; ++i) {
            float caveX = this.random.nextFloat() * (float)this.levelWidth;
            float caveY = this.random.nextFloat() * (float)this.levelHeight;
            float caveZ = this.random.nextFloat() * (float)this.levelLength;
    
            int caveLen = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0f);
            
            float theta = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float deltaTheta = 0.0f;
            float phi = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float deltaPhi = 0.0f;
            
            for (int len = 0; len < caveLen; ++len) {
                caveX += (float)(Math.sin((double)theta) * Math.cos((double)phi));
                caveZ += (float)(Math.cos((double)theta) * Math.cos((double)phi));
                caveY += (float)(Math.sin((double)phi));
                
                theta += deltaTheta * 0.2f;
                deltaTheta *= 0.9f;
                deltaTheta += this.random.nextFloat() - this.random.nextFloat();
                phi += deltaPhi * 0.5f;
                phi *= 0.5f;
                deltaPhi *= 0.9f;
                deltaPhi += this.random.nextFloat() - this.random.nextFloat();
                
                if (this.random.nextFloat() >= 0.3f) {
                    float centerX = caveX + this.random.nextFloat() * 4.0f - 2.0f;
                    float centerY = caveY + this.random.nextFloat() * 4.0f - 2.0f;
                    float centerZ = caveZ + this.random.nextFloat() * 4.0f - 2.0f;
                    
                    float radius = (float)(Math.sin((double)len * Math.PI / (double)caveLen) * 2.5 + 1.0);
                    
                    this.fillOblateSpheroid(centerX, centerY, centerZ, radius, Blocks.AIR);
                }
            }
        }
    }
    
    private void oreLevel() {
        // Given 256x256x64 level:
        // 90 coal count = 230 attempts
        // 70 iron count = 179 attempts
        // 50 gold count = 128 attempts
        
        this.generateDummyOre(Blocks.COAL_ORE, 90);
        this.generateDummyOre(Blocks.IRON_ORE, 70);
        this.generateDummyOre(Blocks.GOLD_ORE, 50);
    }

    private void waterLevel() {
        this.logPhase("Watering");

        Block fluidBlock = this.defaultFluid.getBlock();
        int seaLevel = this.getSeaLevel();
        int flooded = 0;
        
        for (int x = 0; x < this.levelWidth; ++x) {
            flooded += this.flood(x, seaLevel - 1, 0, fluidBlock, Blocks.AIR);
            flooded += this.flood(x, seaLevel - 1, this.levelLength - 1, fluidBlock, Blocks.AIR);
        }
        
        for (int z = 0; z < this.levelLength; ++z) {
            flooded += this.flood(this.levelWidth - 1, seaLevel - 1, z, fluidBlock, Blocks.AIR);
            flooded += this.flood(0, seaLevel - 1, z, fluidBlock, Blocks.AIR);
        }
        
        int waterSourceCount = this.levelWidth * this.levelLength / 200;
        for (int i = 0; i < waterSourceCount; ++i) {
            int randX = this.random.nextInt(this.levelWidth);
            int randY = seaLevel - 1 - this.random.nextInt(3);
            int randZ = this.random.nextInt(this.levelLength);
            
            if (this.getLevelBlock(randX, randY, randZ) == Blocks.AIR) {
                flooded += this.flood(randX, randY, randZ, fluidBlock, Blocks.AIR);
            }
        }
        
        ModernBeta.log(Level.DEBUG, String.format("Flood filled %d tiles", flooded));
    }
    
    private void meltLevel() {
        this.logPhase("Melting");
        
        int attempts = 0;
        
        int lavaSourceCount = this.levelWidth * this.levelLength * this.levelHeight / 10000;
        for (int i = 0; i < lavaSourceCount; ++i) {
            int randX = this.random.nextInt(this.levelWidth);
            int randY = this.random.nextInt(this.getSeaLevel() - 4);
            int randZ = this.random.nextInt(this.levelLength);
            
            if (this.getLevelBlock(randX, randY, randZ) == Blocks.AIR) {
                this.flood(randX, randY, randZ, Blocks.LAVA, Blocks.AIR);
                
                attempts++;
            }
        }
        
        ModernBeta.log(Level.DEBUG, String.format("LavaCount: %d", attempts));
    }
    
    private void growLevel() {
        this.logPhase("Growing");

        this.sandOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        this.gravelOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        int seaLevel = this.getSeaLevel();
        
        for (int x = 0; x < this.levelWidth; ++x) {
            int worldX = x - this.levelWidth / 2;
            
            for (int z = 0; z < this.levelLength; ++z) {
                int worldZ = z - this.levelLength / 2;
                Biome biome = this.biomeProvider.getBiomeSource().getBiome(worldX, worldZ);
                
                boolean genSand = sandOctaveNoise.sampleXY(x, z) > 8.0;
                boolean genGravel = gravelOctaveNoise.sampleXY(x, z) > 12.0;

                int height = levelHeightmap[x + z * this.levelWidth];
                Block blockUp = this.getLevelBlock(x, height + 1, z);
                
                if ((blockUp == this.defaultFluid.getBlock()) && height <= seaLevel - 1 && genGravel) {
                    this.setLevelBlock(x, height, z, Blocks.GRAVEL);
                }
     
                if (blockUp == Blocks.AIR) {
                    Block surfaceBlock = biome.topBlock.getBlock();
                    
                    if (height <= seaLevel - 1 && genSand) {
                        surfaceBlock = Blocks.SAND;
                    }
                    
                    this.setLevelBlock(x, height, z, surfaceBlock);
                }
            }
        }
    }
    
    private void generateDummyOre(Block block, int count) {
        int attempts = this.levelWidth * this.levelLength * this.levelHeight / 256 / 64 * count / 100;
        for (int i = 0; i < attempts; ++i) {
            this.random.nextFloat();
            this.random.nextFloat();
            this.random.nextFloat();
            
            int randSize = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0f * count / 100.0f);
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
    
    private boolean inCaveBounds(int x, int y, int z) {
        return x >= 1 && x < this.levelWidth - 1 && y >= 1 && y < this.levelHeight - 1 && z >= 1 && z < this.levelLength - 1;
    }
}
