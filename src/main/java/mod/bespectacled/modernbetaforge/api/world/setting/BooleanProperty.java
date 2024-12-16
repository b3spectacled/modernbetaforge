package mod.bespectacled.modernbetaforge.api.world.setting;

public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty(boolean value) {
        super(value);
    }
    
    @Override
    public String getType() {
        return "boolean";
    }
}
