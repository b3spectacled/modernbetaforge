package mod.bespectacled.modernbetaforge.api.client.gui;

import java.io.IOException;

import mod.bespectacled.modernbetaforge.api.property.ScreenProperty;
import mod.bespectacled.modernbetaforge.client.gui.GuiScreenCustomizeWorld;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public abstract class GuiPropertyScreen extends GuiScreen {
    private static final String PREFIX = "createWorld.customize.custom.";
    
    protected static final int GUI_ID_CONFIRM = 0;
    protected static final int GUI_ID_CANCEL = 1;
    
    protected static final String GUI_LABEL_CONFIRM = I18n.format("createWorld.customize.custom.modernbetaforge.confirm");
    protected static final String GUI_LABEL_CANCEL = I18n.format("gui.cancel");
    
    protected final GuiScreenCustomizeWorld parent;
    protected final ModernBetaGeneratorSettings.Factory settings;
    protected final String title;
    
    protected GuiButton buttonConfirm;
    protected GuiButton buttonCancel;
    protected int screenTitleHeight;
    protected int screenTitleColor;
    
    /**
     * Constructs a new screen based on the registered {@link ScreenProperty}.
     * 
     * @param parent The {@link GuiScreenCustomizeWorld} parent screen
     * @param registryKey The {@link ResourceLocation} key of the registered {@link ScreenProperty}. Used for setting the screen title.
     */
    public GuiPropertyScreen(GuiScreenCustomizeWorld parent, ResourceLocation registryKey) {
        this.parent = parent;
        this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(this.parent.getSettingsString());
        this.title = I18n.format(PREFIX + registryKey.getNamespace() + "." + registryKey.getPath());
        
        this.screenTitleHeight = 14;
        this.screenTitleColor = 16777215;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonConfirm = this.addButton(new GuiButton(GUI_ID_CONFIRM, this.width / 2 - 122, this.height - 27, 120, 20, GUI_LABEL_CONFIRM));
        this.buttonCancel = this.addButton(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 3, this.height - 27, 120, 20, GUI_LABEL_CANCEL));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.screenTitleHeight, this.screenTitleColor);
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_CONFIRM:
                this.parent.loadValues(this.settings.toString());
                this.parent.setSettingsModified(!this.settings.equals(this.parent.getDefaultSettings()));
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
}
