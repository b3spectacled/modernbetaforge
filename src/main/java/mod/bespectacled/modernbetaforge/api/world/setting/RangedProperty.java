package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.function.Predicate;

public abstract class RangedProperty<T> extends Property<T> {
    private final T minValue;
    private final T maxValue;
    private final PropertyGuiType guiType;
    
    /**
     * Constructs a new Property with minimum and maximum value constraints.
     * 
     * @param value The initial property value.
     * @param minValue The minimum property value.
     * @param maxValue The maximum property value.
     * @param guiType The {@link PropertyGuiType}.
     */
    public RangedProperty(T value, T minValue, T maxValue, PropertyGuiType guiType) {
        super(value);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.guiType = guiType;
    }
    
    /**
     * Gets the predicate used to constrain the minimum/maximum value when used with GUI fields.
     * 
     * @return The predicate to constrain the value.
     */
    public abstract Predicate<String> getStringPredicate();
    
    /**
     * Gets the minimum value for the range of values.
     * 
     * @return The minimum property value.
     */
    public T getMinValue() {
        return this.minValue;
    }
    
    /**
     * Gets the maximum value for the range of values.
     * 
     * @return The maximum property value.
     */
    public T getMaxValue() {
        return this.maxValue;
    }
    
    /**
     * Gets the {@link PropertyGuiType} to specify the type of GUI button this property should be used with.
     * 
     * @return The {@link PropertyGuiType}.
     */
    public PropertyGuiType getGuiType() {
        return this.guiType;
    }
}
