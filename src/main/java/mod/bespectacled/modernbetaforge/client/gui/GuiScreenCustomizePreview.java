package mod.bespectacled.modernbetaforge.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizePreview extends GuiScreen implements GuiResponder, FormatHelper {
    private enum ProgressState {
        IDLE, STARTED, FAILED, SUCCEEDED;
    }
    
    private static final String PREFIX = "createWorld.customize.preview.";
    
    private static final int GUI_ID_RESOLUTION = 0;
    private static final int GUI_ID_GENERATE = 1;
    private static final int GUI_ID_CANCEL = 2;
    
    private static final float MIN_RES = 128.0f;
    
    private final GuiScreenCustomizeWorld parent;
    private final String worldSeed;
    private final int maxResolution;
    private final ModernBetaGeneratorSettings settings;
    private final ExecutorService executor;
    private final ResourceLocation mapLocation;
    
    private final ChunkSource chunkSource;
    private final BiomeSource biomeSource;
    
    private GuiSlider resolutionSlider;
    private GuiButton generate;
    private GuiButton cancel;
    private ListPreset list;
    private BufferedImage mapImage;
    private DynamicTexture mapTexture;
    private ProgressState state;
    private int resolution;
    private float progress;
    private String hintText;
    private String progressText;
    
    protected String title;

    public GuiScreenCustomizePreview(GuiScreenCustomizeWorld parent, String worldSeed, int maxResolution, ModernBetaGeneratorSettings settings) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.worldSeed = worldSeed;
        this.maxResolution = maxResolution;
        this.settings = settings;
        this.executor = Executors.newFixedThreadPool(1);
        this.mapLocation = ModernBeta.createRegistryKey("map_preview");
        
        ResourceLocation chunkKey = new ResourceLocation(settings.chunkSource);
        ResourceLocation biomeKey = new ResourceLocation(settings.biomeSource);
        
        long seed = this.worldSeed.isEmpty() ? new Random().nextLong() : this.worldSeed.hashCode();
        
        this.chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(chunkKey).apply(seed, this.settings);
        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(biomeKey).apply(seed, this.settings);
        
        this.state = ProgressState.IDLE;
        this.resolution = 512;
        this.progress = 0.0f;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        this.generate = this.addButton(new GuiButton(GUI_ID_GENERATE, this.width / 2 - 50, this.height - 27, 100, 20, I18n.format(PREFIX + "generate")));
        this.cancel =  this.addButton(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 53, this.height - 27, 100, 20, I18n.format("gui.cancel")));
        this.resolutionSlider = this.addButton(new GuiSlider(this, GUI_ID_RESOLUTION, this.width / 2 - 153, this.height - 27, PREFIX + "resolution", MIN_RES, this.maxResolution, this.resolution, this));
        
        this.resolutionSlider.width = 100;
        this.hintText = I18n.format(PREFIX + "hint");
        this.progressText = "";
        
        this.list = new ListPreset();
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
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 14, 16777215);

        int size = this.list.height - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - size / 2;
        int textureY = this.height / 2 - size / 2;
        int offsetY = 10;
        textureY -= offsetY;

        drawRect(textureX, textureY, textureX + size, textureY + size, MathUtil.convertARGBComponentsToInt(50, 0, 0, 0));
        this.drawHorizontalLine(textureX - 1, textureX + size, textureY - 1, -2039584);
        this.drawHorizontalLine(textureX - 1, textureX + size, textureY + size, -6250336);
        this.drawVerticalLine(textureX - 1, textureY - 1, textureY + size, -2039584);
        this.drawVerticalLine(textureX + size, textureY - 1, textureY + size, -6250336);
        
        this.loadMapTexture();
        switch(this.state) {
            case SUCCEEDED:
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(this.mapLocation);
                GlStateManager.enableBlend();
                Gui.drawModalRectWithCustomSizedTexture(
                    textureX,
                    textureY,
                    0.0f,
                    0.0f,
                    size,
                    size,
                    size,
                    size
                );
                GlStateManager.disableBlend();
                
                this.drawCenteredString(fontRenderer, "N", this.width / 2 + 2, this.height / 2 - size / 2 + 2 - offsetY, 16776960);
                this.drawCenteredString(fontRenderer, "S", this.width / 2 + 2, this.height / 2 + size / 2 - 9 - offsetY, 16776960);
                this.drawCenteredString(fontRenderer, "E", this.width / 2 + size / 2 - 4, this.height / 2 - 3 - offsetY, 16776960);
                this.drawCenteredString(fontRenderer, "W", this.width / 2 - size / 2 + 5, this.height / 2 - 3 - offsetY, 16776960);
                break;
                
            case STARTED:
                this.hintText = I18n.format(PREFIX + "progress");
                this.progressText = String.format("%d%%", (int)(this.progress * 100.0));
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - 10, 16777215);
                this.drawCenteredString(this.fontRenderer, this.progressText, this.width / 2, this.height / 2 + 3, 16777215);
                break;
                
            case FAILED:
                this.hintText = I18n.format(PREFIX + "failure");
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - 10, 16777215);
                break;
                
            default:
                this.hintText = I18n.format(PREFIX + "hint");
                
                this.drawCenteredString(this.fontRenderer, this.hintText, this.width / 2, this.height / 2 - 10, 16777215);
        }
        
        String seedTest = String.format("%s: %s", I18n.format(PREFIX + "seed"), this.worldSeed);
        this.drawCenteredString(this.fontRenderer, seedTest, this.width / 2, this.height / 2 + size / 2, 16777215);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        return String.format("%s: %d", entryString, (int)entryValue);
    }

    @Override
    public void setEntryValue(int id, boolean value) { }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == GUI_ID_RESOLUTION) {
            this.resolution = (int)value;
        }
    }

    @Override
    public void setEntryValue(int id, String value) { }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE && this.state == ProgressState.STARTED) {
            return;
        }
        
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_GENERATE:
                this.drawTerrainMap();
                break;
            case GUI_ID_CANCEL:
                this.shutdownExecutor();
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    private void drawTerrainMap() {
        this.mapImage = null;
        this.progress = 0.0f;
        this.state = ProgressState.STARTED;
        this.updateButtonsEnabled(false);
        this.deleteMapTexture();
        
        Runnable runnable = () -> {
            try {
                ModernBeta.log(Level.DEBUG, String.format("Drawing terrain map of size %s", this.resolution));
                this.mapImage = DrawUtil.createTerrainMapForPreview(this.chunkSource, this.biomeSource, this.resolution, current -> this.progress = current);
                ModernBeta.log(Level.DEBUG, "Finished drawing terrain map!");
                this.state = ProgressState.SUCCEEDED;
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, "Failed to draw terrain map!");
                ModernBeta.log(Level.ERROR, "Error: " + e.getLocalizedMessage());
                this.state = ProgressState.FAILED;
                
            } finally {
                this.updateButtonsEnabled(true);
                
            }
        };
        
        this.executor.execute(runnable);
    }
    
    private void loadMapTexture() {
        if (this.mapImage != null) {
            this.mapTexture = new DynamicTexture(this.mapImage.getWidth(), this.mapImage.getHeight());
            this.mc.getTextureManager().loadTexture(this.mapLocation, this.mapTexture);

            this.mapImage.getRGB(0, 0, this.mapImage.getWidth(), this.mapImage.getHeight(), this.mapTexture.getTextureData(), 0, this.mapImage.getWidth());
            this.mapTexture.updateDynamicTexture();
            this.mapImage = null;
            
        }
    }
    
    private void deleteMapTexture() {
        this.mc.getTextureManager().deleteTexture(this.mapLocation);
        this.mapTexture = null;
    }
    
    private void shutdownExecutor() {
        ModernBeta.log(Level.DEBUG, "Shutting down executor service..");
        
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                this.executor.shutdownNow();
                
                if (!this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    ModernBeta.log(Level.DEBUG, "Executor service still did not shutdown!");
                }
            } 
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
        }
    }
    
    private void updateButtonsEnabled(boolean enabled) {
        this.resolutionSlider.enabled = enabled;
        this.generate.enabled = enabled;
        this.cancel.enabled = enabled;
    }

    @SideOnly(Side.CLIENT)
    private class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 32;
        private static final int LIST_PADDING_BOTTOM = 32;
        
        public ListPreset() {
            super(
                GuiScreenCustomizePreview.this.mc,
                GuiScreenCustomizePreview.this.width,
                GuiScreenCustomizePreview.this.height,
                LIST_PADDING_TOP,
                GuiScreenCustomizePreview.this.height - LIST_PADDING_BOTTOM,
                1
            );
        }

        @Override
        protected int getSize() {
            return 1;
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) { }

        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        @Override
        protected void drawBackground() { }

        @Override
        protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) { }
        
    }
}
