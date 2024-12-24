package mod.bespectacled.modernbetaforge.api.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.util.ResourceLocation;

public final class ModernBetaRegistry<T> {
    private final String name;
    private final Map<ResourceLocation, T> registryEntries;
    
    protected ModernBetaRegistry(String name) {
        this.name = name;
        
        // Use LinkedHashMap so entries are displayed in order if retrieved as list.
        this.registryEntries = new LinkedHashMap<>();
    }
    
    public void register(ResourceLocation registryKey, T entry) {
        if (this.contains(registryKey)) 
            throw new IllegalArgumentException("[Modern Beta] Registry " + this.name + " already contains entry named " + registryKey);
        
        this.registryEntries.put(registryKey, entry);
    }
    
    public T get(ResourceLocation registryKey) {
        if (!this.contains(registryKey))
            throw new NoSuchElementException("[Modern Beta] Registry " + this.name + " does not contain entry named " + registryKey);
        
        return this.registryEntries.get(registryKey);
    }
    
    public T getOrElse(ResourceLocation registryKey, ResourceLocation alternateKey) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return this.get(alternateKey);
        }
        
        return this.registryEntries.get(registryKey);
    }
    
    public T getOrElse(ResourceLocation registryKey, T alternateValue) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return alternateValue;
        }
        
        return this.registryEntries.get(registryKey);
    }
    
    public boolean contains(ResourceLocation registryKey) {
        return this.registryEntries.containsKey(registryKey);
    }
    
    public boolean contains(T value) {
        return this.registryEntries.containsValue(value);
    }
    
    public List<T> getValues() {
        return this.registryEntries.entrySet()
            .stream()
            .map(e -> e.getValue())
            .collect(Collectors.toList());
    }
    
    public List<ResourceLocation> getKeys() {
        return this.registryEntries.keySet()
            .stream()
            .collect(Collectors.toList());
    }
    
    public Set<Entry<ResourceLocation, T>> getEntrySet() {
        return this.registryEntries.entrySet();
    }
    
    public Entry<ResourceLocation, T> getRandomEntry(Random random) {
        List<Entry<ResourceLocation, T>> entries = new ArrayList<>(this.registryEntries.entrySet());
        
        return entries.get(random.nextInt(entries.size()));
    }
}
