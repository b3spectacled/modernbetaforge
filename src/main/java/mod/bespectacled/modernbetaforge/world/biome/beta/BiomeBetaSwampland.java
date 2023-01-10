package mod.bespectacled.modernbetaforge.world.biome.beta;

import java.util.Random;

import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeColors;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGeneratorSettings;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFossils;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeBetaSwampland extends BiomeBeta {
    public BiomeBetaSwampland() {
        super(new BiomeProperties("Beta Swampland")
            .setTemperature(0.5F)
            .setRainfall(1.0F)
        );
        
        this.topBlock = BlockStates.GRASS_BLOCK;
        this.fillerBlock = BlockStates.DIRT;
        
        if (ModernBetaConfig.mobOptions.useNewMobs) {
            this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 1, 1, 1));
        }

        this.skyColor = ModernBetaBiomeColors.BETA_COOL_SKY_COLOR;
    }
    
    @Override
    public EnumFlowerType pickRandomFlower(Random random, BlockPos blockPos) {
        return EnumFlowerType.BLUE_ORCHID;
    }
    
    @Override
    public void decorate(World world, Random random, BlockPos startPos) {
        super.decorate(world, random, startPos);
        
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();
        ChunkPos chunkPos = new ChunkPos(startPos);
        WorldGenerator waterLilyGen = new WorldGenWaterlily();
        
        if (settings.useFossils && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FOSSIL) && random.nextInt(64) == 0) {
            new WorldGenFossils().generate(world, random, startPos);
        }
        
        if (settings.useLilyPads && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.LILYPAD)) {
            for (int i = 0; i < 4; ++i) {
                int dX = random.nextInt(16) + 8;
                int dZ = random.nextInt(16) + 8;
                
                int height = world.getHeight(startPos.add(dX, 0, dZ)).getY() * 2;
                if (height > 0) {
                    int dY = random.nextInt(height);
                    BlockPos blockPos;
                    BlockPos blockPosDown;
                    
                    for (blockPos = startPos.add(dX, dY, dZ); blockPos.getY() > 0; blockPos = blockPosDown) {
                        blockPosDown = blockPos.down();
                        if (!world.isAirBlock(blockPosDown)) {
                            break;
                        }
                    }
                    
                    waterLilyGen.generate(world, random, blockPos);
                }
            }
        }

        if (settings.useNewFlowers && TerrainGen.decorate(world, random, chunkPos, DecorateBiomeEvent.Decorate.EventType.FLOWERS)) {
            for (int i = 0; i < 1; ++i) {
                int dX = random.nextInt(16) + 8;
                int dZ = random.nextInt(16) + 8;
                
                int height = world.getHeight(startPos.add(dX, 0, dZ)).getY() + 32;
                if (height > 0) {
                    int dY = random.nextInt(height);
                    BlockPos blockPos = startPos.add(dX, dY, dZ);
                    
                    EnumFlowerType enumFlowerType = this.pickRandomFlower(random, blockPos);
                    BlockFlower blockFlower = enumFlowerType.getBlockType().getBlock();
                    
                    if (blockFlower.getDefaultState().getMaterial() != Material.AIR) {
                        this.decorator.flowerGen.setGeneratedBlock(blockFlower, enumFlowerType);
                        this.decorator.flowerGen.generate(world, random, blockPos);
                    }
                }
            }
        }
    }
}
