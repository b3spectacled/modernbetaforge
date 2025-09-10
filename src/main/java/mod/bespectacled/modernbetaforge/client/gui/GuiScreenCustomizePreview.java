package mod.bespectacled.modernbetaforge.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.source.FiniteChunkSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.surface.SurfaceBuilder;
import mod.bespectacled.modernbetaforge.client.color.BetaColorSampler;
import mod.bespectacled.modernbetaforge.util.DrawUtil;
import mod.bespectacled.modernbetaforge.util.ExecutorWrapper;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionRules.BiomeInjectionContext;
import mod.bespectacled.modernbetaforge.world.biome.injector.BiomeInjectionStep;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.structure.ModernBetaStructures;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSim;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimMansion;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimMineshaft;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimMonument;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimScatteredFeature;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimStronghold;
import mod.bespectacled.modernbetaforge.world.structure.sim.StructureSimVillage;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenCustomizePreview extends GuiScreen implements GuiResponder, FormatHelper {
    private enum ProgressState {
        NOT_STARTED, STARTED, FAILED, SUCCEEDED, LOADED;
    }
    
    private static final String PREFIX = "createWorld.customize.preview.modernbetaforge.";
    private static final Map<ResourceLocation, ResourceLocation> STRUCTURE_ICONS = ImmutableMap.<ResourceLocation, ResourceLocation>builder()
        .put(ModernBetaStructures.VILLAGE, ModernBeta.createRegistryKey("textures/gui/preview/village.png"))
        .put(ModernBetaStructures.STRONGHOLD, ModernBeta.createRegistryKey("textures/gui/preview/stronghold.png"))
        .put(ModernBetaStructures.MINESHAFT, ModernBeta.createRegistryKey("textures/gui/preview/mineshaft.png"))
        .put(ModernBetaStructures.MONUMENT, ModernBeta.createRegistryKey("textures/gui/preview/monument.png"))
        .put(ModernBetaStructures.MANSION, ModernBeta.createRegistryKey("textures/gui/preview/mansion.png"))
        .put(ModernBetaStructures.TEMPLE, ModernBeta.createRegistryKey("textures/gui/preview/temple.png"))
        .build();
    private static final int STRUCTURE_ICON_SIZE = 10;
    
    private static final int TEXTURE_Y_OFFSET = 20;
    private static final int HINT_TEXT_OFFSET = 35;
    private static final int PROGRESS_TEXT_OFFSET = 18;
    private static final int BUTTON_LARGE_WIDTH = 164;
    private static final int BUTTON_SMALL_WIDTH = 108;
    private static final int BUTTON_SPACE = 4;
    
    private static final int RGB_WHITE = 16777215;
    private static final int RGB_YELLOW = 16777120;
    private static final int RGB_GREY = 10526880;
    
    private static final int ARGB_BORDER_LIGHT = -2039584;
    private static final int ARGB_BORDER_DARK = -6250336;
    
    private static final int ARGB_PREVIEW_BOX = MathUtil.convertARGBComponentsToInt(50, 0, 0, 0);
    private static final int ARGB_PROGRESS_BOX = MathUtil.convertARGBComponentsToInt(200, 0, 0, 0);
    private static final int ARGB_PROGRESS_BAR = MathUtil.convertARGBComponentsToInt(160, 128, 255, 128);
    
    private static final int GUI_ID_BIOME_COLORS = 0;
    private static final int GUI_ID_RESOLUTION = 1;
    private static final int GUI_ID_GENERATE = 2;
    private static final int GUI_ID_CANCEL = 3;
    private static final int GUI_ID_STRUCTURES = 4;
    
    private static final long COPIED_SEED_WAIT_TIME = 1000L;
    
    private final GuiScreenCustomizeWorld parent;
    private final String worldSeed;
    private final ModernBetaGeneratorSettings settings;
    private final ExecutorWrapper executor;
    private final GuiBoundsChecker mapBounds;
    private final GuiBoundsChecker seedFieldBounds;
    private final GuiBoundsChecker structureButtonBounds;
    private final Map<Long, StructureInfo> structureMap;
    private final Map<Long, GuiBoundsChecker> structureBounds;
    
    protected String title;
    
    private long seed;
    private ChunkSource chunkSource;
    private BiomeSource biomeSource;
    private SurfaceBuilder surfaceBuilder;
    private BiomeInjectionRules injectionRules;
    private Map<ResourceLocation, StructureSim> structureSims;

    private GuiSlider sliderResolution;
    private GuiListButton buttonBiomeBlend;
    private GuiListButton buttonStructures;
    private GuiButton buttonGenerate;
    private GuiButton buttonCancel;
    private ListPreset list;
    @SuppressWarnings("unused")
    private ProgressState prevState;
    private ProgressState state;
    private PreviewSettings previewSettings;
    private int prevResolution;
    private int selectedResolution;
    private float progress;
    private float prevProgress;
    private MapTexture prevMapTexture;
    private MapTexture mapTexture;
    private boolean copiedSeedField;
    private boolean copiedTpCommand;
    private long copiedSeedFieldTime;
    private long copiedTpCommandTime;
    private Supplier<String> tpCallback;
    private boolean haltGeneration;
    private long hoveredStructurePos;
    private boolean hoveredStructure;
    
    public GuiScreenCustomizePreview(GuiScreenCustomizeWorld parent, String worldSeed, ModernBetaGeneratorSettings settings, PreviewSettings previewSettings) {
        this.title = I18n.format(PREFIX + "title");
        this.parent = parent;
        this.worldSeed = worldSeed;
        this.settings = settings;
        this.executor = new ExecutorWrapper(1, "map_preview");
        this.mapBounds = new GuiBoundsChecker();
        this.seedFieldBounds = new GuiBoundsChecker();
        this.structureButtonBounds = new GuiBoundsChecker();
        this.structureMap = new Long2ObjectOpenHashMap<>();
        this.structureBounds = new Long2ObjectOpenHashMap<>();

        this.seed = parseSeed(worldSeed);
        this.initSources(this.seed, settings);
        
        this.state = ProgressState.NOT_STARTED;
        this.previewSettings = new PreviewSettings(previewSettings.resolution, previewSettings.useBiomeBlend, previewSettings.showStructures);
        this.progress = 0.0f;
        this.mapTexture = new MapTexture(this, ModernBeta.createRegistryKey("map_preview"));
    }
    
    @Override
    public void initGui() {
        int resolutionNdx = getNdx(ModernBetaGeneratorSettings.LEVEL_WIDTHS, this.previewSettings.resolution);
        
        int generateX = this.width / 2 - BUTTON_SPACE / 2 - BUTTON_LARGE_WIDTH;
        int cancelX = this.width / 2 + BUTTON_SPACE / 2;
        
        int resolutionX = generateX;
        int biomeX = resolutionX + BUTTON_SMALL_WIDTH + BUTTON_SPACE;
        int structureX = biomeX + BUTTON_SMALL_WIDTH + BUTTON_SPACE;
        
        this.buttonList.clear();
        this.buttonGenerate = this.addButton(new GuiButton(GUI_ID_GENERATE, generateX, this.height - 27, BUTTON_LARGE_WIDTH, 20, I18n.format(PREFIX + "generate")));
        this.buttonCancel =  this.addButton(new GuiButton(GUI_ID_CANCEL, cancelX, this.height - 27, BUTTON_LARGE_WIDTH, 20, I18n.format("gui.cancel")));

        this.sliderResolution = this.addButton(new GuiSlider(this, GUI_ID_RESOLUTION, resolutionX, this.height - 50, PREFIX + "resolution", 2, ModernBetaGeneratorSettings.LEVEL_WIDTHS.length - 1, resolutionNdx, this));
        this.buttonBiomeBlend = this.addButton(new GuiListButton(this, GUI_ID_BIOME_COLORS, biomeX, this.height - 50, I18n.format(PREFIX + "biomeBlend"), true));
        this.buttonStructures = this.addButton(new GuiListButton(this, GUI_ID_STRUCTURES, structureX, this.height - 50, I18n.format(PREFIX + "structures"), true));

        this.sliderResolution.width = BUTTON_SMALL_WIDTH;
        this.buttonBiomeBlend.width = BUTTON_SMALL_WIDTH;
        this.buttonStructures.width = BUTTON_SMALL_WIDTH;
        this.buttonBiomeBlend.setValue(this.previewSettings.useBiomeBlend);
        this.buttonStructures.setValue(this.previewSettings.showStructures);
        
        this.list = new ListPreset(this);

        int viewportSize = Math.min(this.list.height, this.list.width) - ListPreset.LIST_PADDING_TOP - ListPreset.LIST_PADDING_BOTTOM - 32;
        int textureX = this.width / 2 - viewportSize / 2;
        int textureY = this.height / 2 - viewportSize / 2;
        textureY -= TEXTURE_Y_OFFSET;
        
        this.mapBounds.updateBounds(textureX, textureY, viewportSize, viewportSize);
        this.seedFieldBounds.updateBounds(this.getSeedFieldX(), this.getSeedFieldY(), this.getSeedFieldWidth(), this.fontRenderer.FONT_HEIGHT);
        this.structureButtonBounds.updateBounds(structureX, this.height - 50, BUTTON_SMALL_WIDTH, 20);
        this.updateButtonsEnabled(this.state);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        
        this.structureButtonBounds.updateHovered(mouseX, mouseY);
        this.seedFieldBounds.updateHovered(mouseX, mouseY);
        this.mapBounds.updateHovered(mouseX, mouseY);
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
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 14, RGB_WHITE);
        
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

        int boxL = this.width / 2 - 56;
        int boxR = this.width / 2 + 56;
        int boxT = this.height / 2 - HINT_TEXT_OFFSET - 6;
        int boxB = this.height / 2 - PROGRESS_TEXT_OFFSET + 18;
        
        int progressHeight = this.height / 2 - PROGRESS_TEXT_OFFSET;
        int progressBarLength = boxR - boxL - 20;
        int progressBarL = boxL + 10;
        int progressBarR = progressBarL + progressBarLength;

        this.prevProgress = MathUtil.lerp(partialTicks, this.prevProgress, this.progress);
        int progressLength = (int)(progressBarLength * this.prevProgress);

        drawRect(textureX, textureY, textureX + viewportSize, textureY + viewportSize, ARGB_PREVIEW_BOX);
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY - 1, ARGB_BORDER_LIGHT);
        this.drawHorizontalLine(textureX - 1, textureX + viewportSize, textureY + viewportSize, ARGB_BORDER_DARK);
        this.drawVerticalLine(textureX - 1, textureY - 1, textureY + viewportSize, ARGB_BORDER_LIGHT);
        this.drawVerticalLine(textureX + viewportSize, textureY - 1, textureY + viewportSize, ARGB_BORDER_DARK);
        
        switch(this.state) {
            case SUCCEEDED:
                this.mapTexture.loadMapTexture();
                this.updateState(ProgressState.LOADED);
                // Allow cascading into LOADED case for smooth transition
        
            case LOADED:
                boolean isSameMap = !this.worldSeed.isEmpty() && this.prevResolution == this.selectedResolution;
                if (isSameMap) {
                    this.mapTexture.mapAlpha = 1.0f;
                }
                
                this.prevMapTexture.lerpAlpha(partialTicks, 0.5f, 0.0f);
                if (this.mapTexture.mapAlpha < 1.0f) {
                    this.prevMapTexture.drawMapTexture(textureX, textureY, viewportSize);
                    this.mapTexture.lerpAlpha(partialTicks, 0.5f, 1.0f);
                }
                
                this.mapTexture.drawMapTexture(textureX, textureY, viewportSize);
                this.drawStructureIcons(textureX, textureY, viewportSize, partialTicks);
                
                break;
                
            case STARTED:
                if (this.prevMapTexture.mapTexture != null) {
                    this.prevMapTexture.drawMapTexture(textureX, textureY, viewportSize);
                    this.drawStructureIcons(textureX, textureY, viewportSize, partialTicks);
                    drawRect(boxL, boxT, boxR, boxB, ARGB_PROGRESS_BOX);
                }
                
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "progress"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, RGB_WHITE);
                
                if (this.chunkSource instanceof FiniteChunkSource && !((FiniteChunkSource)this.chunkSource).hasPregenerated()) {
                    String levelProgressText = ((FiniteChunkSource)this.chunkSource).getPhase();
                    
                    if (levelProgressText != null) {
                        this.drawCenteredString(this.fontRenderer, levelProgressText + "..", this.width / 2, progressHeight, RGB_WHITE);
                    }
                } else {
                    drawRect(progressBarL, progressHeight + 9, progressBarL + progressLength, progressHeight - 2, ARGB_PROGRESS_BAR);
                    this.drawHorizontalLine(progressBarL - 2, progressBarR + 1, progressHeight - 4, ARGB_BORDER_LIGHT);
                    this.drawHorizontalLine(progressBarL - 2, progressBarR + 1, progressHeight + 10, ARGB_BORDER_DARK);
                    this.drawVerticalLine(progressBarL - 2, progressHeight + 10, progressHeight - 4, ARGB_BORDER_LIGHT);
                    this.drawVerticalLine(progressBarR + 1, progressHeight + 10, progressHeight - 4, ARGB_BORDER_DARK);
                    
                    this.drawCenteredString(this.fontRenderer, String.format("%d%%", (int)(this.progress * 100.0)), this.width / 2, progressHeight, RGB_WHITE);
                }
                
                break;
                
            case FAILED:
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "failure"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, RGB_WHITE);
                break;
                
            default:
                this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "hint"), this.width / 2, this.height / 2 - HINT_TEXT_OFFSET, RGB_WHITE);
        }

        String seedPrefix = this.worldSeed.isEmpty() ? "Random " : "";
        String seedLabel = String.format("%s%s: ", seedPrefix, I18n.format(PREFIX + "seed"));
        String seedField = this.getFormattedSeed();
        int seedTextLen = this.fontRenderer.getStringWidth(seedLabel + seedField);
        int seedFieldLen = this.fontRenderer.getStringWidth(seedField);
        int seedColor = this.seedFieldBounds.isHovered() ? System.currentTimeMillis() - this.copiedSeedFieldTime < 100L ? RGB_GREY : RGB_YELLOW : RGB_WHITE;
        int seedFieldX = this.width / 2 + seedTextLen / 2 - seedFieldLen;
        int seedFieldHeight = this.getSeedFieldY();
        
        this.drawString(this.fontRenderer, seedLabel, this.width / 2 - seedTextLen / 2, seedFieldHeight, RGB_WHITE);
        this.drawString(this.fontRenderer, seedField, seedFieldX, seedFieldHeight, seedColor);
        
        int seedUnderlineShadow = MathUtil.convertRGBtoARGB(4144959);
        int seedUnderline = MathUtil.convertRGBtoARGB(seedColor);
        
        if (!seedField.isEmpty()) {
            this.drawHorizontalLine(seedFieldX + 1, seedFieldX + seedFieldLen, seedFieldHeight + this.fontRenderer.FONT_HEIGHT + 1, seedUnderlineShadow);
            this.drawHorizontalLine(seedFieldX, seedFieldX + seedFieldLen - 1, seedFieldHeight + this.fontRenderer.FONT_HEIGHT, seedUnderline);
        }
        
        if (this.seedFieldBounds.isHovered() && !this.copiedSeedField && !this.copiedTpCommand) {
            this.drawHoveringText(I18n.format(PREFIX + "copy"), mouseX, mouseY);
            
        } else if (this.copiedSeedField) {
            this.drawHoveringText(I18n.format(PREFIX + "copied"), mouseX, mouseY);
            
        } else if (this.copiedTpCommand) {
            this.drawHoveringText(I18n.format(PREFIX + "copiedTp"), mouseX, mouseY);
            
        }
        
        if (this.structureButtonBounds.isHovered() && !this.copiedSeedField && !this.copiedTpCommand) {
            this.drawHoveringText(I18n.format(PREFIX + "structuresNote"), mouseX, mouseY);
        }
        
        this.hoveredStructure = false;
        this.hoveredStructurePos = 0L;
        
        if (this.state == ProgressState.LOADED && this.mapBounds.inBounds(mouseX, mouseY)) {
            int x = (int)(this.mapBounds.getRelativeX(mouseX) / (float)viewportSize * this.selectedResolution);
            int y = (int)(this.mapBounds.getRelativeY(mouseY) / (float)viewportSize * this.selectedResolution);

            x -= this.selectedResolution / 2f;
            y -= this.selectedResolution / 2f;
            
            int height = this.chunkSource.getHeight(x, y, HeightmapChunk.Type.SURFACE);
            Biome biome = this.sampleBiome(x, y);
            
            String coordinateText = String.format("%d, %d, %d", x, height, y);
            String biomeText = biome.getBiomeName();
            for (Entry<Long, GuiBoundsChecker> entry : this.structureBounds.entrySet()) {
                if (entry.getValue().inBounds(mouseX, mouseY)) {
                    this.hoveredStructure = true;
                    this.hoveredStructurePos = entry.getKey();
                }
            }
            
            int tpX = x;
            int tpZ = y;
            int tpHeight = height < this.chunkSource.getSeaLevel() ? this.chunkSource.getSeaLevel() : height + 1;
            this.tpCallback = () -> String.format("/tp %d %d %d", tpX, tpHeight, tpZ);
            
            if (!this.copiedSeedField && !this.copiedTpCommand) {
                List<String> tooltips = new ArrayList<>();
                tooltips.add(coordinateText);
                tooltips.add(biomeText);
                
                if (this.hoveredStructure && this.previewSettings.showStructures) {
                    String structure = this.structureMap.get(this.hoveredStructurePos).structure.getPath().toLowerCase();
                    structure = structure.substring(0, 1).toUpperCase() + structure.substring(1);
                    
                    tooltips.add(structure);
                }

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
            this.previewSettings.useBiomeBlend = value;        
        } else if (id == GUI_ID_STRUCTURES) {
            this.previewSettings.showStructures = value;
        }
    }

    @Override
    public void setEntryValue(int id, float value) {
        if (id == GUI_ID_RESOLUTION) {
            this.previewSettings.resolution = ModernBetaGeneratorSettings.LEVEL_WIDTHS[(int)value];
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
        if (mouseButton == 0 && this.seedFieldBounds.isHovered()) {
            GuiScreen.setClipboardString(this.getFormattedSeed());
            ModernBeta.log(I18n.format(PREFIX + "copied"));
            
            this.copiedSeedField = true;
            this.copiedSeedFieldTime = System.currentTimeMillis();
            
            this.copiedTpCommand = false;
        }
        
        if (mouseButton == 0 && this.mapBounds.isHovered() && this.state == ProgressState.LOADED) {
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
                this.haltGeneration = true;
                if (this.chunkSource instanceof FiniteChunkSource) {
                    ((FiniteChunkSource)this.chunkSource).haltGeneration();
                }
                
                this.executor.shutdown();
                this.unloadMapTexture(this.mapTexture);
                this.unloadMapTexture(this.prevMapTexture);
                this.parent.setPreviewSettings(this.previewSettings);
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    private void updateState(ProgressState state) {
        this.prevState = this.state;
        this.state = state;
    }
    
    private void createTerrainMap() {
        this.prevProgress = 0.0f;
        this.progress = 0.0f;
        this.prevResolution = this.selectedResolution;
        this.selectedResolution = this.previewSettings.resolution;
        this.updateState(ProgressState.STARTED);
        this.updateButtonsEnabled(this.state);
        long time = System.currentTimeMillis();
        this.unloadMapTexture(this.prevMapTexture);
        
        this.prevMapTexture = new MapTexture(this, this.mapTexture.mapIdentifier, this.mapTexture.mapImage, this.mapTexture.mapTexture, 1.0f);
        this.mapTexture = new MapTexture(this, ModernBeta.createRegistryKey("map_preview_" + new Random().nextLong()));

        if (this.worldSeed.isEmpty()) {
            this.seed = new Random().nextLong();
            this.initSources(this.seed, this.settings);
            this.seedFieldBounds.updateBounds(this.getSeedFieldX(), this.getSeedFieldY(), this.getSeedFieldWidth(), this.fontRenderer.FONT_HEIGHT);
        }
        
        Runnable runnable = () -> {
            try {
                ModernBeta.log(Level.DEBUG, String.format("Drawing terrain map of size %s", this.selectedResolution));

                // Make sure to reset climate samplers if world was previously loaded.
                BetaColorSampler.INSTANCE.resetClimateSamplers();
                
                BufferedImage newMapImage = DrawUtil.createTerrainMap(
                    this.chunkSource,
                    this.biomeSource,
                    this.surfaceBuilder,
                    this.injectionRules,
                    this.selectedResolution,
                    this.previewSettings.useBiomeBlend,
                    progress -> this.progress = progress,
                    () -> this.haltGeneration
                );

                if (newMapImage == null) {
                    ModernBeta.log(Level.DEBUG, "Terrain map drawing was canceled!");
                    this.mapTexture.loadMapImage(this.prevMapTexture.mapImage);
                } else {
                    ModernBeta.log(Level.DEBUG, String.format("Finished drawing terrain map in %2.3fs!", (System.currentTimeMillis() - time) / 1000f));
                    this.mapTexture.loadMapImage(newMapImage);
                }
                
                this.sampleStructures();
                this.updateState(ProgressState.SUCCEEDED);
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, "Failed to draw terrain map!");
                ModernBeta.log(Level.ERROR, "Error: " + e.getLocalizedMessage());
                this.updateState(ProgressState.FAILED);
                
            } finally {
                this.updateButtonsEnabled(this.state);
                
            }
        };
        
        this.executor.queueRunnable(runnable);
    }
    
    private void drawStructureIcons(int startTextureX, int startTextureY, int viewportSize, float partialTicks) {
        int chunkWidth = this.selectedResolution >> 4;
        int chunkLength = this.selectedResolution >> 4;
        
        int offsetChunkX = chunkWidth / 2;
        int offsetChunkZ = chunkLength / 2;

        for (int chunkX = -offsetChunkX; chunkX <= offsetChunkX; ++chunkX) {
            for (int chunkZ = -offsetChunkZ; chunkZ <= offsetChunkZ; ++chunkZ) {
                long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
                
                if (this.structureMap.containsKey(chunkPos)) {
                    GuiBoundsChecker bounds = this.structureBounds.get(chunkPos);
                    StructureInfo info = this.structureMap.get(chunkPos);
                    
                    ResourceLocation structure = info.structure;
                    float progress = info.iconProgress;
                    float alpha = info.iconAlpha;
                    
                    int iconSize = STRUCTURE_ICON_SIZE;
                    int iconOffset = STRUCTURE_ICON_SIZE / 2;

                    if (this.hoveredStructurePos == chunkPos) {
                        progress = (float)MathHelper.clampedLerp(progress, 2.0f, partialTicks);
                    } else {
                        progress = (float)MathHelper.clampedLerp(progress, 1.0f, partialTicks);
                    }
                    
                    if (this.state == ProgressState.STARTED || !this.previewSettings.showStructures) {
                        alpha = (float)MathHelper.clampedLerp(alpha, 0.0f, partialTicks);
                    } else {
                        alpha = (float)MathHelper.clampedLerp(alpha, 1.0f, partialTicks);
                    }
                    
                    info.iconProgress = progress;
                    info.iconAlpha = alpha;
                    
                    iconSize = Math.round(iconSize * progress);
                    iconOffset = Math.round(iconOffset * progress);
                    
                    int x = chunkX << 4;
                    int z = chunkZ << 4;
                    
                    float textureX = x + this.selectedResolution / 2f;
                    float textureY = z + this.selectedResolution / 2f;
                    
                    textureX /= this.selectedResolution;
                    textureY /= this.selectedResolution;
                    
                    textureX *= viewportSize;
                    textureY *= viewportSize;
                    
                    textureX += startTextureX;
                    textureY += startTextureY;
                    
                    int textureL = (int)textureX - iconOffset;
                    int textureR = (int)textureX - iconOffset + iconSize;
                    int textureT = (int)textureY - iconOffset;
                    int textureB = (int)textureY - iconOffset + iconSize;
                    
                    bounds.updateBounds(textureL, textureT, iconSize, iconSize);

                    GlStateManager.color(1.0F, 1.0F, 1.0F, info.iconAlpha);
                    this.parent.mc.getTextureManager().bindTexture(STRUCTURE_ICONS.get(structure));
                    GlStateManager.enableBlend();
                    
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferBuilder = tessellator.getBuffer();
                    bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                    bufferBuilder.pos(textureL, textureB, 0.0).tex(0.0, 1.0).endVertex();
                    bufferBuilder.pos(textureR, textureB, 0.0).tex(1.0, 1.0).endVertex();
                    bufferBuilder.pos(textureR, textureT, 0.0).tex(1.0, 0.0).endVertex();
                    bufferBuilder.pos(textureL, textureT, 0.0).tex(0.0, 0.0).endVertex();
                    tessellator.draw();
                    
                    GlStateManager.disableBlend();
                }
            }
        }
    }
    
    private void unloadMapTexture(MapTexture mapTexture) {
        if (mapTexture != null) {
            mapTexture.unloadAll();
        }
    }
    
    private void updateButtonsEnabled(ProgressState state) {
        boolean enabled = state != ProgressState.STARTED;

        this.sliderResolution.enabled = enabled;
        this.buttonBiomeBlend.enabled = enabled;
        this.buttonStructures.enabled = enabled;
        this.buttonGenerate.enabled = enabled;
        this.buttonCancel.enabled = true;
    }
    
    private void initSources(long seed, ModernBetaGeneratorSettings settings) {
        this.chunkSource = ModernBetaRegistries.CHUNK_SOURCE.get(settings.chunkSource).apply(seed, settings);
        this.biomeSource = ModernBetaRegistries.BIOME_SOURCE.get(settings.biomeSource).apply(seed, settings);
        this.surfaceBuilder = ModernBetaRegistries.SURFACE_BUILDER.get(settings.surfaceBuilder).apply(this.chunkSource, settings);
        this.injectionRules = this.chunkSource.createBiomeInjectionRules(this.biomeSource).build();
        
        this.structureSims = new Object2ObjectLinkedOpenHashMap<>();
        if (this.settings.useVillages)
            this.structureSims.put(ModernBetaStructures.VILLAGE, new StructureSimVillage(seed));
        if (this.settings.useStrongholds)
            this.structureSims.put(ModernBetaStructures.STRONGHOLD, new StructureSimStronghold(this.chunkSource));
        if (this.settings.useMineShafts)
            this.structureSims.put(ModernBetaStructures.MINESHAFT, new StructureSimMineshaft(seed));
        if (this.settings.useMonuments)
            this.structureSims.put(ModernBetaStructures.MONUMENT, new StructureSimMonument(seed));
        if (this.settings.useMansions)
            this.structureSims.put(ModernBetaStructures.MANSION, new StructureSimMansion(seed));
        if (this.settings.useTemples)
            this.structureSims.put(ModernBetaStructures.TEMPLE, new StructureSimScatteredFeature(seed));
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
        int fieldY = this.height / 2 + viewportSize / 2;
        fieldY -= TEXTURE_Y_OFFSET / 2;
        
        return fieldY;
    }
    
    private int getSeedFieldWidth() {
        return this.fontRenderer.getStringWidth(this.getFormattedSeed());
    }
    
    private void sampleStructures() {
        this.structureMap.clear();
        this.structureBounds.clear();
        
        int centerChunkX = 0;
        int centerChunkZ = 0;
        
        int chunkWidth = this.selectedResolution >> 4;
        int chunkLength = this.selectedResolution >> 4;
        
        int offsetChunkX = chunkWidth / 2;
        int offsetChunkZ = chunkLength / 2;
        
        for (int localChunkX = 0; localChunkX < chunkWidth; ++localChunkX) {
            int chunkX = localChunkX - offsetChunkX + centerChunkX;
            
            for (int localChunkZ = 0; localChunkZ < chunkLength; ++localChunkZ) {
                int chunkZ = localChunkZ - offsetChunkZ + centerChunkZ;
                
                for (Entry<ResourceLocation, StructureSim> entry : this.structureSims.entrySet()) {
                    // Match behavior for stronghold gen as in ModernBetaBiomeProvider
                    BiFunction<Integer, Integer, Biome> biomeFunc = entry.getKey().equals(ModernBetaStructures.STRONGHOLD) ?
                        this.biomeSource::getBiome :
                        this::sampleBiome;
                    
                    entry.getValue().generatePositions(chunkX, chunkZ, biomeFunc);
                    this.sampleStructure(chunkX, chunkZ, entry);
                }
            }
        }
    }
    
    private void sampleStructure(int chunkX, int chunkZ, Entry<ResourceLocation, StructureSim> entry) {
        int range = entry.getValue().getRange();
        
        for (int structureChunkX = chunkX - range; structureChunkX <= chunkX + range; structureChunkX++) {
            for (int structureChunkZ = chunkZ - range; structureChunkZ <= chunkZ + range; structureChunkZ++) {
                if (entry.getValue().canGenerate(structureChunkX, structureChunkZ)) {
                    if (this.chunkSource.skipChunk(structureChunkX, structureChunkZ)) {
                        continue;
                    }
                    
                    this.structureMap.put(ChunkPos.asLong(structureChunkX, structureChunkZ), new StructureInfo(entry.getKey()));
                    this.structureBounds.put(ChunkPos.asLong(structureChunkX, structureChunkZ), new GuiBoundsChecker());
                    
                    return;
                }
            }
        }
    }
    
    private Biome sampleBiome(int x, int z) { 
        Biome biome = this.biomeSource.getBiome(x, z);
        BiomeInjectionContext context = DrawUtil.createInjectionContext(this.chunkSource, this.surfaceBuilder, x, z, biome);
        
        Biome injectedBiome = this.injectionRules.test(context, x, z, BiomeInjectionStep.PRE_SURFACE);
        biome = injectedBiome != null ? injectedBiome : biome;
        context.setBiome(biome);
        
        injectedBiome = this.injectionRules.test(context, x, z, BiomeInjectionStep.CUSTOM);
        biome = injectedBiome != null ? injectedBiome : biome;
        context.setBiome(biome);
        
        injectedBiome = this.injectionRules.test(context, x, z, BiomeInjectionStep.POST_SURFACE);
        biome = injectedBiome != null ? injectedBiome : biome;
        
        return biome;
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
    private static class ListPreset extends GuiSlot {
        private static final int LIST_PADDING_TOP = 32;
        private static final int LIST_PADDING_BOTTOM = 54;
        
        public ListPreset(GuiScreenCustomizePreview parent) {
            super(
                parent.mc,
                parent.width,
                parent.height,
                LIST_PADDING_TOP,
                parent.height - LIST_PADDING_BOTTOM,
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
    private static class MapTexture {
        private final GuiScreenCustomizePreview parent;
        private final ResourceLocation mapIdentifier;
        private BufferedImage mapImage;
        private DynamicTexture mapTexture;
        private float mapAlpha;
        
        public MapTexture(GuiScreenCustomizePreview parent, ResourceLocation mapIdentifier) {
            this(parent, mapIdentifier, null, null, 0.0f);
        }
        
        public MapTexture(GuiScreenCustomizePreview parent, ResourceLocation mapIdentifier, BufferedImage mapImage, DynamicTexture mapTexture, float mapAlpha) {
            this.parent = parent;
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
            this.parent.mc.getTextureManager().loadTexture(this.mapIdentifier, this.mapTexture);

            this.mapImage.getRGB(0, 0, mapWidth, mapHeight, this.mapTexture.getTextureData(), 0, mapWidth);
            this.mapTexture.updateDynamicTexture();
            this.unloadMapImage();
        }
        
        public void unloadMapImage() {
            this.mapImage = null;
        }
        
        public void unloadMapTexture() {
            this.parent.mc.getTextureManager().deleteTexture(this.mapIdentifier);
            this.mapTexture = null;
        }
        
        public void unloadAll() {
            this.unloadMapImage();
            this.unloadMapTexture();
        }
        
        public void drawMapTexture(int textureX, int textureY, int viewportSize) {
            int width = this.parent.width;
            int height = this.parent.height;
            
            if (this.mapTexture != null) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, this.mapAlpha);
                this.parent.mc.getTextureManager().bindTexture(this.mapIdentifier);
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
                
                this.parent.drawCenteredString(this.parent.fontRenderer, "N", width / 2 + 2, height / 2 - viewportSize / 2 + 2 - TEXTURE_Y_OFFSET, RGB_YELLOW);
                this.parent.drawCenteredString(this.parent.fontRenderer, "S", width / 2 + 2, height / 2 + viewportSize / 2 - 9 - TEXTURE_Y_OFFSET, RGB_YELLOW);
                this.parent.drawCenteredString(this.parent.fontRenderer, "E", width / 2 + viewportSize / 2 - 4, height / 2 - 3 - TEXTURE_Y_OFFSET, RGB_YELLOW);
                this.parent.drawCenteredString(this.parent.fontRenderer, "W", width / 2 - viewportSize / 2 + 5, height / 2 - 3 - TEXTURE_Y_OFFSET, RGB_YELLOW);
            }
        }
        
        public void lerpAlpha(float partialTicks, float scale, float target) {
            this.mapAlpha = MathUtil.lerp(partialTicks * scale, this.mapAlpha, target);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static class StructureInfo {
        private final ResourceLocation structure;
        private float iconProgress;
        private float iconAlpha;
        
        public StructureInfo(ResourceLocation structure) {
            this.structure = structure;
            this.iconProgress = 1.0f;
            this.iconAlpha = 0.0f;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static class PreviewSettings {
        private int resolution;
        private boolean useBiomeBlend;
        private boolean showStructures;
        
        public PreviewSettings() {
            this.resolution = 512;
            this.useBiomeBlend = true;
            this.showStructures = false;
        }
        
        public PreviewSettings(int resolution, boolean useBiomeBlend, boolean showStructures) {
            this.resolution = resolution;
            this.useBiomeBlend = useBiomeBlend;
            this.showStructures = showStructures;
        }
    }
}
