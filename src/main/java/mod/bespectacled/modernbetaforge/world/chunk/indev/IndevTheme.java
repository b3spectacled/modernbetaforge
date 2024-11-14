package mod.bespectacled.modernbetaforge.world.chunk.indev;

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
}
