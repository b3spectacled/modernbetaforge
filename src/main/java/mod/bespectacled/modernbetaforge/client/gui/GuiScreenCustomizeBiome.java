package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizeBiome extends GuiScreen {
    private static final int SLOT_HEIGHT = 32;
    private static final int MAX_SEARCH_LENGTH = 40;
    private static final int SEARCH_BAR_LENGTH = 360;
    
    private final GuiScreenCustomizeWorld parent;
    private final BiConsumer<String, ModernBetaChunkGeneratorSettings.Factory> consumer;
    private final String initialBiome;
    private final String searchBiome;
    private final List<Info> biomes;

    private ModernBetaChunkGeneratorSettings.Factory settings;
    private ListPreset list;
    private GuiTextField searchBar;
    private GuiButton select;
    private String searchText;
    
    protected String title;
    
    public GuiScreenCustomizeBiome(
        GuiScreenCustomizeWorld guiCustomizeWorldScreen,
        BiConsumer<String, ModernBetaChunkGeneratorSettings.Factory> consumer,
        String initialBiome,
        String searchBiome
    ) {
        this.title = "Customize Single Biome";
        this.parent = guiCustomizeWorldScreen;
        this.consumer = consumer;
        this.initialBiome = initialBiome;
        this.searchBiome = searchBiome;
        this.biomes = this.loadBiomes();
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        this.title = I18n.format("createWorld.customize.custom.biomes.title");
        this.searchText = I18n.format("createWorld.customize.biomes.search.info");
        
        this.settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(this.parent.getSettingsString());
        this.list = new ListPreset(this.initialBiome);
        
        this.searchBar = new GuiTextField(5, this.fontRenderer, this.width / 2 - SEARCH_BAR_LENGTH / 2, 40, SEARCH_BAR_LENGTH, 20);
        this.searchBar.setMaxStringLength(MAX_SEARCH_LENGTH);
        this.searchBar.setText(this.searchBiome);
        
        this.select = this.addButton(new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.biomes.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel")));
        this.buttonList.add(new GuiButton(2, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 100, 40, 50, 20, I18n.format("createWorld.customize.biomes.search")));
        this.buttonList.add(new GuiButton(3, this.width / 2 + SEARCH_BAR_LENGTH / 2 - 50, 40, 50, 20, I18n.format("createWorld.customize.biomes.reset")));
        
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
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        this.searchBar.mouseClicked(mouseX, mouseY, clicked);
        
        super.mouseClicked(mouseX, mouseY, clicked);
    }
    
    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        if (!this.searchBar.textboxKeyTyped(character, keyCode)) {
            super.keyTyped(character, keyCode);
        }
        
        if (keyCode == 28 || keyCode == 156) {
            this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this.parent, this.consumer, this.initialBiome, this.searchBar.getText()));
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case 0:
                this.parent.loadValues(this.settings.toString());
                this.parent.setSettingsModified(!this.settings.equals(this.parent.getDefaultSettings()));
                this.mc.displayGuiScreen(this.parent);
                break;
            case 1:
                this.mc.displayGuiScreen(this.parent);
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this.parent, this.consumer, this.initialBiome, this.searchBar.getText()));
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiScreenCustomizeBiome(this.parent, this.consumer, this.initialBiome, ""));
                break;
        }
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
    
    private boolean hasValidSelection() {
        return this.list.selected > -1 && this.list.selected < this.biomes.size();
    }
    
    private List<Info> loadBiomes() {
        List<Info> biomes = new ArrayList<>();
        
        for (Biome biome : ForgeRegistries.BIOMES.getValuesCollection()) {
            String name = biome.getBiomeName();
            String registryName = biome.getRegistryName().toString();
            boolean containsSearchBiome = name.contains(this.searchBiome) || registryName.contains(this.searchBiome);
            
            if (this.searchBiome == null || this.searchBiome.isEmpty() || containsSearchBiome) {
                biomes.add(new Info(name, registryName));
            }
        }
        
        return biomes;
    }
    
    private void playSound() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
    
    @SideOnly(Side.CLIENT)
    public class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 66;
        
        public int selected;
        
        public ListPreset(String initialBiome) {
            super(
                GuiScreenCustomizeBiome.this.mc,
                GuiScreenCustomizeBiome.this.width,
                GuiScreenCustomizeBiome.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizeBiome.this.height - 32,
                SLOT_HEIGHT
            );
            
            this.selected = -1;
            for (int i = 0; i < GuiScreenCustomizeBiome.this.biomes.size(); ++i) {
                Info info = GuiScreenCustomizeBiome.this.biomes.get(i);
                
                if (info.registryName.equals(initialBiome)) {
                    this.selected = i;
                }
            }
        }
        
        @Override
        protected int getSize() {
            return GuiScreenCustomizeBiome.this.biomes.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean doubleClicked, int mouseX, int mouseY) {
            this.selected = selected;
            
            GuiScreenCustomizeBiome.this.updateButtonValidity();
            GuiScreenCustomizeBiome.this.consumer.accept(
                GuiScreenCustomizeBiome.this.biomes.get(GuiScreenCustomizeBiome.this.list.selected).registryName,
                GuiScreenCustomizeBiome.this.settings
            );
            
            if (doubleClicked) {
                GuiScreenCustomizeBiome.this.playSound();
                
                GuiScreenCustomizeBiome.this.parent.loadValues(GuiScreenCustomizeBiome.this.settings.toString());
                GuiScreenCustomizeBiome.this.parent.setSettingsModified(!GuiScreenCustomizeBiome.this.settings.equals(GuiScreenCustomizeBiome.this.parent.getDefaultSettings()));
                GuiScreenCustomizeBiome.this.mc.displayGuiScreen(GuiScreenCustomizeBiome.this.parent);
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
        protected void drawSlot(int biome, int x, int y, int height, int mouseX, int mouseY, float partialTicks) {
            Info info = GuiScreenCustomizeBiome.this.biomes.get(biome);
            int paddingY = 3;
            int paddingL = 6;
            
            // Render Biome name
            GuiScreenCustomizeBiome.this.fontRenderer.drawString(info.name, x + paddingL, y + paddingY, 16777215);
            
            // Render Biome registry name
            GuiScreenCustomizeBiome.this.fontRenderer.drawString(info.registryName, x + paddingL, y + 12 + paddingY, 10526880);
        }
    }
    
    @SideOnly(Side.CLIENT)
    static class Info {
        public String name;
        public String registryName;
        
        public Info(String name, String registryName) {
            this.name = name;
            this.registryName = registryName;
        }
    }
}
