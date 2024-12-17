package mod.bespectacled.modernbetaforge.api.world.setting;

public class StringProperty extends Property<String> {
    /**
     * Constructs a new StringProperty with an initial string.
     * 
     * @param value The initial String value.
     */
    public StringProperty(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "string";
    }
}
