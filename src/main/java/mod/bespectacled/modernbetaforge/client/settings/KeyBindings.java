package mod.bespectacled.modernbetaforge.client.settings;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.ModernBeta;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {
    private static final String PREFIX = String.format("key.%s.", ModernBeta.MODID);
    private static final String CATEGORY = String.format("%scategories.ui", PREFIX);
    private static final GuiKeyConflictContext CONFLICT_CONTEXT = new GuiKeyConflictContext();
    
    public static final KeyBinding LEFT_NAV_KEY = new KeyBinding(PREFIX + "leftNav", CONFLICT_CONTEXT, Keyboard.KEY_A, CATEGORY);
    public static final KeyBinding RIGHT_NAV_KEY = new KeyBinding(PREFIX + "rightNav", CONFLICT_CONTEXT, Keyboard.KEY_D, CATEGORY);
}
