package mod.bespectacled.modernbetaforge.client.gui;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPropertyScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPropertyScreenTest extends GuiPropertyScreen {
    public GuiPropertyScreenTest(GuiScreenCustomizeWorld parent, ResourceLocation registryKey) {
        super(parent, registryKey);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.screenTitleHeight, this.screenTitleColor);
    }

}
