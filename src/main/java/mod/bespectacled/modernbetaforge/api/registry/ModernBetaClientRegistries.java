package mod.bespectacled.modernbetaforge.api.registry;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.client.property.GuiProperty;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModernBetaClientRegistries {
    /**
     * Holds registered {@link GuiCustomizePreset world presets}.
     * Register client-side world presets here.
     */
    public static final ModernBetaRegistry<GuiCustomizePreset> GUI_PRESET;
    
    /**
     * Holds registered {@link GuiPredicate GUI predicates}.
     * Register client-side predicates to enable or disable GUI buttons based on the state of {@link ModernBetaGeneratorSettings} in the world customization GUI.
     */
    public static final ModernBetaRegistry<GuiPredicate> GUI_PREDICATE;
    
    /**
     * Holds registered {@link GuiProperty GUI properties}.
     * Registry client-side properties here.
     */
    public static final ModernBetaRegistry<GuiProperty<?>> GUI_PROPERTY;
    
    static {
        GUI_PRESET = new ModernBetaRegistry<>("PRESET");
        GUI_PREDICATE = new ModernBetaRegistry<>("GUI_PREDICATE");
        GUI_PROPERTY = new ModernBetaRegistry<>("GUI_PROPERTY");
    }
}
