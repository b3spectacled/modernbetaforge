package mod.bespectacled.modernbetaforge.api.client.property;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiProperty<T> {
    private T value;
    
    /**
     * Constructs a new GuiProperty with initial value.
     * If registering the property to {@link ModernBetaClientRegistries#GUI_PROPERTY} then the initial value is the default value.
     * 
     * @param value The initial property value.
     */
    public GuiProperty(T value) {
        this.value = value;
    }
    
    /**
     * Gets the string id specifying the property type.
     * 
     * @return The property type
     */
    public abstract String getType();

    /**
     * Accept a GUI visitor for the property.
     * 
     * @param visitor The property visitor.
     * @param guiIdentifier The id of the GUI button.
     * @return A new GUI button entry.
     */
    public abstract GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier);
    
    /**
     * Accept a GUI generator settings visitor for the property.
     * 
     * @param visitor The property visitor.
     * @param guiIdentifier The id of the GUI button.
     * @param value The value to set.
     * @param registryKey The registry key associated with this property.
     */
    public abstract void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey);

    /**
     * Accept a name formatter visitor for the property.
     * 
     * @param visitor The property visitor.
     * @return The formatted name for the property.
     */
    public abstract String visitNameFormatter(PropertyVisitor visitor);
    
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
        
        if (!(o instanceof GuiProperty<?>)) {
            return false;
        }
        
        GuiProperty<?> other = (GuiProperty<?>)o;

        return 
            this.getType().equals(other.getType()) &&    
            this.getValue().equals(other.getValue());
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.value.hashCode();
        hashCode = 31 * hashCode + this.getType().hashCode();
        
        return hashCode;
    }
}
