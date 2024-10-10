package mod.bespectacled.modernbetaforge.api.registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public final class ModernBetaRegistry<T> {
    private final String name;
    private final Map<String, T> map; // Use LinkedHashMap so entries are displayed in order if retrieved as list.
    
    protected ModernBetaRegistry(String name) {
        this.name = name;
        this.map = new LinkedHashMap<String, T>();
    }
    
    public void register(String key, T entry) {
        if (this.contains(key)) 
            throw new IllegalArgumentException("[Modern Beta] Registry " + this.name + " already contains entry named " + key);
        
        this.map.put(key, entry);
    }
    
    public T get(String key) {
        if (!this.contains(key))
            throw new NoSuchElementException("[Modern Beta] Registry " + this.name + " does not contain entry named " + key);
        
        return this.map.get(key);
    }
    
    public T getOrElse(String key, String alternate) {
        if (!this.contains(key)) {
            return this.get(alternate);
        }
        
        return this.map.get(key);
    }
    
    public T getOrElse(String key, T alternate) {
        if (!this.contains(key)) {
            return alternate;
        }
        
        return this.map.get(key);
    }
    
    public boolean contains(String key) {
        return this.map.containsKey(key);
    }
    
    public boolean contains(T value) {
        return this.map.containsValue(value);
    }
    
    public List<T> getEntries() {
        return this.map.entrySet()
            .stream()
            .map(e -> e.getValue())
            .collect(Collectors.toList());
    }
    
    public List<String> getKeys() {
        return this.map.keySet()
            .stream()
            .collect(Collectors.toList());
    }
}
