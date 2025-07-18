package mod.bespectacled.modernbetaforge.world.chunk.indev;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
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
    
    public static IndevHouse fromId(String id) {
        for (IndevHouse house : IndevHouse.values()) {
            if (house.id.equalsIgnoreCase(id)) {
                return house;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No Indev House matching id: " + id);
    }
    
    public static IndevHouse fromIdOrElse(String id, IndevHouse alternate) {
        IndevHouse house;
        
        try {
            house = fromId(id);
        } catch (IllegalArgumentException e) {
            ModernBeta.log(Level.WARN, String.format("Did not find IndevHouse id '%s', returning alternate.", id));
            house = alternate;
        }
        
        return house;
    }
}
