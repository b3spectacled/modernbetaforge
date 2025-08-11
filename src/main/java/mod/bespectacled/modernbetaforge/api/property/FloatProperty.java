package mod.bespectacled.modernbetaforge.api.property;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.property.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.GuiPropertyVisitor;
import mod.bespectacled.modernbetaforge.property.visitor.PropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class FloatProperty extends RangedProperty<Float> {
    private static final int DEFAULT_SCALE = 2;
    private final int scale;
    
    /**
     * Constructs a new FloatProperty with {@link Float#MIN_VALUE} and {@link Float#MAX_VALUE} value constraints.
     * The PropertyGuiType is set to {@link PropertyGuiType#FIELD}.
     * The scale specify how many decimal digits should be displayed in the GUI (min: 1, max: 3).
     * 
     * @param value The initial float property value
     * @param minValue The minimum float property value
     * @param maxValue The maximum float property value
     * @param guiType The {@link PropertyGuiType}
     * @param scale The decimal scale (number of decimal places)
     */
    public FloatProperty(float value, float minValue, float maxValue, PropertyGuiType guiType, int scale) {
        super(MathHelper.clamp(value, minValue, maxValue), minValue, maxValue, guiType);
        
        this.scale = MathHelper.clamp(scale, 1, 3);
    }
    
    /**
     * Constructs a new FloatProperty with {@link Float#MIN_VALUE} and {@link Float#MAX_VALUE} value constraints.
     * The PropertyGuiType is set to {@link PropertyGuiType#FIELD}.
     * The scale is set to the default value of {@link #DEFAULT_SCALE}.
     * 
     * @param value The initial float property value
     * @param minValue The minimum float property value
     * @param maxValue The maximum float property value
     * @param guiType The {@link PropertyGuiType}
     */
    public FloatProperty(float value, float minValue, float maxValue, PropertyGuiType guiType) {
        this(value, minValue, maxValue, guiType, DEFAULT_SCALE);
    }
    
    /**
     * Constructs a new FloatProperty with {@link Float#MIN_VALUE} and {@link Float#MAX_VALUE} value constraints.
     * The PropertyGuiType is set to {@link PropertyGuiType#FIELD}.
     * The scale is set to the default value of {@link #DEFAULT_SCALE}.
     * 
     * @param value The initial float property value
     */
    public FloatProperty(float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE, PropertyGuiType.FIELD);
    }
    
    @Override
    public void setValue(Float value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(this.scale, RoundingMode.HALF_UP);
        
        super.setValue(MathHelper.clamp(bigDecimal.floatValue(), this.getMinValue(), this.getMaxValue()));
    }
    
    @Override
    public String getType() {
        return "float";
    }
    
    @Override
    public void visitFactory(FactoryPropertyVisitor visitor, ModernBetaGeneratorSettings.Factory factory, ResourceLocation registryKey, JsonObject jsonObject) {
        visitor.visit(this, factory, registryKey, jsonObject);
    }

    @Override
    public GuiPageButtonList.GuiListEntry visitGui(GuiPropertyVisitor visitor, int guiIdentifier) {
        return visitor.visit(this, guiIdentifier);
    }

    @Override
    public void visitEntryValue(EntryValuePropertyVisitor visitor, int guiIdentifier, Object value, ResourceLocation registryKey) {
        visitor.visit(this, guiIdentifier, value, registryKey);
    }

    @Override
    public String visitNameFormatter(PropertyVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String getFormatter() {
        return String.format("%%2.%df", this.scale);
    }

    @Override
    public Predicate<String> getStringPredicate() {
        return string -> {
            Float value = Floats.tryParse(string);
            
            return string.isEmpty() || string.equals("-") || (value != null && Floats.isFinite(value));
        };
    }
    
    /**
     * Gets the number of decimal digits to be displayed in the GUI.
     * 
     * @return The scale (number of decimal places)
     */
    public int getScale() {
        return this.scale;
    }
}
