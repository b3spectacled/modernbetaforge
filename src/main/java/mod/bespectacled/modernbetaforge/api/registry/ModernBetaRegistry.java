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
import net.minecraft.util.math.MathHelper;

public final class ModernBetaRegistry<T> {
    private final String name;
    private final Map<ResourceLocation, RegistryEntry<T>> registryEntries;
    
    protected ModernBetaRegistry(String name) {
        this.name = name;
        
        // Use LinkedHashMap so entries are displayed in order if retrieved as list.
        this.registryEntries = new LinkedHashMap<>();
    }
    
    /**
     * Registers a new entry to the Modern Beta registry with the default priority.
     * Throws {@link IllegalArgumentException} if an entry with the same registry key already exists.
     * 
     * @param registryKey {@link ResourceLocation} denoting the entry's unique identifier
     * @param entry The entry
     */
    public void register(ResourceLocation registryKey, T entry) {
        this.register(registryKey, entry, RegistryEntry.DEFAULT_PRIORITY);
    }

    /**
     * Registers a new entry to the Modern Beta registry with a given priority.
     * Throws an {@link IllegalArgumentException} if an entry with the same registry key already exists.
     * 
     * @param registryKey {@link ResourceLocation} denoting the entry's unique identifier
     * @param entry The entry
     * @param priority The entry's priority/ordering (minimum: 1). Lower priorities will place the entry earlier when the registry is utilized.
     */
    public void register(ResourceLocation registryKey, T entry, int priority) {
        if (this.contains(registryKey)) 
            throw new IllegalArgumentException("[Modern Beta] Registry " + this.name + " already contains entry named " + registryKey);
        
        this.registryEntries.put(registryKey, new RegistryEntry<>(entry, priority));
    }
    
