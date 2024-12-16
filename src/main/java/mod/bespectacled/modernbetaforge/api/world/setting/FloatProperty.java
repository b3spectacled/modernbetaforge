package mod.bespectacled.modernbetaforge.api.world.setting;


import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;

import net.minecraft.util.math.MathHelper;

public class FloatProperty extends RangedProperty<Float> {
    public FloatProperty(float value, float minValue, float maxValue, PropertyGuiType guiType) {
        super(value, minValue, maxValue, guiType);
    }
    
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
