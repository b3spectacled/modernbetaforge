package mod.bespectacled.modernbetaforge.api.world.biome;

import mod.bespectacled.modernbetaforge.world.biome.source.BetaBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.PEBiomeSource;
import mod.bespectacled.modernbetaforge.world.biome.source.SingleBiomeSource;
import net.minecraft.world.storage.WorldInfo;

public enum BiomeSourceType {
    BETA("beta", "Beta"),
    SINGLE("single", "Single"),
    PE("pe", "Pocket Edition")
    ;
    
    private final String id;
    private final String name;
    
    private BiomeSourceType(String id, String formattedName) {
        this.id = id;
        this.name = formattedName;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public BiomeSource create(WorldInfo worldInfo) {
        switch(this) {
            case BETA: return new BetaBiomeSource(worldInfo);
            case SINGLE: return new SingleBiomeSource(worldInfo);
            case PE: return new PEBiomeSource(worldInfo);
            default: return new BetaBiomeSource(worldInfo);
        }
    }
    
    public static BiomeSourceType fromId(String id) throws IllegalArgumentException {
        for (BiomeSourceType type : BiomeSourceType.values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] Invalid biome source id!");
    }
}
