package mod.bespectacled.modernbetaforge.api.registry;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModernBetaClientRegistries {
    public static final ModernBetaRegistry<GuiCustomizePreset> GUI_PRESET;
    public static final ModernBetaRegistry<GuiPredicate> GUI_PREDICATE;
    
    static {
        GUI_PRESET = new ModernBetaRegistry<>("PRESET");
        GUI_PREDICATE = new ModernBetaRegistry<>("GUI_PREDICATE");
    }
}
