package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.function.Predicate;

public abstract class RangedProperty<T> extends Property<T> {
    private final T minValue;
    private final T maxValue;
    private final PropertyGuiType guiType;
    
    public RangedProperty(T value, T minValue, T maxValue, PropertyGuiType guiType) {
        super(value);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.guiType = guiType;
    }
    
    public abstract Predicate<String> getStringPredicate();
    
    public T getMinValue() {
        return this.minValue;
    }
    
    public T getMaxValue() {
        return this.maxValue;
    }
    
    public PropertyGuiType getGuiType() {
        return this.guiType;
    }
}
