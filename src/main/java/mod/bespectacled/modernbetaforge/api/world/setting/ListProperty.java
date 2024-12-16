package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.Arrays;

public class ListProperty extends Property<String> {
    private final String[] values;
    
    public ListProperty(String value, String[] values) {
        super(value);
        
        this.values = values;
    }

    @Override
    public String getType() {
        return "list";
    }
    
    public String[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    
    public int indexOf(String value) {
        for (int i = 0; i < this.values.length; ++i) {
            if (value.equals(this.values[i])) {
                return i;
            }
        }
        
        return -1;
    }
}
