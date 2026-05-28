package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckbox extends GuiButton {
    public static final int DEFAULT_WIDTH = 12;
    
    private boolean toggled;
    
    public GuiCheckbox(int buttonId, int x, int y, boolean initial) {
        this(buttonId, x, y, DEFAULT_WIDTH, DEFAULT_WIDTH, initial);
    }
    
    public GuiCheckbox(int buttonId, int x, int y, int width, int height, boolean initial) {
        super(buttonId, x, y, width, height, "");
        
        this.toggled = initial;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.updateHovered(mouseX, mouseY);
            
            int boxL = this.x;
            int boxT = this.y;
            int boxR = this.x + this.width;
            int boxB = this.y + this.height;
            
            int colorSelected = this.hovered ? GuiColors.ARGB_LIGHT_GREEN : GuiColors.ARGB_GREEN;
            int colorDeselected = this.hovered ? GuiColors.ARGB_DARK_GREY : GuiColors.ARGB_DARKER_GREY;
            
            drawRect(boxL, boxT, boxR, boxB, GuiColors.ARGB_LIGHT_GREY);
            drawRect(boxL + 1, boxT + 1, boxR - 1, boxB - 1, GuiColors.ARGB_DARKEST_GREY);
            drawRect(boxL + 2, boxT + 2, boxR - 2, boxB - 2, this.toggled ? colorSelected : colorDeselected);
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.toggled = !this.toggled;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void setWidth(int width) { }
    
    public boolean getToggled() {
        return this.toggled;
    }
    
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
    
    public void toggle() {
        this.setToggled(!this.toggled);
    }
    
    private void updateHovered(int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
