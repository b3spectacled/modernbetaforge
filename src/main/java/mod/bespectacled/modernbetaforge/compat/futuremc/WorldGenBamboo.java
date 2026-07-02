package mod.bespectacled.modernbetaforge.compat.futuremc;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeHolders;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import thedarkcolour.futuremc.block.villagepillage.BlockBamboo;
import thedarkcolour.futuremc.registry.FBlocks;

public class WorldGenBamboo {
    public static final Set<Biome> VALID_BIOMES = new HashSet<>();

    public static void generate(Random random, int chunkX, int chunkZ, World world) {
        int x = (chunkX << 4) + 8;
        int z = (chunkZ << 4) + 8;
        BlockPos pos = new BlockPos(x, 0, z);
        Biome biome = world.getBiomeForCoordsBody(pos);
        ChunkPos chunkPos = world.getChunk(chunkX, chunkZ).getPos();
        
        if (VALID_BIOMES.contains(biome) && world.getWorldType() != WorldType.FLAT) {
            MutableBlockPos mutablePos = new MutableBlockPos();
            
            for (int i = 0; i < 23; ++i) {
                int bX = random.nextInt(16);
                int bZ = random.nextInt(16);
                int bY = random.nextInt(world.getHeight(chunkPos.getBlock(0, 0, 0).add(bX, 0, bZ)).getY() + 32);
                mutablePos.setPos(chunkPos.getBlock(0, 0, 0).add(bX, bY, bZ));
                
                generate(world, random, mutablePos);
            }
        }
    }
    
    private static void generate(World world, Random random, BlockPos pos) {
        BlockBamboo bamboo = FBlocks.BAMBOO;
        Block block = world.getBlockState(pos).getBlock();
        Biome biome = world.getBiome(pos);
        
        if (block.isReplaceable(world, pos) && bamboo.canPlaceBlockAt(world, pos) && VALID_BIOMES.contains(biome)) {
            world.setBlockState(pos, bamboo.getDefaultState());
            
            for (int i = 0; i < 10; ++i) {
                IBlockState blockState = world.getBlockState(pos);
                
                if (blockState.getBlock() == bamboo && bamboo.canGrow(world, pos, blockState, false)) {
                    bamboo.grow(world, random, pos, blockState);
                }
            }
        }
    }
    
    static {
        VALID_BIOMES.add(ModernBetaBiomeHolders.BETA_RAINFOREST);
    }
}
