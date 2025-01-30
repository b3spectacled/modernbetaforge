package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.lwjgl.input.Keyboard;

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
public class GuiScreenCustomizeRegistry extends GuiScreen {
    private static final String PREFIX = "createWorld.customize.custom";
    private static final int DEFAULT_SLOT_HEIGHT = 32;
    private static final int MAX_SEARCH_LENGTH = 40;
    private static final int SEARCH_BAR_LENGTH = 360;
    
    private static final int GUI_ID_SELECT = 0;
    private static final int GUI_ID_CANCEL = 1;
    private static final int GUI_ID_SEARCH = 2;
    private static final int GUI_ID_RESET = 3;
    
    private final GuiScreenCustomizeWorld parent;
    private final BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer;
    private final Function<ResourceLocation, String> nameFormatter;
    private final int slotHeight;
    private final boolean displayIcons;
    private final String initialEntry;
    private final String searchEntry;
    private final boolean startSearchFocused;
    
    private final String langName;
    private final List<ResourceLocation> registryKeys;
    private final List<Info> entries;

    private ModernBetaGeneratorSettings.Factory settings;
    private ListPreset list;
    private GuiTextField searchBar;
    private GuiButton select;
    private String searchText;
    @SuppressWarnings("unused") private int hoveredElement;
    @SuppressWarnings("unused") private long hoveredTime;
    
