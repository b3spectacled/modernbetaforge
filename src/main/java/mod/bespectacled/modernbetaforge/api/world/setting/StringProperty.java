package mod.bespectacled.modernbetaforge.api.world.setting;

public class StringProperty extends Property<String> {
    public StringProperty(String value) {
        super(value);
    }

    @Override
    public String getType() {
        return "string";
    }
}
