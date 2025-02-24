package mod.bespectacled.modernbetaforge.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.primitives.Longs;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.util.ExecutorWrapper;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizePreview extends GuiScreen implements GuiResponder, FormatHelper {
    private enum ProgressState {
        NOT_STARTED, STARTED, FAILED, SUCCEEDED, LOADED;
    }
    
    private static final String PREFIX = "createWorld.customize.preview.";
    private static final int TEXTURE_Y_OFFSET = 10;
    private static final int HINT_TEXT_OFFSET = 20;
    private static final int PROGRESS_TEXT_OFFSET = 7;
    
    private static final int GUI_ID_BIOME_COLORS = 0;
    private static final int GUI_ID_RESOLUTION = 1;
    private static final int GUI_ID_GENERATE = 2;
    private static final int GUI_ID_CANCEL = 3;
    
    private static final long COPIED_SEED_WAIT_TIME = 1000L;
    
    private final GuiScreenCustomizeWorld parent;
    private final String worldSeed;
    private final ModernBetaGeneratorSettings settings;
    private final ExecutorWrapper executor;
    private final GuiBoundsChecker mapBounds;
    private final GuiBoundsChecker seedFieldBounds;
    
    private long seed;
    private ChunkSource chunkSource;
    private BiomeSource biomeSource;
    private SurfaceBuilder surfaceBuilder;
    private BiomeInjectionRules injectionRules;
    
    private GuiListButton buttonBiomeBlend;
    private GuiSlider sliderResolution;
    private GuiButton buttonGenerate;
    private GuiButton buttonCancel;
    private ListPreset list;
    private ProgressState state;
    private int resolution;
    private boolean useBiomeBlend;
    private float progress;
    private MapTexture prevMapTexture;
    private MapTexture mapTexture;
    private boolean hoveredSeedField;
    private boolean hoveredMap;
    private boolean copiedSeedField;
    private boolean copiedTpCommand;
    private long copiedSeedFieldTime;
    private long copiedTpCommandTime;
    private Supplier<String> tpCallback;
    
    protected String title;

    public GuiScreenCustomizePreview(GuiScreenCustomizeWorld parent, String worldSeed, ModernBetaGeneratorSettings settings) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.worldSeed = worldSeed;
        this.settings = settings;
        this.executor = new ExecutorWrapper(1, "map_preview");
        this.mapBounds = new GuiBoundsChecker();
        this.seedFieldBounds = new GuiBoundsChecker();

        this.seed = parseSeed(worldSeed);
        this.initSources(this.seed, settings);
        
        this.state = ProgressState.NOT_STARTED;
        this.resolution = 512;
        this.useBiomeBlend = true;
        this.progress = 0.0f;
        this.mapTexture = new MapTexture(ModernBeta.createRegistryKey("map_preview"));
    }
    
    @Override
    public void initGui() {
        int resolutionNdx = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.resolution);
        
        this.buttonList.clear();
        this.buttonBiomeBlend = this.addButton(new GuiListButton(this, GUI_ID_BIOME_COLORS, this.width / 2 - 187, this.height - 27, I18n.format(PREFIX + "biomeBlend"), true));
        this.sliderResolution = this.addButton(new GuiSlider(this, GUI_ID_RESOLUTION, this.width / 2 - 92, this.height - 27, PREFIX + "resolution", 2, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, resolutionNdx, this));
        this.buttonGenerate = this.addButton(new GuiButton(GUI_ID_GENERATE, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format(PREFIX + "generate")));
        this.buttonCancel =  this.addButton(new GuiButton(GUI_ID_CANCEL, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.cancel")));
        
        this.buttonBiomeBlend.width = 90;
        this.sliderResolution.width = 90;
        this.buttonBiomeBlend.setValue(this.useBiomeBlend);
        
        this.list = new ListPreset();

        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - viewportSize / 2;
        int textureY = this.height / 2 - viewportSize / 2;
        textureY -= TEXTURE_Y_OFFSET;
        
        this.mapBounds.updateBounds(textureX, textureY, viewportSize, viewportSize);
        this.seedFieldBounds.updateBounds(this.getSeedFieldX(), this.getSeedFieldY(), this.getSeedFieldWidth(), this.fontRenderer.FONT_HEIGHT);
        this.updateButtonsEnabled(this.state);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        
        this.hoveredSeedField = this.seedFieldBounds.inBounds(mouseX, mouseY);
        this.hoveredMap = this.mapBounds.inBounds(mouseX, mouseY);
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 14, 16777215);
        
        if (System.currentTimeMillis() - this.copiedSeedFieldTime > COPIED_SEED_WAIT_TIME) {
            this.copiedSeedField = false;
        }
        
        if (System.currentTimeMillis() - this.copiedTpCommandTime > COPIED_SEED_WAIT_TIME) {
            this.copiedTpCommand = false;
        }

        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - viewportSize / 2;
        int textureY = this.height / 2 - viewportSize / 2;
        textureY -= TEXTURE_Y_OFFSET;

        int boxL = this.width / 2 - 60;
        int boxR = this.width / 2 + 60;
        int boxT = this.height / 2 - HINT_TEXT_OFFSET - 8;
        int boxB = this.height / 2 - PROGRESS_TEXT_OFFSET + 16;

        drawRect(textureX, textureY, textureX + viewportSize, textureY + viewportSize, MathUtil.convertARGBComponentsToInt(50, 0, 0, 0));
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY - 1, -2039584);
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY + viewportSize, -6250336);
        this.drawVerticalLine(textureX - 1, textureY - 1, textureY + viewportSize, -2039584);
        this.drawVerticalLine(textureX + viewportSize, textureY - 1, textureY + viewportSize, -6250336);
        
        switch(this.state) {
            case SUCCEEDED:
                this.mapTexture.loadMapTexture();
                this.state = ProgressState.LOADED;
                // Allow cascading into LOADED case for smooth transition
        
            case LOADED:
                this.prevMapTexture.lerpAlpha(partialTicks, 0.5f, 0.0f);
                if (this.mapTexture.mapAlpha < 1.0f) {
                    this.prevMapTexture.drawMapTexture(textureX, textureY, viewportSize);
                    this.mapTexture.lerpAlpha(partialTicks, 0.75f, 1.0f);
                }
                
                this.mapTexture.drawMapTexture(textureX, textureY, viewportSize);
                break;
                
            case STARTED:
                if (this.prevMapTexture.mapTexture != null) {
                    this.prevMapTexture.drawMapTexture(textureX, textureY, viewportSize);
                    drawRect(boxL, boxT, boxR, boxB, MathUtil.convertARGBComponentsToInt(200, 0, 0, 0));
                }
                
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "progress"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
                
                if (this.chunkSource instanceof FiniteChunkSource && !((FiniteChunkSource)this.chunkSource).hasPregenerated()) {
                    String levelProgressText = ((FiniteChunkSource)this.chunkSource).getPhase();
                    
                    if (levelProgressText != null) {
                        this.drawCenteredString(this.fontRenderer, levelProgressText + "..", this.width / 2, this.height / 2 - PROGRESS_TEXT_OFFSET, 16777215);
                    }
                } else {
                    this.drawCenteredString(this.fontRenderer, String.format("%d%%", (int)(this.progress * 100.0)), this.width / 2, this.height / 2 - PROGRESS_TEXT_OFFSET, 16777215);
                }
                
                break;
                
            case FAILED:
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "failure"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
                break;
                
            default:
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "hint"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, 16777215);
        }

        String seedPrefix = this.worldSeed.isEmpty() ? "Random " : "";
        String seedLabel = String.format("%s%s: ", seedPrefix, I18n.format(PREFIX + "seed"));
        String seedField = this.getFormattedSeed();
        int seedTextLen = this.fontRenderer.getStringWidth(seedLabel + seedField);
        int seedFieldLen = this.fontRenderer.getStringWidth(seedField);
        int seedColor = this.hoveredSeedField ? System.currentTimeMillis() - this.copiedSeedFieldTime < 100L ? 10526880 : 16777120 : 16777215;
        int seedFieldX = this.width / 2 + seedTextLen / 2 - seedFieldLen;
        int seedFieldHeight = this.height / 2 + viewportSize / 2;
        
        this.drawString(this.fontRenderer, seedLabel, this.width / 2 - seedTextLen / 2, seedFieldHeight, 16777215);
        this.drawString(this.fontRenderer, seedField, seedFieldX, seedFieldHeight, seedColor);
        
        int seedUnderlineShadow = MathUtil.convertRGBtoARGB(4144959);
        int seedUnderline = MathUtil.convertRGBtoARGB(seedColor);
        
        if (!seedField.isEmpty()) {
            this.drawHorizontalLine(seedFieldX + 1, seedFieldX + seedFieldLen + 1, seedFieldHeight + this.fontRenderer.FONT_HEIGHT + 1, seedUnderlineShadow);
            this.drawHorizontalLine(seedFieldX, seedFieldX + seedFieldLen, seedFieldHeight + this.fontRenderer.FONT_HEIGHT, seedUnderline);
        }
        
        if (this.hoveredSeedField && !this.copiedSeedField && !this.copiedTpCommand) {
            this.drawHoveringText(I18n.format(PREFIX + "copy"), mouseX, mouseY);
            
        } else if (this.copiedSeedField) {
            this.drawHoveringText(I18n.format(PREFIX + "copied"), mouseX, mouseY);
            
        } else if (this.copiedTpCommand) {
            this.drawHoveringText(I18n.format(PREFIX + "copiedTp"), mouseX, mouseY);
            
        }
        
        if (this.state == ProgressState.LOADED && this.mapBounds.inBounds(mouseX, mouseY)) {
            float x = this.mapBounds.getRelativeX(mouseX) / (float)viewportSize * this.resolution;
            float y = this.mapBounds.getRelativeY(mouseY) / (float)viewportSize * this.resolution;

            x -= (float)this.resolution / 2f;
            y -= (float)this.resolution / 2f;
            
            int height = this.chunkSource.getHeight((int)x, (int)y, HeightmapChunk.Type.SURFACE);
            Biome biome = this.biomeSource.getBiome((int)x, (int)y);
            
            BiomeInjectionContext context = DrawUtil.createInjectionContext(this.chunkSource, this.surfaceBuilder, (int)x, (int)y, biome);
            Biome injectedBiome = this.injectionRules.test(context, (int)x, (int)y, BiomeInjectionStep.ALL);
            biome = injectedBiome != null ? injectedBiome : biome;
            
            String coordinateText = String.format("%d, %d, %d", (int)x, height, (int)y);
            String biomeText = biome.getBiomeName();
            
            int tpX = (int)x;
            int tpZ = (int)y;
            int tpHeight = height < this.chunkSource.getSeaLevel() ? this.chunkSource.getSeaLevel() : height + 1;
            this.tpCallback = () -> String.format("/tp %d %d %d", tpX, tpHeight, tpZ);
            
            if (!this.copiedSeedField && !this.copiedTpCommand) {
                List<String> tooltips = new ArrayList<>();
                tooltips.add(coordinateText);
                tooltips.add(biomeText);

                this.drawHoveringText(tooltips, mouseX, mouseY);
            }
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    
    @Override
    public String getText(int id, String entryString, float entryValue) {
        if (id == GUI_ID_RESOLUTION) {
            return String.format("%s: %d", entryString, ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)entryValue]);
        }
        
        return String.format("%d", (int)entryValue);
    }

    @Override
    public void setEntryValue(int id, boolean value) { 
        if (id == GUI_ID_BIOME_COLORS) {
            this.useBiomeBlend = value;        
        }
    }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == GUI_ID_RESOLUTION) {
            this.resolution = ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)value];
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && this.hoveredSeedField) {
            GuiScreen.setClipboardString(this.getFormattedSeed());
            ModernBeta.log(I18n.format(PREFIX + "copied"));
            
            this.copiedSeedField = true;
            this.copiedSeedFieldTime = System.currentTimeMillis();
            
            this.copiedTpCommand = false;
        }
        
        if (mouseButton == 0 && this.hoveredMap && this.state == ProgressState.LOADED) {
            if (this.tpCallback != null) {
                GuiScreen.setClipboardString(this.tpCallback.get());
            }
            ModernBeta.log(I18n.format(PREFIX + "copiedTp"));
            
            this.copiedTpCommand = true;
            this.copiedTpCommandTime = System.currentTimeMillis();
            
            this.copiedSeedField = false;
        }
        
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case GUI_ID_GENERATE:
                this.createTerrainMap();
                break;
            case GUI_ID_CANCEL:
                this.executor.shutdown();
                this.unloadMapTexture(this.mapTexture);
                this.unloadMapTexture(this.prevMapTexture);
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    private void createTerrainMap() {
        this.progress = 0.0f;
        this.state = ProgressState.STARTED;
        this.updateButtonsEnabled(this.state);
        long time = System.currentTimeMillis();
        this.unloadMapTexture(this.prevMapTexture);
        
        this.prevMapTexture = new MapTexture(this.mapTexture.mapIdentifier, this.mapTexture.mapImage, this.mapTexture.mapTexture, 1.0f);
        this.mapTexture = new MapTexture(ModernBeta.createRegistryKey("map_preview_" + new Random().nextLong()));

        if (this.worldSeed.isEmpty()) {
            this.seed = new Random().nextLong();
            this.initSources(this.seed, this.settings);
            this.seedFieldBounds.updateBounds(this.getSeedFieldX(), this.getSeedFieldY(), this.getSeedFieldWidth(), this.fontRenderer.FONT_HEIGHT);
        }
        
        Runnable runnable = () -> {
            try {
                ModernBeta.log(Level.DEBUG, String.format("Drawing terrain map of size %s", this.resolution));
                this.mapTexture.loadMapImage(DrawUtil.createTerrainMapForPreview(
                    this.chunkSource,
                    this.biomeSource,
                    this.surfaceBuilder,
                    this.injectionRules,
                    this.resolution,
                    this.useBiomeBlend,
                    current -> this.progress = current
                ));
                ModernBeta.log(Level.DEBUG, String.format("Finished drawing terrain map in %2.3fs!", (System.currentTimeMillis() - time) / 1000f));
                this.state = ProgressState.SUCCEEDED;
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, "Failed to draw terrain map!");
                ModernBeta.log(Level.ERROR, "Error: " + e.getLocalizedMessage());
                this.state = ProgressState.FAILED;
                
            } finally {
                this.updateButtonsEnabled(this.state);
                
            }
        };
        
        this.executor.queueRunnable(runnable);
    }
    
    private void unloadMapTexture(MapTexture mapTexture) {
        if (mapTexture != null) {
            mapTexture.unloadAll();
        }
    }
    
    private void updateButtonsEnabled(ProgressState state) {
        boolean enabled = state != ProgressState.STARTED;
        
        this.buttonBiomeBlend.enabled = enabled;
        this.sliderResolution.enabled = enabled;
        this.buttonGenerate.enabled = enabled;
        this.buttonCancel.enabled = enabled;
    }
    
    private void initSources(long seed, ModernBetaGeneratorSettings settings) {
        this.chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(settings.chunkSource).apply(seed, settings);
        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(settings.biomeSource).apply(seed, settings);
        this.surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER.get(settings.surfaceBuilder).apply(this.chunkSource, settings);
        this.injectionRules = this.chunkSource.buildBiomeInjectorRules(this.biomeSource);
    }
    
    private String getFormattedSeed() {
        return this.worldSeed.isEmpty() ? this.state == ProgressState.NOT_STARTED ? "" : new Long(this.seed).toString() : this.worldSeed; 
    }
    
    private int getSeedFieldX() {
        String seedPrefix = this.worldSeed.isEmpty() ? "Random " : "";
        String seedLabel = String.format("%s%s: ", seedPrefix, I18n.format(PREFIX + "seed"));
        String seedField = this.getFormattedSeed();
        
        int seedTextLen = this.fontRenderer.getStringWidth(seedLabel + seedField);
        int seedFieldLen = this.fontRenderer.getStringWidth(seedField);
        
        return this.width / 2 + seedTextLen / 2 - seedFieldLen;
    }
    
    private int getSeedFieldY() {
        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        
        return this.height / 2 + viewportSize / 2;
    }
    
    private int getSeedFieldWidth() {
        return this.fontRenderer.getStringWidth(this.getFormattedSeed());
    }
    
    private static long parseSeed(String seedString) {
        long seed = new Random().nextLong();
        
        if (!seedString.isEmpty()) {
            try {
                seed = Longs.tryParse(seedString);
            } catch (Exception e) {
                seed = seedString.hashCode();
            }
        }
        
        return seed;
    }
    
    private static int getNdx(int[] arr, int val) {
        for (int i = 0; i < arr.length; ++i) {
            if (val == arr[i])
                return i;
        }
        
        return 0;
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
    
    @SideOnly(Side.CLIENT)
    private class MapTexture {
        private final ResourceLocation mapIdentifier;
        private BufferedImage mapImage;
        private DynamicTexture mapTexture;
        private float mapAlpha;
        
        public MapTexture(ResourceLocation mapIdentifier) {
            this(mapIdentifier, null, null, 0.0f);
        }
        
        public MapTexture(ResourceLocation mapIdentifier, BufferedImage mapImage, DynamicTexture mapTexture, float mapAlpha) {
            this.mapIdentifier = mapIdentifier;
            this.mapImage = mapImage;
            this.mapTexture = mapTexture;
            this.mapAlpha = mapAlpha;
        }
        
        public void loadMapImage(BufferedImage mapImage) {
            this.mapImage = mapImage;
        }
        
        public void loadMapTexture() {
            int mapWidth = this.mapImage.getWidth();
            int mapHeight = this.mapImage.getHeight();
            
            this.mapTexture = new DynamicTexture(mapWidth, mapHeight);
            GuiScreenCustomizePreview.this.mc.getTextureManager().loadTexture(this.mapIdentifier, this.mapTexture);

            this.mapImage.getRGB(0, 0, mapWidth, mapHeight, this.mapTexture.getTextureData(), 0, mapWidth);
            this.mapTexture.updateDynamicTexture();
            this.unloadMapImage();
        }
        
        public void unloadMapImage() {
            this.mapImage = null;
        }
        
        public void unloadMapTexture() {
            GuiScreenCustomizePreview.this.mc.getTextureManager().deleteTexture(this.mapIdentifier);
            this.mapTexture = null;
        }
        
        public void unloadAll() {
            this.unloadMapImage();
            this.unloadMapTexture();
        }
        
        public void drawMapTexture(int textureX, int textureY, int viewportSize) {
            GuiScreenCustomizePreview preview = GuiScreenCustomizePreview.this;
            
            int width = preview.width;
            int height = preview.height;
            
            if (this.mapTexture != null) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, this.mapAlpha);
                preview.mc.getTextureManager().bindTexture(this.mapIdentifier);
                GlStateManager.enableBlend();
                Gui.drawModalRectWithCustomSizedTexture(
                    textureX,
                    textureY,
                    0.0f,
                    0.0f,
                    viewportSize,
                    viewportSize,
                    viewportSize,
                    viewportSize
                );
                GlStateManager.disableBlend();
                
                preview.drawCenteredString(fontRenderer, "N", width / 2 + 2, height / 2 - viewportSize / 2 + 2 - TEXTURE_Y_OFFSET, 16777120);
                preview.drawCenteredString(fontRenderer, "S", width / 2 + 2, height / 2 + viewportSize / 2 - 9 - TEXTURE_Y_OFFSET, 16777120);
                preview.drawCenteredString(fontRenderer, "E", width / 2 + viewportSize / 2 - 4, height / 2 - 3 - TEXTURE_Y_OFFSET, 16777120);
                preview.drawCenteredString(fontRenderer, "W", width / 2 - viewportSize / 2 + 5, height / 2 - 3 - TEXTURE_Y_OFFSET, 16777120);
            }
        }
        
        public void lerpAlpha(float partialTicks, float scale, float target) {
            this.mapAlpha = MathUtil.lerp(partialTicks * scale, this.mapAlpha, target);
        }
    }
}
