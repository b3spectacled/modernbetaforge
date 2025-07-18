package mod.bespectacled.modernbetaforge.world.chunk.indev;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;

public enum IndevTheme {
    NORMAL("normal"),
    PARADISE("paradise"),
    WOODS("woods");
    
    public final String id;
    
    private IndevTheme(String id) {
        this.id = id;
    }
    
    public static IndevTheme fromId(String id) {
        for (IndevTheme theme : IndevTheme.values()) {
            if (theme.id.equalsIgnoreCase(id)) {
                return theme;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] No Indev Theme matching id: " + id);
    }
    
    public static IndevTheme fromIdOrElse(String id, IndevTheme alternate) {
        IndevTheme theme;
        
        try {
            theme = fromId(id);
        } catch (IllegalArgumentException e) {
            ModernBeta.log(Level.WARN, String.format("Did not find IndevTheme id '%s', returning alternate.", id));
            theme = alternate;
        }
        
        return theme;
    }
}
