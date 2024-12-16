package mod.bespectacled.modernbetaforge.api.world.setting;

import net.minecraft.util.math.MathHelper;

public class FloatProperty extends Property<Float> implements RangedProperty<Float> {
    private final float minValue;
    private final float maxValue;
    
    public FloatProperty(float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE);
    }
    
    public FloatProperty(float value, float minValue, float maxValue) {
        super(value);
        
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    @Override
    public void setValue(Float value) {
        super.setValue(MathHelper.clamp(value, this.minValue, this.maxValue));
    }
    
    @Override
    public String getType() {
        return "float";
    }

    @Override
    public Float getMinValue() {
        return this.minValue;
    }

    @Override
    public Float getMaxValue() {
        return this.maxValue;
    }
}
