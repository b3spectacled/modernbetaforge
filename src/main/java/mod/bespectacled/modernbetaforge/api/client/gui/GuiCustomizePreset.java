package mod.bespectacled.modernbetaforge.api.client.gui;

import mod.bespectacled.modernbetaforge.ModernBeta;

public class GuiCustomizePreset {
    public final String modId;
    public final String settings;
    public final String texture;
    public final String name;
    public final String desc;
    
    /**
     * Constructs a preset with all the fields specified.
     * 
     * @param modId The unique mod identifier. 
     * @param settings The preset settings string that you can get from the in-game GUI.
     * @param texture The preset texture asset location.
     * @param name The name of the preset. The localization prefix will be automatically attached.
     * @param desc The description of the preset. The localization prefix will be automatically attached.
     */
    public GuiCustomizePreset(String modId, String settings, String texture, String name, String desc) {
        this.modId = modId;
        this.settings = settings;
        this.texture = texture;
        this.name = name;
        this.desc = desc;
    }
    
    /**
     * Constructs a preset with all the fields specified, with default Modern Beta mod id.
     * 
     * @param settings The preset settings string that you can get from the in-game GUI.
     * @param texture The preset texture asset location.
     * @param name The name of the preset. The localization prefix will be automatically attached.
     * @param desc The description of the preset. The localization prefix will be automatically attached.
     */
    public GuiCustomizePreset(String settings, String texture, String name, String desc) {
        this(ModernBeta.MODID, settings, texture, name, desc);
    }
    
    /**
     * Constructs a preset with all the fields specified, with default Modern Beta mod id and no description.
     * 
     * @param settings The preset settings string that you can get from the in-game GUI.
     * @param texture The preset texture asset location.
     * @param name The name of the preset. The localization prefix will be automatically attached.
     */
    public GuiCustomizePreset(String settings, String texture, String name) {
        this(settings, texture, name, "");
    }
    
    /**
     * Constructs a preset with all the fields specified, with default Modern Beta mod id, no description, and no preset texture specified.
     * 
     * @param settings The preset settings string that you can get from the in-game GUI.
     * @param name The name of the preset. The localization prefix will be automatically attached.
     */
    public GuiCustomizePreset(String settings, String name) {
        this(settings, "textures/misc/unknown_pack.png", name, "");
    }
    
    /**
     * Gets a formatted string for the preset texture directory, given a filename string.
     * 
     * @param filename The filename of the preset texture
     * 
     * @return Formatted file directory string.
     */
    public static String texture(String filename) {
        return String.format("textures/gui/presets/%s", filename);
    }
    
    /**
     * Gets a formatted string for the preset name localization, given a name id string.
     * 
     * @param name The name id of the preset.
     * 
     * @return Formatted localization string
     */
    public static String name(String name) {
        return String.format("createWorld.customize.custom.preset.%s", name);
    }
    
    /**
     * Gets a formatted string for the preset description localization, given a name id string.
     * 
     * @param name The name id of the preset.
     * 
     * @return Formatted localization string
     */
    public static String info(String name) {
        return String.format("createWorld.customize.custom.preset.info.%s", name);
    }
}
