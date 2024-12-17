package mod.bespectacled.modernbetaforge.api.world.setting;

public class BooleanProperty extends Property<Boolean> {
    /**
     * Constructs a new BooleanProperty.
     * 
     * @param value The initial boolean value.
     */
    public BooleanProperty(boolean value) {
        super(value);
    }
    
    @Override
    public String getType() {
        return "boolean";
    }
}
