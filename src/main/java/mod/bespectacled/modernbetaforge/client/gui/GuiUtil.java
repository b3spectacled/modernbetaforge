package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
    public static final int BUTTON_SIZE = 20;
    
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
    
    public static String trimString(FontRenderer fontRenderer, String string, int width) {
        if (fontRenderer.getStringWidth(string) > width) {
            string = fontRenderer.trimStringToWidth(string, width - fontRenderer.getStringWidth("...")) + "...";
        }
        
        return string;
    }
    
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double prev = left;
            left = right;
            right = prev;
        }

        if (top < bottom) {
            double prev = top;
            top = bottom;
            bottom = prev;
        }

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(r, g, b, a);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
