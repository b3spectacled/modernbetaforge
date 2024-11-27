package mod.bespectacled.modernbetaforge.world.chunk.indev;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum IndevHouse {
    NONE("none"),
    OAK("oak", Blocks.PLANKS, Blocks.STONE),
    MOSSY("mossy", Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
    
    public final String id;
    public final Block wallBlock;
    public final Block floorBlock;
    
    private IndevHouse(String id, Block wallBlock, Block floorBlock) {
        this.id = id;
        this.wallBlock = wallBlock;
        this.floorBlock = floorBlock;
    }
    
    private IndevHouse(String id) {
        this(id, null, null);
    }
    
    public static IndevTheme fromId(String id) {
        for (IndevTheme theme : IndevTheme.values()) {
            if (theme.id.equalsIgnoreCase(id)) {
                return theme;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No Indev House matching id: " + id);
    }
}
