package mod.bespectacled.modernbetaforge.api.property;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.google.gson.JsonObject;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.setting.visitor.EntryValuePropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.FactoryPropertyVisitor;
import mod.bespectacled.modernbetaforge.world.setting.visitor.GuiPropertyVisitor;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public final class FloatProperty extends RangedProperty<Float> {
    /**
     * Constructs a new FloatProperty with minimum and maximum value constraints.
     * 
     * @param value The initial float property value.
     * @param minValue The minimum float property value.
     * @param maxValue The maximum float property value.
     * @param guiType The {@link PropertyGuiType}.
     */
    public FloatProperty(float value, float minValue, float maxValue, PropertyGuiType guiType) {
        super(value, minValue, maxValue, guiType);
    }
    
    /**
     * Constructs a new FloatProperty with {@link Float#MIN_VALUE} and {@link Float#MAX_VALUE} value constraints.
     * The PropertyGuiType is set to {@link PropertyGuiType#FIELD}.
     * 
     * @param value The initial float property value.
     */
    public FloatProperty(float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE, PropertyGuiType.FIELD);
    }
    
    @Override
    public void setValue(Float value) {
        super.setValue(MathHelper.clamp(value, this.getMinValue(), this.getMaxValue()));
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
    public String getFormatter() {
        return "%2.3f";
    }

    @Override
    public Predicate<String> getStringPredicate() {
        return string -> {
            Float value = Floats.tryParse(string);
            
            return string.isEmpty() || string.equals("-") || (value != null && Floats.isFinite(value));
        };
    }
}
