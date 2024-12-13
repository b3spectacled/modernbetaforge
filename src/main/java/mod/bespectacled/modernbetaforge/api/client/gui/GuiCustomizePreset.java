package mod.bespectacled.modernbetaforge.api.client.gui;

public class GuiCustomizePreset {
    public final String settings;
    public final String texture;
    public final String name;
    public final String desc;
    
    public GuiCustomizePreset(String settings, String texture, String name, String desc) {
        this.settings = settings;
        this.texture = texture;
        this.name = name;
        this.desc = desc;
    }
    
    public GuiCustomizePreset(String settings, String texture, String name) {
        this(settings, texture, name, "");
    }
    
    public static String texture(String filename) {
        return String.format("textures/gui/presets/%s", filename);
    }
    
    public static String name(String name) {
        return String.format("createWorld.customize.custom.preset.%s", name);
    }
    
    public static String info(String name) {
        return String.format("createWorld.customize.custom.preset.info.%s", name);
    }
}
