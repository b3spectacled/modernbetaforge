package mod.bespectacled.modernbetaforge.api.registry;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import mod.bespectacled.modernbetaforge.api.datafix.ModDataFix;
import net.minecraft.util.ResourceLocation;

public class ModernBetaModRegistry {
    /**
     * Singleton instance of ModernBetaModRegistry for registering addon mods.
     * Registering an addon mod is only necessary if you wish to register {@link ModDataFix datafixes}
     * to {@link ModernBetaRegistries#MOD_DATA_FIX}.
     */
    public static final ModernBetaModRegistry INSTANCE = new ModernBetaModRegistry();
    
    private final Map<String, Integer> registryEntries;
    
    private ModernBetaModRegistry() {
        this.registryEntries = new LinkedHashMap<>();
    }
    
    /**
     * Registers the mod to the mod registry.
     * 
     * @param modId The mod identifier, which should also match the namespace used for the mod's {@link ResourceLocation tags}.
     * @param dataVersion The current integer data version for the mod
     */
    public void register(String modId, int dataVersion) {
        if (this.contains(modId))
            throw new IllegalArgumentException("[Modern Beta] Mod registry already contains entry named " + modId);
        
        this.registryEntries.put(modId, dataVersion);
    }
    
    /**
     * Gets the data version for the specified mod ID.
     * 
     * @param modId The mod identifier
     * @return The current integer data version for the mod
     */
    public int getDataVersion(String modId) {
        if (!this.contains(modId))
            throw new NoSuchElementException("[Modern Beta] Mod registry does not contain entry named " + modId);
        
        return this.registryEntries.get(modId);
    }
    
    /**
     * Gets whether the specified mod ID has been added to the registry.
     * 
     * @param modId The mod identifier
     * @return Whether the registry has the mod registered
     */
    public boolean contains(String modId) {
        return this.registryEntries.containsKey(modId);
    }
    
    /**
     * Gets a list of all registry entries in the registry.
     * 
     * @return The list of registry entries
     */
    public List<Entry<String, Integer>> getEntries() {
        return this.registryEntries.entrySet()
            .stream()
            .map(e -> new SimpleEntry<String, Integer>(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }
}
