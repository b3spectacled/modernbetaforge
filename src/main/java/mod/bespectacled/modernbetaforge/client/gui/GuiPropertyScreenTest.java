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

}
