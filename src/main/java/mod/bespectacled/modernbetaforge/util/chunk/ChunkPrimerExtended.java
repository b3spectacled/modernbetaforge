package mod.bespectacled.modernbetaforge.util.chunk;

import mod.bespectacled.modernbetaforge.util.BlockStates;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

@SuppressWarnings("deprecation")
public class ChunkPrimerExtended extends ChunkPrimer {
    private static final IBlockState DEFAULT_STATE = BlockStates.AIR;
    
    private final int worldHeight;
    private final int worldFloor;
    private final int bitShift;
    private final char[] data;
    
    public ChunkPrimerExtended(int worldHeight, int worldFloor) {
        // Ensure minimum array size
        worldHeight = Math.max(worldHeight, 256);
        
        int worldSizeY = worldHeight - worldFloor;
        int bitShift = 0;
        
        while (worldSizeY > 0) {
            worldSizeY >>= 1;
            bitShift++;
        }
        
        this.worldHeight = worldHeight;
        this.worldFloor = worldFloor;
        this.bitShift = bitShift;
        this.data = new char[16 * 16 * (1 << this.bitShift)];
    }
    
    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        char blockData = this.data[this.getBlockIndex(x, y, z)];
        IBlockState blockState = Block.BLOCK_STATE_IDS.getByValue(blockData);
        
        return blockState == null ? DEFAULT_STATE : blockState;
    }
    
    @Override
    public void setBlockState(int x, int y, int z, IBlockState state) {
        this.data[this.getBlockIndex(x, y, z)] = (char)Block.BLOCK_STATE_IDS.get(state);
    }
    
    @Override
    public int findGroundBlockIdx(int x, int z) {
        for (int y = this.worldHeight; y >= this.worldFloor; --y) {
            IBlockState blockState = this.getBlockState(x, y, z);
            
            if (blockState != null && blockState != DEFAULT_STATE) {
                return y;
            }
        }
        
        return this.worldFloor;
    }
    
    private int getBlockIndex(int x, int y, int z)  {
        int ndx = x << (this.bitShift + 4) | z << (this.bitShift) | (y - this.worldFloor);
        if (ndx < 0) {
            throw new ArrayIndexOutOfBoundsException(String.format("Block index %d out of bounds at (%d, %d, %d)!", ndx, x, y, z));
        }
        
        return ndx;
    }
}
