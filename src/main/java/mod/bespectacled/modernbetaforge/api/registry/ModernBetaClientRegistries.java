package mod.bespectacled.modernbetaforge.api.registry;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModernBetaClientRegistries {
    public static final ModernBetaRegistry<GuiCustomizePreset> PRESET;
    
    static {
        PRESET = new ModernBetaRegistry<>("PRESET");
    }
}
