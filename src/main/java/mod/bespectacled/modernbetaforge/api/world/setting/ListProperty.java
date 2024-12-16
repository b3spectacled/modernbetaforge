package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.Arrays;

public class ListProperty extends StringProperty {
    private final String[] values;
    
    public ListProperty(String value, String[] values) {
        super(value);
        
        this.values = values;
    }
    
    public String[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
}