    protected String title;
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        int slotHeight,
        String initialEntry,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this(parent, consumer, nameFormatter, slotHeight, false, initialEntry, "", false, langName, registryKeys);
    }
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        String initialEntry,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this(parent, consumer, nameFormatter, DEFAULT_SLOT_HEIGHT, false, initialEntry, "", false, langName, registryKeys);
    }
    
    public GuiScreenCustomizeRegistry(
        GuiScreenCustomizeWorld parent,
        BiConsumer<String, ModernBetaGeneratorSettings.Factory> consumer,
        Function<ResourceLocation, String> nameFormatter,
        int slotHeight,
        boolean displayIcons,
        String initialEntry,
        String searchEntry,
        boolean startSearchFocused,
        String langName,
        List<ResourceLocation> registryKeys
    ) {
        this.title = String.format(
            "%s %s",
            I18n.format("createWorld.customize.custom.registry.title"),
            I18n.format(PREFIX + "." + langName)
        );
        this.parent = parent;
        this.consumer = consumer;
        this.nameFormatter = nameFormatter;
        this.slotHeight = slotHeight;
        this.displayIcons = displayIcons;
        this.initialEntry = initialEntry;
        this.searchEntry = searchEntry;
        this.startSearchFocused = startSearchFocused;
        
        this.langName = langName;
        this.registryKeys = registryKeys;
        this.entries = this.loadEntries();
    }
    
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        
        this.buttonList.clear();
        this.select = this.addButton(new GuiButton(GUI_ID_SELECT, this.width / 2 - 122, this.height - 27, 120, 20, I18n.format("createWorld.customize.registry.select") + " " + I18n.format(PREFIX + "." + langName)));
        this.buttonList.add(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 3, this.height - 27, 120, 20, I18n.format("gui.cancel")));
        this.buttonList.add(new GuiButton(GUI_ID_SEARCH, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 100, 40, 50, 20, I18n.format("createWorld.customize.registry.search")));
        this.buttonList.add(new GuiButton(GUI_ID_RESET, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 50, 40, 50, 20, I18n.format("createWorld.customize.registry.reset")));
        
        this.searchText = I18n.format("createWorld.customize.registry.search.info");
        
        this.settings = ModernBetaGeneratorSettings.Factory.jsonToFactory(this.parent.getSettingsString());
        this.list = this.list != null ? new ListPreset(this.list.selected) : new ListPreset(this.initialEntry);
        
        int numDisplayed = (this.list.height - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM) / this.slotHeight;
        if (this.list.selected > numDisplayed - 1)
            this.list.scrollBy(this.slotHeight * (this.list.selected - numDisplayed + 1));
        
        this.searchBar = new GuiTextField(5, this.fontRenderer, this.width / 2 - SEARCH_BAR_LENGTH / 2, 40, SEARCH_BAR_LENGTH, 20);
        this.searchBar.setMaxStringLength(MAX_SEARCH_LENGTH);
        this.searchBar.setText(this.searchEntry);
        this.searchBar.setFocused(this.startSearchFocused);
        
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
        this.drawString(this.fontRenderer, this.searchText, this.width / 2 - SEARCH_BAR_LENGTH / 2, 30, 10526880);
        this.searchBar.drawTextBox();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        this.searchBar.updateCursorCounter();
        super.updateScreen();
    }
    
    public void updateButtonValidity() {
        this.select.enabled = this.hasValidSelection();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        this.searchBar.mouseClicked(mouseX, mouseY, clicked);
        
        super.mouseClicked(mouseX, mouseY, clicked);
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!this.searchBar.textboxKeyTyped(character, keyCode)) {
            super.keyTyped(character, keyCode);
        }
        
        if (this.searchBar.isFocused() && keyCode == 28 || keyCode == 156) {
            SoundUtil.playClickSound(this.mc.getSoundHandler());
            this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                this.parent,
                this.consumer,
                this.nameFormatter,
                this.slotHeight,
                this.displayIcons,
                this.initialEntry,
                this.searchBar.getText(),
                true,
                this.langName,
                this.registryKeys
            ));
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_SELECT:
                this.consumer.accept(this.entries.get(this.list.selected).registryName, this.settings);
                this.parent.loadValues(this.settings.toString());
                this.parent.setSettingsModified(!this.settings.equals(this.parent.getDefaultSettings()));
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_CANCEL:
                this.mc.displayGuiScreen(this.parent);
                break;
            case GUI_ID_SEARCH:
                this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                    this.parent,
                    this.consumer,
                    this.nameFormatter,
                    this.slotHeight,
                    this.displayIcons,
                    this.initialEntry,
                    this.searchBar.getText(),
                    this.searchBar.isFocused() && !this.searchBar.getText().isEmpty(),
                    this.langName,
                    this.registryKeys
                ));
                break;
            case GUI_ID_RESET:
                this.mc.displayGuiScreen(new GuiScreenCustomizeRegistry(
                    this.parent,
                    this.consumer,
                    this.nameFormatter,
                    this.slotHeight,
                    this.displayIcons,
                    this.initialEntry,
                    "",
                    false,
                    this.langName,
                    this.registryKeys
                ));
                break;
        }
    }

    private boolean hasValidSelection() {
        return this.list.selected > -1 && this.list.selected < this.entries.size();
    }
    
    private List<Info> loadEntries() {
        List<Info> entries = new ArrayList<>();
        
        for (ResourceLocation registryKey : this.registryKeys) {
            String formattedName = this.nameFormatter.apply(registryKey);
            
            if (this.searchEntry == null ||
                this.searchEntry.isEmpty() ||
                formattedName.toLowerCase().contains(this.searchEntry.toLowerCase()) ||
                registryKey.toString().toLowerCase().contains(this.searchEntry.toLowerCase())
            ) {
                entries.add(new Info(formattedName, registryKey.toString()));
            }
        }
        
        return entries;
    }
    
    @SideOnly(Side.CLIENT)
    public class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 66;
        private static final int LIST_PADDING_BOTTOM = 32;
        
        public int selected;
        
        public ListPreset(int initialEntry) {
            super(
                GuiScreenCustomizeRegistry.this.mc,
                GuiScreenCustomizeRegistry.this.width,
                GuiScreenCustomizeRegistry.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizeRegistry.this.height - LIST_PADDING_BOTTOM,
                GuiScreenCustomizeRegistry.this.slotHeight
            );
            
            this.selected = initialEntry;
        }
        
        public ListPreset(String initialEntry) {
            super(
                GuiScreenCustomizeRegistry.this.mc,
                GuiScreenCustomizeRegistry.this.width,
                GuiScreenCustomizeRegistry.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizeRegistry.this.height - LIST_PADDING_BOTTOM,
                GuiScreenCustomizeRegistry.this.slotHeight
            );
            
            this.selected = -1;
            for (int i = 0; i < GuiScreenCustomizeRegistry.this.entries.size(); ++i) {
                Info info = GuiScreenCustomizeRegistry.this.entries.get(i);
                
                if (info.registryName.equals(initialEntry.toString())) {
                    this.selected = i;
                }
            }
        }
        
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public void handleMouseInput() {
            super.handleMouseInput();

            int paddingR = 13;
            int listL = (this.width - this.getListWidth()) / 2;
            int listR = (this.width + this.getListWidth()) / 2 + paddingR;
            int listMouseY = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int element = listMouseY / this.slotHeight;
            
            boolean inListBounds = this.isMouseYWithinSlotBounds(this.mouseY) && this.mouseY >= this.top && this.mouseY <= this.bottom;
            boolean inSlotBounds = this.mouseX >= listL && this.mouseX <= listR;

            if (inListBounds && inSlotBounds && listMouseY >= 0 && element < this.getSize()) {
                GuiScreenCustomizeRegistry.this.hoveredElement = element;
                GuiScreenCustomizeRegistry.this.hoveredTime = System.currentTimeMillis();
                
            } else {
                GuiScreenCustomizeRegistry.this.hoveredElement = -1;
                
            }
        }
        
        @Override
        protected int getSize() {
            return GuiScreenCustomizeRegistry.this.entries.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            
            GuiScreenCustomizeRegistry.this.updateButtonValidity();
            GuiScreenCustomizeRegistry.this.consumer.accept(
                GuiScreenCustomizeRegistry.this.entries.get(GuiScreenCustomizeRegistry.this.list.selected).registryName,
                GuiScreenCustomizeRegistry.this.settings
            );
            
            if (doubleClicked) {
                SoundUtil.playClickSound(this.mc.getSoundHandler());
                
                GuiScreenCustomizeRegistry.this.parent.loadValues(GuiScreenCustomizeRegistry.this.settings.toString());
                GuiScreenCustomizeRegistry.this.parent.setSettingsModified(!GuiScreenCustomizeRegistry.this.settings.equals(GuiScreenCustomizeRegistry.this.parent.getDefaultSettings()));
                GuiScreenCustomizeRegistry.this.mc.displayGuiScreen(GuiScreenCustomizeRegistry.this.parent);
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
            int paddingL = 4;
            int paddingR = 13;
            int paddingY = 1;
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int element = 0; element < this.getSize(); ++element) {
                int y = insideTop + element * this.slotHeight + this.headerPadding;
                int height = this.slotHeight - 4;

                if (y > this.bottom || y + height < this.top) {
                    this.updateItemPos(element, insideLeft, y, partialTicks);
                }

                if (this.showSelectionBox && this.isSelected(element)) {
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

                this.drawSlot(element, insideLeft, y, height, mouseX, mouseY, partialTicks);
            }
        }
        
        @Override
        protected void drawSlot(int biome, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            Info info = GuiScreenCustomizeRegistry.this.entries.get(biome);
            int paddingY = 3;
            int paddingL = 6;
            
            // Render Biome name
            GuiScreenCustomizeRegistry.this.fontRenderer.drawString(info.name, x + paddingL, y + paddingY, 16777215);
            
            // Render Biome registry name
            GuiScreenCustomizeRegistry.this.fontRenderer.drawString(info.registryName, x + paddingL, y + 12 + paddingY, 10526880);
        }
    }
    
    @SideOnly(Side.CLIENT)
    static class Info {
        public String name;
        public String registryName;
        public String tooltip;
        
        public Info(String name, String registryName) {
            this(name, registryName, "");
        }
        
        public Info(String name, String registryName, String tooltip) {
            this.name = name;
            this.registryName = registryName;
            this.tooltip = tooltip;
        }
    }
}
;