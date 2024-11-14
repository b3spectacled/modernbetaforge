package mod.bespectacled.modernbetaforge.world.chunk.indev;

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
}
