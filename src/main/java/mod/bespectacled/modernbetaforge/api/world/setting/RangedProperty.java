package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.function.Predicate;

public abstract class RangedProperty<T> extends Property<T> {
    private final T minValue;
    private final T maxValue;
    
    public RangedProperty(T value, T minValue, T maxValue) {
        super(value);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public abstract Predicate<String> getStringPredicate();
    
    public T getMinValue() {
        return this.minValue;
    }
    
    public T getMaxValue() {
        return this.maxValue;
    }
}
