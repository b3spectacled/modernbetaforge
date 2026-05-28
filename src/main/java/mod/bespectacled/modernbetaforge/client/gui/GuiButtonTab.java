package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonTab extends GuiButton {
    private boolean selected;
    
    public GuiButtonTab(int id, int x, int y, int width, int height, String text) {
        this(id, x, y, width, height, text, false);
    }
    
    public GuiButtonTab(int id, int x, int y, int width, int height, String text, boolean initial) {
        super(id, x, y, width, height, text);
        
        this.selected = initial;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.updateHovered(mouseX, mouseY);
            
            int boxL = this.x;
            int boxT = this.y;
            int boxR = this.x + this.width;
            int boxB = this.y + this.height;
            
            boolean hoveredOrSelected = this.hovered || !this.enabled;
            
            int colorBox = hoveredOrSelected ? GuiColors.ARGB_TRANS_LIGHTER_GREY : GuiColors.ARGB_TRANS_LIGHT_GREY;
            int colorText = hoveredOrSelected ? GuiColors.RGB_WHITE : GuiColors.RGB_GREY;
            int colorBar = this.selected ?
                !this.enabled ? GuiColors.ARGB_GREEN : GuiColors.ARGB_TRANSPARENT :
                GuiColors.ARGB_TRANSPARENT;
         
            int textX = boxL + this.width / 2;
            int textY = boxT + this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2;

            drawRect(boxL, boxT + 2, boxR, boxB, colorBox);
            this.drawHorizontalLine(boxL + 1, boxR - 2, boxT + 1, colorBox);
            this.drawHorizontalLine(boxL + 3, boxR - 4, boxT, colorBox);
            this.drawHorizontalLine(boxL, boxR - 1, boxB - 1, colorBar);
            this.drawCenteredString(mc.fontRenderer, this.displayString, textX, textY, colorText);
            
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            return this.selected = this.hovered;
        }

        return false;
    }
    
    public boolean getSelected() {
        return this.selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    private void updateHovered(int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
