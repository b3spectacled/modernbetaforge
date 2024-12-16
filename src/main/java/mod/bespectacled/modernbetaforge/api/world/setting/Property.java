package mod.bespectacled.modernbetaforge.api.world.setting;

public abstract class Property<T> {
    private T value;
    
    public Property(T value) {
        this.value = value;
    }
    
    public abstract String getType();
    
    public T getValue() {
        return this.value;
    }
    
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
