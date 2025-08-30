package mod.bespectacled.modernbetaforge.api.datafix;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DataFix {
    private final String tag;
    private final Function<JsonObject, @Nullable JsonElement> dataFixer;

    public DataFix(String tag, Function<JsonObject, @Nullable JsonElement> dataFixer) {
        this.tag = tag;
        this.dataFixer = dataFixer;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public Function<JsonObject, @Nullable JsonElement> getDataFixer() {
        return this.dataFixer;
    }
}
