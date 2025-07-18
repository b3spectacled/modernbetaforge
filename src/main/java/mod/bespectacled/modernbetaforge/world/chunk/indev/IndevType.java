package mod.bespectacled.modernbetaforge.world.chunk.indev;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public enum IndevType {
    ISLAND("island"),
    FLOATING("floating"),
    INLAND("inland");
    
    public final String id;
    
    private IndevType(String id) {
        this.id = id;
    }
    
    public static IndevType fromId(String id) {
        for (IndevType type : IndevType.values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No Indev Type matching id: " + id);
    }
    
    public static IndevType fromIdOrElse(String id, IndevType alternate) {
        IndevType type;
        
        try {
            type = fromId(id);
        } catch (IllegalArgumentException e) {
            ModernBeta.log(Level.WARN, String.format("Did not find IndevType id '%s', returning alternate.", id));
            type = alternate;
        }
        
        return type;
    }
}
