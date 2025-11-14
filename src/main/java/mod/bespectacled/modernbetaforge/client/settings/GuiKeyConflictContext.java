package mod.bespectacled.modernbetaforge.client.settings;

import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.settings.IKeyConflictContext;

public class GuiKeyConflictContext implements IKeyConflictContext {
    @Override
    public boolean isActive() {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        return currentScreen != null && currentScreen instanceof GuiScreenCustomizeWorld;
    }
    
    @Override
    public boolean conflicts(IKeyConflictContext other) {
        return this == other;
    }
}
