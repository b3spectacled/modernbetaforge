package mod.bespectacled.modernbetaforge.api.world.setting;


import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;

import net.minecraft.util.math.MathHelper;

public class FloatProperty extends RangedProperty<Float> {
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
    public Predicate<String> getStringPredicate() {
        return string -> {
            Float value = Floats.tryParse(string);
            
            return string.isEmpty() || (value != null && value >= this.getMinValue() && value <= this.getMaxValue());
        };
    }
}
