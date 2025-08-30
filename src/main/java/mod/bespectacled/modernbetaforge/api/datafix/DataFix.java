package mod.bespectacled.modernbetaforge.api.datafix;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

public class DataFix {
    private final String tag;
    private final Consumer<JsonObject> dataFixConsumer;

    public DataFix(String tag, Consumer<JsonObject> dataFixConsumer) {
        this.tag = tag;
        this.dataFixConsumer = dataFixConsumer;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public Consumer<JsonObject> getDataFixConsumer() {
        return this.dataFixConsumer;
    }
}
