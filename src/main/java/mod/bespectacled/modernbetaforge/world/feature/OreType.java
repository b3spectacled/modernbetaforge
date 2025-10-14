package mod.bespectacled.modernbetaforge.world.feature;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public enum OreType {
    DEFAULT("default"),
    CLASSIC("classic"),
    VANILLA("vanilla");
    
    public final String id;
    
    private OreType(String id) {
        this.id = id;
    }
    
    public static OreType fromId(String id) {
        for (OreType type : OreType.values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No Ore Type matching id: " + id);
    }

    public static OreType fromIdOrElse(String id, OreType alternate) {
        OreType type;
        
        try {
            type = fromId(id);
        } catch (IllegalArgumentException e) {
            ModernBeta.log(Level.WARN, String.format("Did not find Ore Type id '%s', returning alternate.", id));
            type = alternate;
        }
        
        return type;
    }
}
