package mod.bespectacled.modernbetaforge.util.chunk;

/**
 * A simple container for an array to hold height values for an entire chunk (256 blocks).
 * 
 * == Heightmap Reference ==
 * SURFACE: The highest block that blocks motion. Blocked by highest solid surface.
 * OCEAN: The highest non-air block, solid block. Blocked by highest non-solid surface.
 * FLOOR: The highest block non-air block, solid block, if any. Blocked by lowest solid surface.
 * 
 */
public class HeightmapChunk {
    public enum Type {
        SURFACE,
        OCEAN,
        FLOOR,
        STRUCTURE
    }
    
    private final short heightmapSurface[];
    private final short heightmapOcean[];
    private final short heightmapFloor[];
    private final short heightmapStructure[];

    public HeightmapChunk(short[] heightmapSurface, short[] heightmapOcean, short[] heightmapFloor, short[] heightmapStructure) {
        if (heightmapSurface.length != 256 || heightmapOcean.length != 256 || heightmapFloor.length != 256 || heightmapStructure.length != 256) 
            throw new IllegalArgumentException("[Modern Beta] Heightmap is an invalid size!");

        this.heightmapSurface = heightmapSurface;
        this.heightmapOcean = heightmapOcean;
        this.heightmapFloor = heightmapFloor;
        this.heightmapStructure = heightmapStructure;
    }
    
    public int getHeight(int x, int z, HeightmapChunk.Type type) {
        int ndx = (z & 0xF) + (x & 0xF) * 16;
        int height;
        
        switch(type) {
            case SURFACE:
                height = this.heightmapSurface[ndx];
                break;
            case OCEAN:
                height = this.heightmapOcean[ndx];
                break;
            case FLOOR:
                height = this.heightmapFloor[ndx];
                break;
            case STRUCTURE:
                height = this.heightmapStructure[ndx];
                break;
            default:
                height = this.heightmapSurface[ndx];
        }
        
        return height;
    }
}