package mod.bespectacled.modernbetaforge.api.world.property;

import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;

public abstract class Property<T> {
    private T value;
    
    /**
     * Constructs a new Property with initial value.
     * If registering the property to {@link ModernBetaRegistries#PROPERTY} then the initial value is the default value.
     * 
     * @param value The initial property value.
     */
    public Property(T value) {
        this.value = value;
    }
    
    /**
     * Gets the string id specifying the property type.
     * 
     * @return The property type
     */
    public abstract String getType();
    
    /**
     * Accept a Factory visitor for the property.
     * 
     * @param visitor The property visitor.
     * @param factory The generator settings factory.
     * @param registryKey The registry key associated with this property.
     * @param jsonObject The JSON object to read/write the property from/to.
     */
    public abstract void visitFactory(FactoryPropertyVisitor visitor, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject);
    
    /**
     * Accept a GUI visitor for the property.
     * 
     * @param visitor The property visitor.
     * @param guiIdentifier The id of the GUI button.
     * @return A new GUI button entry.
     */
    public abstract GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier);
    
    /**
     * Accept a GUI generator settings setter for the property.
     * 
     * @param visitor The property visitor.
     * @param guiIdentifier The id of the GUI button.
     * @param value The value to set.
     * @param registryKey The registry key associated with this property.
     */
    public abstract void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey);

    /**
     * Gets the format string for use with the customization GUI.
     * 
     * @return The format string
     */
    public abstract String getFormatter();
    
    /**
     * Gets the property value.
     * 
     * @return The property value.
     */
    public T getValue() {
        return this.value;
    }
    
    /**
     * Sets the property value.
     * 
     * @param value The property value to set.
     */
    public void setValue(T value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof Property<?>)) {
            return false;
        }
        
        Property<?> other = (Property<?>)o;

        return 
            this.getType().equals(other.getType()) &&    
            this.getValue().equals(other.getValue());
    }
}
