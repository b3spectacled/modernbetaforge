package mod.bespectacled.modernbetaforge.client.gui.element;

import mod.bespectacled.modernbetaforge.client.gui.GuiColors;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonNav extends GuiButton {
    public static final int BUTTON_SIZE = 14;
    public static final int BUTTON_WIDTH_PADDING = 6;

    private static final int ARGB_KEY_ICON_BACK_ACTIVE = MathUtil.convertARGBComponentsToInt(160, 120, 120, 120);
    private static final int ARGB_KEY_ICON_BORDER_ACTIVE = MathUtil.convertARGBComponentsToInt(160, 160, 160, 160);
    private static final int ARGB_KEY_ICON_BACK_INACTIVE = MathUtil.convertARGBComponentsToInt(160, 40, 40, 40);
    private static final int ARGB_KEY_ICON_BORDER_INACTIVE = MathUtil.convertARGBComponentsToInt(160, 0, 0, 0);
    private static final int RGB_KEY_ICON_TEXT_ACTIVE = 13158600;
    private static final int RGB_KEY_ICON_TEXT_INACTIVE = 7895160;
    
    public GuiButtonNav(Minecraft mc, int id, int x, int y, String text) {
        super(id, x, y, getButtonWidth(mc, text), BUTTON_SIZE, text);
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.updateHovered(mouseX, mouseY);
            
            int boxL = this.x;
            int boxT = this.y;
            int boxR = this.x + this.width;
            int boxB = this.y + this.height;

            int colorBorder = this.enabled ? ARGB_KEY_ICON_BORDER_ACTIVE : ARGB_KEY_ICON_BORDER_INACTIVE;
            int colorBack = this.enabled ? ARGB_KEY_ICON_BACK_ACTIVE : ARGB_KEY_ICON_BACK_INACTIVE;
            int colorText = this.enabled ? this.hovered ? GuiColors.RGB_WHITE : RGB_KEY_ICON_TEXT_ACTIVE : RGB_KEY_ICON_TEXT_INACTIVE;

            drawHorizontalLine(boxL, boxR - 1, boxT - 1, colorBorder);
            drawHorizontalLine(boxL, boxR - 1, boxB, colorBorder);
            drawVerticalLine(boxL - 1, boxT - 1, boxB, colorBorder);
            drawVerticalLine(boxR, boxT - 1, boxB, colorBorder);
            drawRect(boxL + 1, boxT + 1, boxR - 1, boxB - 1, colorBack);
            this.drawCenteredString(mc.fontRenderer, this.displayString, x + width / 2 + 1, y + BUTTON_SIZE / 4, colorText);
        }
    }
    
    private void updateHovered(int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
    
    public static int getButtonWidth(Minecraft mc, String text) {
        int textWidth = mc.fontRenderer.getStringWidth(text);
        textWidth = Math.max(textWidth, BUTTON_SIZE);
        textWidth += textWidth > BUTTON_SIZE ? BUTTON_WIDTH_PADDING : 0;
        
        return textWidth;
    }
}
