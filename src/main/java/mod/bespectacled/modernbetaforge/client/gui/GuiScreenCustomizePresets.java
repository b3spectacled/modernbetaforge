package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.client.gui.GuiCustomizePreset;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.util.SoundUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizePresets extends GuiScreen {
    private enum FilterType {
        ALL, BUILTIN, ADDON, CUSTOM
    }
    
    private static final String PREFIX = "createWorld.customize.presets.";
    private static final String PREFIX_FILTER = "createWorld.customize.presets.filter";
    
    private static final int SLOT_HEIGHT = 32;
    private static final int MAX_PRESET_LENGTH = 50000;
    
    private static final int GUI_ID_FILTER = 0;
    private static final int GUI_ID_SELECT = 1;
    private static final int GUI_ID_CANCEL = 2;
    
    private final GuiScreenCustomizeWorld parent;
    private final FilterType filterType;
    private final List<Info> presets;
    
    private ListPreset list;
    private GuiTextField export;
    private GuiButton select;
    private String shareText;
    private String listText;
    
    protected String title;
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent) {
        this(parent, FilterType.ALL);
    }
    
    public GuiScreenCustomizePresets(GuiScreenCustomizeWorld parent, FilterType filterType) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.filterType = filterType;
        this.presets = this.loadPresets(filterType);
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(GUI_ID_FILTER, this.width / 2 - 153, this.height - 27, 100, 20, this.getFilterString()));
        this.select = this.<GuiButton>addButton(new GuiButton(GUI_ID_SELECT, this.width / 2 - 50, this.height - 27, 100, 20, I18n.format(PREFIX + "select")));
        this.buttonList.add(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 53, this.height - 27, 100, 20, I18n.format("gui.cancel")));
        
        this.shareText = I18n.format(PREFIX + "share");
        this.listText = I18n.format(PREFIX + "list");
        
        this.list = new ListPreset();
        
        this.export = new GuiTextField(2, this.fontRenderer, 50, 40, this.width - 100, 20);
        this.export.setMaxStringLength(MAX_PRESET_LENGTH);
        this.export.setText(this.parent.getSettingsString());
        
        this.updateButtonValidity();
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 12, 16777215);
        this.drawString(this.fontRenderer, this.shareText, 50, 30, 10526880);
        this.drawString(this.fontRenderer, this.listText, 50, 70, 10526880);
        this.export.drawTextBox();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        this.export.updateCursorCounter();
        super.updateScreen();
    }
    
    public void updateButtonValidity() {
        this.select.enabled = this.hasValidSelection();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        this.export.mouseClicked(mouseX, mouseY, clicked);
        
        super.mouseClicked(mouseX, mouseY, clicked);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!this.export.textboxKeyTyped(character, keyCode)) {
            super.keyTyped(character, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_FILTER:
                FilterType[] values = FilterType.values();
                
                int increment = GuiScreen.isShiftKeyDown() ? -1 : 1;
                int index = this.filterType.ordinal() + increment;
                if (index < 0) index = values.length - 1;
                
                FilterType filterType = values[index % values.length];
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this.parent, filterType));
                break;
            case GUI_ID_SELECT:
                this.parent.loadValues(this.export.getText());
                this.parent.isSettingsModified();
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }

    private boolean hasValidSelection() {
        return (this.list.selected > -1 && this.list.selected < this.presets.size()) || this.export.getText().length() > 1;
    }
    
    private List<Info> loadPresets(FilterType filterType) {
        List<Info> presets = new ArrayList<>();
        
        String name;
        String desc;
        ResourceLocation texture;
        ModernBetaGeneratorSettings.Factory factory;
        
        List<ResourceLocation> filteredKeys = ModernBetaClientRegistries.GUI_PRESET.getKeys()
            .stream()
            .filter(key -> {
                switch (filterType) {
                    case ALL:
                        return true;
                    case BUILTIN:
                        return key.getNamespace().equals(ModernBeta.MODID);
                    case ADDON:
                        return !key.getNamespace().equals(ModernBeta.MODID);
                    case CUSTOM:
                        return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        for (ResourceLocation key : filteredKeys) {
            GuiCustomizePreset preset = ModernBetaClientRegistries.GUI_PRESET.get(key);
            
            name = GuiCustomizePreset.formatName(key);
            desc = GuiCustomizePreset.formatInfo(key);
            texture = GuiCustomizePreset.formatTexture(key);
            factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(preset.settings);
            
            presets.add(new Info(name, desc, texture, factory));
        }

        if (filterType == FilterType.ALL || filterType == FilterType.CUSTOM) {
            String[] customPresets = ModernBetaConfig.guiOptions.customPresets;
            for (int i = 0; i < customPresets.length; ++i) {
                String customPreset = customPresets[i];
                name = I18n.format("createWorld.customize.custom.preset.custom").concat(String.format(" %d", i + 1));
                texture = new ResourceLocation("textures/misc/unknown_pack.png");
                factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(customPreset);
                
                presets.add(new Info(name, texture, factory));
            }
        }
        
        return presets;
    }
    
    private String getFilterString() {
        return I18n.format(PREFIX_FILTER) + ": " + I18n.format(PREFIX_FILTER + "." + this.filterType.name().toLowerCase());
    }

    @SideOnly(Side.CLIENT)
    private class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 80;
        private static final int LIST_PADDING_BOTTOM = 32;
        
        public int selected;
        
        public ListPreset() {
            super(
                GuiScreenCustomizePresets.this.mc,
                GuiScreenCustomizePresets.this.width,
                GuiScreenCustomizePresets.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizePresets.this.height - LIST_PADDING_BOTTOM,
                SLOT_HEIGHT + 6
            );
            this.selected = -1;
        }
        
        @Override
        protected int getSize() {
            return GuiScreenCustomizePresets.this.presets.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            
            GuiScreenCustomizePresets.this.updateButtonValidity();
            GuiScreenCustomizePresets.this.export.setText(GuiScreenCustomizePresets.this.presets.get(GuiScreenCustomizePresets.this.list.selected).settings.toString());
            
            if (doubleClicked) {
                SoundUtil.playClickSound(this.mc.getSoundHandler());
                
                GuiScreenCustomizePresets.this.parent.loadValues(GuiScreenCustomizePresets.this.export.getText());
                GuiScreenCustomizePresets.this.parent.isSettingsModified();
                GuiScreenCustomizePresets.this.mc.displayGuiScreen(GuiScreenCustomizePresets.this.parent);
            } 
        }
        
        @Override
        protected boolean isSelected(int selected) {
            return selected == this.selected;
        }
        
        @Override
        protected void drawBackground() { }
        
        @Override
        protected void drawSelectionBox(int insideLeft, int insideTop, int mouseX, int mouseY, float partialTicks) {
            int size = this.getSize();
            int paddingL = 4;
            int paddingR = 13;
            int paddingY = 1;
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int preset = 0; preset < size; ++preset) {
                int y = insideTop + preset * this.slotHeight + this.headerPadding;
                int height = this.slotHeight - 4;

                if (y > this.bottom || y + height < this.top) {
                    this.updateItemPos(preset, insideLeft, y, partialTicks);
                }

                if (this.showSelectionBox && this.isSelected(preset)) {
                    int l = this.left + (this.width / 2 - this.getListWidth() / 2) + paddingL;
                    int r = this.left + this.width / 2 + this.getListWidth() / 2 + paddingR;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                    
                    bufferbuilder.pos((double)l, (double)(y + height + 2 + paddingY), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)r, (double)(y + height + 2 + paddingY), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                    
                    bufferbuilder.pos((double)r, (double)(y - 2 + paddingY), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                    bufferbuilder.pos((double)l, (double)(y - 2 + paddingY), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                    
                    bufferbuilder.pos((double)(l + 1), (double)(y + height + 1 + paddingY), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos((double)(r - 1), (double)(y + height + 1 + paddingY), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                    
                    bufferbuilder.pos((double)(r - 1), (double)(y - 1 + paddingY), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                    bufferbuilder.pos((double)(l + 1), (double)(y - 1 + paddingY), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                    
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }

                this.drawSlot(preset, insideLeft, y, height, mouseX, mouseY, partialTicks);
            }
        }
        
        @Override
        protected void drawSlot(int preset, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            Info info = GuiScreenCustomizePresets.this.presets.get(preset);
            int paddingY = 2;
            
            // Render preset icon
            this.blitIcon(x, y + paddingY, info.texture);
            
            // Render preset name
            GuiScreenCustomizePresets.this.fontRenderer.drawString(info.name, x + SLOT_HEIGHT + 10, y + 2 + paddingY, 16777215);
            
            // Render preset description, splitting if too long
            List<String> splitString = GuiScreenCustomizePresets.this.fontRenderer.listFormattedStringToWidth(info.desc, 190);
            if (splitString.size() > 1) {
                for (int i = 0; i < splitString.size(); ++i) {
                    String line = splitString.get(i);
                    GuiScreenCustomizePresets.this.fontRenderer.drawString(line, x + SLOT_HEIGHT + 10, y + 13 + paddingY + i * 10, 10526880);
                }
                
            } else {
                GuiScreenCustomizePresets.this.fontRenderer.drawString(info.desc, x + SLOT_HEIGHT + 10, y + 13 + paddingY, 10526880);
            }
        }

        private void blitIcon(int x, int y, ResourceLocation resourceLocation) {
            int iX = x + 5;
            int iY = y;
            
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + SLOT_HEIGHT, iY - 1, -2039584);
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + SLOT_HEIGHT, iY + SLOT_HEIGHT, -6250336);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX - 1, iY - 1, iY + SLOT_HEIGHT, -2039584);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX + SLOT_HEIGHT, iY - 1, iY + SLOT_HEIGHT, -6250336);
            
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(resourceLocation);
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.pos(iX + 0, iY + SLOT_HEIGHT, 0.0).tex(0.0, 1.0).endVertex();
            bufferBuilder.pos(iX + SLOT_HEIGHT, iY + SLOT_HEIGHT, 0.0).tex(1.0, 1.0).endVertex();
            bufferBuilder.pos(iX + SLOT_HEIGHT, iY + 0, 0.0).tex(1.0, 0.0).endVertex();
            bufferBuilder.pos(iX + 0, iY + 0, 0.0).tex(0.0, 0.0).endVertex();
            
            tessellator.draw();
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class Info {
        public String name;
        public String desc;
        public ResourceLocation texture;
        public ModernBetaGeneratorSettings.Factory settings;
        
        public Info(String name, String desc, ResourceLocation texture, ModernBetaGeneratorSettings.Factory factory) {
            this.name = name;
            this.desc = desc;
            this.texture = texture;
            this.settings = factory;
        }
        
        public Info(String name, ResourceLocation texture, ModernBetaGeneratorSettings.Factory factory) {
            this(name, "", texture, factory);
        }
    }
}
