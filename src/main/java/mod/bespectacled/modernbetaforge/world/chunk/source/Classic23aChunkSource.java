package mod.bespectacled.modernbetaforge.world.chunk.source;

import java.util.Random;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk.Type;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoiseCombined;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
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

    public Classic23aChunkSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
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
        if (this.haltGeneration) return;
        this.setPhase("Raising");
        this.raiseLevel();

        if (this.haltGeneration) return;
        this.setPhase("Eroding");
        this.erodeLevel();

        if (this.haltGeneration) return;
        this.setPhase("Soiling");
        this.soilLevel();
        
        if (this.settings.useIndevCaves) {
            if (this.haltGeneration) return;
            this.setPhase("Carving");
            this.carveLevel();
        }
        
        this.oreLevel();

        if (this.haltGeneration) return;
        this.setPhase("Watering");
        this.waterLevel();

        if (this.haltGeneration) return;
        this.setPhase("Melting");
        this.meltLevel();

        if (this.haltGeneration) return;
        this.setPhase("Growing");
        this.growLevel();

        if (this.haltGeneration) return;
        this.setPhase("Assembling");
        this.assembleLevel();
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
    
    private void raiseLevel() {
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                double heightLow = this.lowOctaveNoise.sample(x * 1.3f, z * 1.3f) / 8.0 - 8.0;
                double heightHigh = this.highOctaveNoise.sample(x * 1.3f, z * 1.3f) / 6.0 + 6.0;
                double heightSelector = this.selectorOctaveNoise.sample(x, z) / 8.0;
                
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
        int seaLevel = this.getSeaLevel();
        MutableBlockPos blockPos = new MutableBlockPos();
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            int worldX = x - this.levelWidth / 2;
            
            for (int z = 0; z < this.levelLength; ++z) {
                int worldZ = z - this.levelLength / 2;
                
                int dirtDepth = (int)(this.soilOctaveNoise.sample(x, z) / 24.0) - 4;
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
                        block = Blocks.DIRT;
                     
                    if (y <= stoneThreshold)
                        block = this.defaultBlock.getBlock();

                    this.setLevelBlock(x, y, z, block);
                }
            }
        }
    }
    
    private void carveLevel() {
        int caveCount = this.levelWidth * this.levelLength * this.levelHeight / 256 / 64;
        Random tunnelRandom = new Random(this.seed);
        
        for (int i = 0; i < caveCount; ++i) {
            this.setPhaseProgress(i / (float)(caveCount - 1));
            float caveX = this.random.nextFloat() * (float)this.levelWidth;
            float caveY = this.random.nextFloat() * (float)this.levelHeight;
            float caveZ = this.random.nextFloat() * (float)this.levelLength;
    
            int caveLen = (int)((this.random.nextFloat() + this.random.nextFloat()) * 75.0f);
            
            float theta = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float deltaTheta = 0.0f;
            float phi = (float)((double)this.random.nextFloat() * Math.PI * 2.0);
            float deltaPhi = 0.0f;
            
            float caveWidth = MathUtil.getRandomFloatInRange(ModernBetaGeneratorSettings.MIN_LEVEL_CAVE_WIDTH, this.levelCaveWidth, tunnelRandom);
            
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
                    
                    float radius = (float)((Math.sin((double)len * Math.PI / (double)caveLen) * 2.5 + 1.0) * caveWidth);
                    
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
            if (i % 100 == 0) {
                this.setPhaseProgress(i / (float)(waterSourceCount - 1));
            }
            
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
        int attempts = 0;
        
        int lavaSourceCount = this.levelWidth * this.levelLength * this.levelHeight / 10000;
        for (int i = 0; i < lavaSourceCount; ++i) {
            if (i % 100 == 0) {
                this.setPhaseProgress(i / (float)(lavaSourceCount - 1));
            }
            
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
        this.sandOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        this.gravelOctaveNoise = new PerlinOctaveNoise(this.random, 8, false);
        int seaLevel = this.getSeaLevel();
        
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                boolean genSand = sandOctaveNoise.sample(x, z) > 8.0;
                boolean genGravel = gravelOctaveNoise.sample(x, z) > 12.0;

                int height = levelHeightmap[x + z * this.levelWidth];
                Block blockUp = this.getLevelBlock(x, height + 1, z);
                
                if ((blockUp == this.defaultFluid.getBlock()) && height <= seaLevel - 1 && genGravel) {
                    this.setLevelBlock(x, height, z, Blocks.GRAVEL);
                }
     
                if (blockUp == Blocks.AIR) {
                    Block surfaceBlock = Blocks.GRASS;
                    
                    if (height <= seaLevel - 1 && genSand) {
                        surfaceBlock = Blocks.SAND;
                    }
                    
                    this.setLevelBlock(x, height, z, surfaceBlock);
                }
            }
        }
    }
    
    /*
     * Not present in original source,
     * but a bedrock layer needs to be added.
     * 
     */
    private void assembleLevel() {
        for (int x = 0; x < this.levelWidth; ++x) {
            this.setPhaseProgress(x / (float)(this.levelWidth - 1));
            
            for (int z = 0; z < this.levelLength; ++z) {
                this.setLevelBlock(x, 0, z, Blocks.BEDROCK);
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
}
