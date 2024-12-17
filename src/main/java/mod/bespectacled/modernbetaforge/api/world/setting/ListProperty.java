package mod.bespectacled.modernbetaforge.api.world.setting;

import java.util.Arrays;

public class ListProperty extends Property<String> {
    private final String[] values;
    
    /**
     * Constructs a new ListProperty with an initial string and an array of valid values.
     * 
     * @param value The initial String value.
     * @param values Array of valid String values.
     */
    public ListProperty(String value, String[] values) {
        super(value);
        
        this.values = values;
    }

    @Override
    public String getType() {
        return "list";
    }
    
    /**
     * Gets the array of valid String values.
     * 
     * @return A String array of valid values.
     */
    public String[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    
    /**
     * Gets the index of the matching given value from the {@link ListProperty#values} array.
     * 
     * @param value The String value to search with.
     * @return The index from {@link ListProperty#values} if the search value is present, -1 otherwise.
     */
    public int indexOf(String value) {
        for (int i = 0; i < this.values.length; ++i) {
            if (value.equals(this.values[i])) {
                return i;
            }
        }
        
        return -1;
    }
}
