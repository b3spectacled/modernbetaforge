package mod.bespectacled.modernbetaforge.api.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiCustomizePreset {
    public final String settings;
    
    /**
     * Constructs a new world customization preset.
     * 
     * @param settings The preset settings string that you can get from the in-game GUI.
     */
    public GuiCustomizePreset(String settings) {
        this.settings = settings;
    }
    
    /**
     * Gets a formatted resource location for the preset texture directory, given a resource location.
     * 
     * @param registryKey The resource location of the preset.
     * 
     * @return Formatted file directory resource location.
     */
    public static ResourceLocation formatTexture(ResourceLocation registryKey) {
        return new ResourceLocation(registryKey.getNamespace(), String.format("textures/gui/presets/%s.png", registryKey.getPath()));
    }
    
    /**
     * Gets a formatted string for the preset name localization, given a resource location.
     * 
     * @param registryKey The resource location of the preset.
     * 
     * @return Formatted localization string
     */
    public static String formatName(ResourceLocation registryKey) {
        String formattedRegistryKey = registryKey.getNamespace() + "." + registryKey.getPath();
        
        return I18n.format(String.format("createWorld.customize.custom.preset.%s", formattedRegistryKey));
    }
    
    /**
     * Gets a formatted string for the preset description localization, given a resource location.
     * 
     * @param registryKey The resource location of the preset.
     * 
     * @return Formatted localization string
     */
    public static String formatInfo(ResourceLocation registryKey) {
        String formattedRegistryKey = String.format(
            "createWorld.customize.custom.preset.info.%s",
            registryKey.getNamespace() + "." + registryKey.getPath()
        );
        
        if (I18n.hasKey(formattedRegistryKey)) {
            return I18n.format(formattedRegistryKey);
        }

        return "";
    }
}
