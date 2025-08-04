package mod.bespectacled.modernbetaforge.api.registry;

import java.util.AbstractMap.SimpleEntry;
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
    private final Map<ResourceLocation, RegistryEntry<T>> registryEntries;
    
    protected ModernBetaRegistry(String name) {
        this.name = name;
        
        // Use LinkedHashMap so entries are displayed in order if retrieved as list.
        this.registryEntries = new LinkedHashMap<>();
    }
    
    public void register(ResourceLocation registryKey, T entry) {
        if (this.contains(registryKey)) 
            throw new IllegalArgumentException("[Modern Beta] Registry " + this.name + " already contains entry named " + registryKey);
        
        this.registryEntries.put(registryKey, new RegistryEntry<>(entry));
    }
    
    public T get(ResourceLocation registryKey) {
        if (!this.contains(registryKey))
            throw new NoSuchElementException("[Modern Beta] Registry " + this.name + " does not contain entry named " + registryKey);
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    public T getOrElse(ResourceLocation registryKey, ResourceLocation alternateKey) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return this.get(alternateKey);
        }
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    public T getOrElse(ResourceLocation registryKey, T alternateValue) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return alternateValue;
        }
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    public ResourceLocation validateOrElse(ResourceLocation registryKey, ResourceLocation alternateKey) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate key.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);

            return alternateKey;
        }
        
        return registryKey;
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
            .map(e -> e.getValue().entry)
            .collect(Collectors.toList());
    }
    
    public List<ResourceLocation> getKeys() {
        return this.registryEntries.keySet()
            .stream()
            .collect(Collectors.toList());
    }
    
    public List<Entry<ResourceLocation, T>> getEntries() {
        return this.registryEntries.entrySet()
            .stream()
            .map(e -> new SimpleEntry<ResourceLocation, T>(e.getKey(), e.getValue().entry))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    public Set<Entry<ResourceLocation, T>> getEntrySet() {
        return this.registryEntries.entrySet()
            .stream()
            .map(e -> new SimpleEntry<ResourceLocation, T>(e.getKey(), e.getValue().entry))
            .collect(Collectors.toSet());
    }
    
    public Entry<ResourceLocation, T> getRandomEntry(Random random) {
        List<Entry<ResourceLocation, T>> entries = new ArrayList<>(this.getEntrySet());
        
        return entries.get(random.nextInt(entries.size()));
    }
    
    private static class RegistryEntry<T> {
        private final T entry;
        
        private RegistryEntry(T entry) {
            this.entry = entry;
        }
    }
}
