package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUtil {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    
    public static final int DEFAULT_STANDARD_BUTTON_WIDTH = 70;
    public static final int MIN_STANDARD_BUTTON_WIDTH = 60;
    public static final int MAX_STANDARD_BUTTON_WIDTH = 90;
    
    public static final int DEFAULT_WIDE_BUTTON_WIDTH = 144;
    public static final int MIN_WIDE_BUTTON_WIDTH = 124;
    public static final int MAX_WIDE_BUTTON_WIDTH = 184;
    
    public static final int BUTTON_SPACE = 4;
    
    public static int getButtonWidth(int defaultWidth, int maxWidth, float currentGuiWidth) {
        return getButtonWidth(defaultWidth, defaultWidth, maxWidth, currentGuiWidth);
    }
    
    public static int getButtonWidth(int defaultWidth, int minWidth, int maxWidth, float currentGuiWidth) {
        float scale = MathHelper.clamp((currentGuiWidth * 2.0f) / DEFAULT_WIDTH, 0.0f, 1.5f);
        
        return MathHelper.clamp((int)(scale * defaultWidth), minWidth, maxWidth);
    }
    
    public static int getStandardButtonWidth(float currentGuiWidth) {
        return getButtonWidth(DEFAULT_STANDARD_BUTTON_WIDTH, MIN_STANDARD_BUTTON_WIDTH, MAX_STANDARD_BUTTON_WIDTH, currentGuiWidth);
    }
    
    public static int getWideButtonWidth(float currentGuiWidth) {
        return getButtonWidth(DEFAULT_WIDE_BUTTON_WIDTH, MIN_WIDE_BUTTON_WIDTH, MAX_WIDE_BUTTON_WIDTH, currentGuiWidth);
    }
}
