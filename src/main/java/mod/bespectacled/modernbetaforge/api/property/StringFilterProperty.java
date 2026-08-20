package mod.bespectacled.modernbetaforge.api.property;

import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;

public abstract class StringFilterProperty extends StringProperty {
    private final Predicate<ResourceLocation> filter;

    /**
     * Constructs a new StringFilterProperty with an initial value, storing the registry name, and a predicate used
     * to filter entries when populating a list.
     * 
     * @param value The initial name value.
     * @param filter The predicate used to filter the collection values.
     */
    public StringFilterProperty(ResourceLocation value, Predicate<ResourceLocation> filter) {
        super(value.toString());
        
        this.filter = filter;
    }

    /**
     * Gets the predicate used to filter entries when populating a list.
     * 
     * @return The predicate used to filter the collection values.
     */
    public final Predicate<ResourceLocation> getFilter() {
        return this.filter;
    }
}
