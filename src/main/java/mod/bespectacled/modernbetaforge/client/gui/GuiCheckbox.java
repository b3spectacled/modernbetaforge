package mod.bespectacled.modernbetaforge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckbox extends GuiButton {
    private static final int WIDTH = 20;
    
    private boolean toggled;
    
    public GuiCheckbox(int buttonId, int x, int y, boolean initial) {
        super(buttonId, x, y, WIDTH, WIDTH, "");
        
        this.toggled = initial;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            int boxL = this.x;
            int boxT = this.y;
            int boxR = this.x + this.width;
            int boxB = this.y + this.height;
            
            this.updateHovered(mouseX, mouseY);
            
            drawRect(boxL, boxT, boxR, boxB, GuiColors.ARGB_LIGHT_GREY);
            drawRect(boxL + 1, boxT + 1, boxR - 1, boxB - 1, GuiColors.ARGB_TRANS_GREY);
            
            if (this.toggled) {
                drawRect(boxL + 2, boxT + 2, boxR - 2, boxB - 2, GuiColors.ARGB_TRANS_GREEN);
            }
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.toggled = !toggled;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void setWidth(int width) { }
    
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
    
    private void updateHovered(int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