    /**
     * Gets the entry related to the given registry key.
     * Throws {@link NoSuchElementException} if no entry is found.
     * 
     * @param registryKey {@link ResourceLocation} denoting the entry's unique identifier
     * @return The entry
     */
    public T get(ResourceLocation registryKey) {
        if (!this.contains(registryKey))
            throw new NoSuchElementException("[Modern Beta] Registry " + this.name + " does not contain entry named " + registryKey);
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    /**
     * Gets the entry related to the given registry key or an alternate entry based on the alternate registry key.
     * Throws {@link NoSuchElementException} if neither the primary nor the alternate entry is found.
     * 
     * @param registryKey {@link ResourceLocation} denoting the entry's unique identifier
     * @param alternateKey {@link ResourceLocation} denoting the alternate entry's unique identifier
     * @return The entry
     */
    public T getOrElse(ResourceLocation registryKey, ResourceLocation alternateKey) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return this.get(alternateKey);
        }
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    /**
     * Gets the entry related to the given registry key or an alternate entry.
     * 
     * @param registryKey {@link ResourceLocation} denoting the entry's unique identifier
     * @param alternateValue The alternate entry
     * @return The entry
     */
    public T getOrElse(ResourceLocation registryKey, T alternateValue) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate entry.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);
            
            return alternateValue;
        }
        
        return this.registryEntries.get(registryKey).entry;
    }
    
    /**
     * Checks if the provided registry key exists in the registry.
     * 
     * @param registryKey The registry key to use to check for an entry's existence
     * @param alternateKey The alternate registry key to return if the primary key's entry doesn't exist
     * @return Either the primary registry key or the alternate key, if the primary key's entry doesn't exist
     */
    public ResourceLocation validateOrElse(ResourceLocation registryKey, ResourceLocation alternateKey) {
        if (!this.contains(registryKey)) {
            String warning = String.format("Did not find key '%s' for registry '%s', getting alternate key.", registryKey, this.name);
            ModernBeta.log(Level.WARN, warning);

            return alternateKey;
        }
        
        return registryKey;
    }
    
    /**
     * Checks if an entry tied to a registry key exists in the registry.
     * 
     * @param registryKey The registry key to use to check for an entry's existence
     * @return Whether there is an entry related to the registry key
     */
    public boolean contains(ResourceLocation registryKey) {
        return this.registryEntries.containsKey(registryKey);
    }
    
    /**
     * Checks if an entry exists in the registry.
     * 
     * @param value The entry to check for
     * @return Whether there is an entry that matches the given entry
     */
    public boolean contains(T value) {
        return this.registryEntries.containsValue(value);
    }
    
    /**
     * Gets a list of all entries in the registry. The list is sorted by priority, then insertion order.
     * 
     * @return The list of entry values
     */
    public List<T> getValues() {
        return this.sortRegistryEntries()
            .stream()
            .map(e -> e.getValue().entry)
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    /**
     * Gets a list if all registry keys in the registry. The list is sorted by priority, then insertion order.
     * 
     * @return The list of registry keys
     */
    public List<ResourceLocation> getKeys() {
        return this.sortRegistryEntries()
            .stream()
            .map(e -> e.getKey())
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    /**
     * Gets a list of all registry entries, including keys and values, in the registry.
     * The list is sorted by priority, then insertion order.
     * 
     * @return The list of registry entries
     */
    public List<Entry<ResourceLocation, T>> getEntries() {
        return this.sortRegistryEntries()
            .stream()
            .map(e -> new SimpleEntry<ResourceLocation, T>(e.getKey(), e.getValue().entry))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    /**
     * Gets a set of all registry entries. As a set, order is not guaranteed.
     * 
     * @return The set of registry entries
     */
    public Set<Entry<ResourceLocation, T>> getEntrySet() {
        return this.registryEntries.entrySet()
            .stream()
            .map(e -> new SimpleEntry<ResourceLocation, T>(e.getKey(), e.getValue().entry))
            .collect(Collectors.toSet());
    }
    
    /**
     * Gets a random registry entry from the registry.
     * 
     * @param random The random object
     * @return A random registry entry
     */
    public Entry<ResourceLocation, T> getRandomEntry(Random random) {
        List<Entry<ResourceLocation, T>> entries = new ArrayList<>(this.getEntrySet());
        
        return entries.get(random.nextInt(entries.size()));
    }
    
    /**
     * Sets the priority of an entry in the registry and returns the previous priority.
     * Throws {@link NoSuchElementException} if no entry is found.
     * 
     * @param registryKey The registry key of the entry to modify
     * @param priority The new integer priority
     * @return The entry's previous priority
     */
    public int setPriority(ResourceLocation registryKey, int priority) {
        if (!this.contains(registryKey))
            throw new NoSuchElementException("[Modern Beta] Registry " + this.name + " does not contain entry named " + registryKey);
        
        return this.registryEntries.get(registryKey).setPriority(priority);
    }
    
    private List<Entry<ResourceLocation, RegistryEntry<T>>> sortRegistryEntries() {
        List<Entry<ResourceLocation, RegistryEntry<T>>> registryEntries = new ArrayList<>(this.registryEntries.entrySet());
        registryEntries.sort(Entry.comparingByValue());
        
        return registryEntries;
    }
    
    private static class RegistryEntry<T> implements Comparable<RegistryEntry<T>> {
        private static final int DEFAULT_PRIORITY = 1000;
        
        private final T entry;
        private int priority;
        
        private RegistryEntry(T entry) {
            this(entry, DEFAULT_PRIORITY);
        }
        
        private RegistryEntry(T entry, int priority) {
            this.entry = entry;
            this.priority = MathHelper.clamp(priority, 1, Integer.MAX_VALUE);
        }
        
        public int setPriority(int priority) {
            int prevPriority = this.priority;
            this.priority = priority;
            
            return prevPriority;
        }

        @Override
        public int compareTo(RegistryEntry<T> o) {
            return Integer.compare(this.priority, o.priority);
        }
    }
}
