package mod.bespectacled.modernbetaforge.api.datafix;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DataFix {
    private final String tag;
    private final Function<JsonObject, JsonElement> dataFixer;

    /**
     * Constructs a new DataFix with a String tag target and a {@link Function} that accepts a {@link JsonObject}
     * representing the generator settings and outputs a nullable {@link JsonElement} to add or replace a value at said tag. 
     * 
     * @param tag The generator setting target.
     * @param dataFixer A function that returns the new value for the tag. The returned value may be null, in which case no change will occur.
     */
    public DataFix(String tag, Function<JsonObject, JsonElement> dataFixer) {
        this.tag = tag;
        this.dataFixer = dataFixer;
    }
    
    /**
     * Gets the generator settings tag.
     * 
     * @return The String tag.
     */
    public String getTag() {
        return this.tag;
    }
    
    /**
     * Gets the data fixer function.
     * 
     * @return A function that returns the new value for the tag. The returned value may be null, in which case no change will occur.
     */
    public Function<JsonObject, JsonElement> getDataFixer() {
        return this.dataFixer;
    }
}
