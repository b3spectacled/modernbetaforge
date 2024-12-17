package mod.bespectacled.modernbetaforge.api.world.setting;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;

public abstract class Property<T> {
    private T value;
    
    /**
     * Constructs a new Property with initial value.
     * If registering the property to {@link ModernBetaRegistries#PROPERTY} then the initial value is the default value.
     * 
     * @param value The initial property value.
     */
    public Property(T value) {
        this.value = value;
    }
    
    /**
     * Gets the string id specifying the property type.
     * 
     * @return The property type
     */
    public abstract String getType();
    
    /**
     * Gets the property value.
     * 
     * @return The property value.
     */
    public T getValue() {
        return this.value;
    }
    
    /**
     * Sets the property value.
     * 
     * @param value The property value to set.
     */
    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof Property<?>)) {
            return false;
        }
        
        Property<?> other = (Property<?>)o;

        return 
            this.getType().equals(other.getType()) &&    
            this.getValue().equals(other.getValue());
    }
}
