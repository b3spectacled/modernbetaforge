package mod.bespectacled.modernbetaforge.world.biome.layer;

public enum GenLayerType {
    VANILLA("vanilla"),
    CONTINENTAL("continental"),
    ISLANDS("islands"),
    SMALL_ISLANDS("small_islands");
    
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
        
        throw new IllegalArgumentException("[Modern Beta] No Layer Type matching id: " + id);
    }
}
