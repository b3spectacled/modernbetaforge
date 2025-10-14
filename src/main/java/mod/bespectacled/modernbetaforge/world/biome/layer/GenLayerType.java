package mod.bespectacled.modernbetaforge.world.biome.layer;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public enum GenLayerType {
    VANILLA("vanilla"),
    CONTINENTAL("continental"),
    ISLANDS("islands"),
    SMALL_ISLANDS("small_islands"),
    PANGAEA("pangaea");
    
    public final String id;
    
    private GenLayerType(String id) {
        this.id = id;
    }
    
    public static GenLayerType fromId(String id) {
        for (GenLayerType type : GenLayerType.values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No GenLayerType matching id: " + id);
    }

    public static GenLayerType fromIdOrElse(String id, GenLayerType alternate) {
        GenLayerType type;
        
        try {
            type = fromId(id);
        } catch (IllegalArgumentException e) {
            ModernBeta.log(Level.WARN, String.format("Did not find GenLayerType id '%s', returning alternate.", id));
            type = alternate;
        }
        
        return type;
    }
}
