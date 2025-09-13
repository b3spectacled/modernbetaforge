package mod.bespectacled.modernbetaforge.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private static final long EXPIRATION_TIME = 5000L;
    
    private final Supplier<T> constructor;
    private final Consumer<T> cleanFunc;
    private final Map<T, Long> available;
    private final Map<T, Long> used;
    
    public ObjectPool(Supplier<T> constructor, Consumer<T> cleanFunc) {
        this.constructor = constructor;
        this.cleanFunc = cleanFunc;
        this.available = new HashMap<>();
        this.used = new HashMap<>();
    }
    
    public ObjectPool(Supplier<T> constructor) {
        this(constructor, t -> {});
    }
    
    public synchronized T get() {
        long now = System.currentTimeMillis();
        
        if (!this.available.isEmpty()) {
            for (Entry<T, Long> entry : this.available.entrySet()) {
                if (now - entry.getValue() > EXPIRATION_TIME) {
                    this.pop(this.available);
                    
                } else {
                    T t = this.pop(this.available, entry.getKey());
                    this.push(this.used, t, now);
                    
                    return t;
                }
            }
        }
        
        return this.create(now);
    }
    
    public synchronized void release(T t) {
        this.cleanFunc.accept(t);
        this.available.put(t, System.currentTimeMillis());
        this.used.remove(t);
    }
    
    private T create(long now) {
        T t = this.constructor.get();
        this.push(this.used, t, now);
        
        return t;
    }
    
    private void push(Map<T, Long> map, T t, long now) {
        map.put(t, now);
    }
    
    private T pop(Map<T, Long> map) {
        Entry<T, Long> entry = map.entrySet().iterator().next();
        
        T t = entry.getKey();
        map.remove(t);
        
        return t;
    }
    
    private T pop(Map<T, Long> map, T t) {
        map.remove(t);
        
        return t;
    }
}
