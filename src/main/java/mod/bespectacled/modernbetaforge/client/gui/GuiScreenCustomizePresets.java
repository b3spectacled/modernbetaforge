package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
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
    private static final List<Info> PRESETS;
    private ListPreset list;
    private GuiButton select;
    private GuiTextField export;
    private final GuiCustomizeWorldScreen parent;
    protected String title;
    private String shareText;
    private String listText;
    
    public GuiScreenCustomizePresets(GuiCustomizeWorldScreen guiCustomizeWorldScreen) {
        this.title = "Customize World Presets";
        this.parent = guiCustomizeWorldScreen;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        
        this.title = I18n.format("createWorld.customize.custom.presets.title");
        this.shareText = I18n.format("createWorld.customize.presets.share");
        this.listText = I18n.format("createWorld.customize.presets.list");
        
        this.list = new ListPreset();
        
        this.export = new GuiTextField(2, this.fontRenderer, 50, 40, this.width - 100, 20);
        this.export.setMaxStringLength(2000);
        this.export.setText(this.parent.saveValues());
        
        this.select = this.<GuiButton>addButton(new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.presets.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel")));
        
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
        this.export.mouseClicked(mouseX, mouseY, clicked);
        
        super.mouseClicked(mouseX, mouseY, clicked);
    }
    
    @Override
    protected void keyTyped(char character, int integer) throws IOException {
        if (!this.export.textboxKeyTyped(character, integer)) {
            super.keyTyped(character, integer);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        switch (guiButton.id) {
            case 0:
                this.parent.loadValues(this.export.getText());
                this.mc.displayGuiScreen(this.parent);
                break;
            case 1:
                this.mc.displayGuiScreen(this.parent);
                break;
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
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
    
    private boolean hasValidSelection() {
        return (this.list.selected > -1 && this.list.selected < GuiScreenCustomizePresets.PRESETS.size()) || this.export.getText().length() > 1;
    }
    
    static {
        PRESETS = Lists.newArrayList();
        
        ModernBetaChunkGeneratorSettings.Factory factory;
        ResourceLocation resourceLocation;
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"beta\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":false,\"replaceBeachBiomes\":false,\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":64,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useRavines\":false,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useNewFlowers\":false,\"useLilyPads\":false,\"useMelons\":false,\"useDesertWells\":false,\"useFossils\":false}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/beta.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.beta"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"alpha\",\"biomeSource\":\"single\",\"replaceOceanBiomes\":false,\"replaceBeachBiomes\":false,\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":100.0,\"depthNoiseScaleZ\":100.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":64,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useRavines\":false,\"useWaterLakes\":false,\"waterLakeChance\":4,\"useLavaLakes\":false,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"modernbetaforge:alpha\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":0,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":false,\"useNewFlowers\":false,\"useLilyPads\":false,\"useMelons\":false,\"useDesertWells\":false,\"useFossils\":false}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/alpha.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.alpha"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"alpha\",\"biomeSource\":\"single\",\"replaceOceanBiomes\":false,\"replaceBeachBiomes\":false,\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":100.0,\"depthNoiseScaleZ\":100.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":64,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useRavines\":false,\"useWaterLakes\":false,\"waterLakeChance\":4,\"useLavaLakes\":false,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"modernbetaforge:alpha_winter\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":0,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":false,\"useNewFlowers\":false,\"useLilyPads\":false,\"useMelons\":false,\"useDesertWells\":false,\"useFossils\":false}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/alpha_winter.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.alphaWinter"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"infdev_415\",\"biomeSource\":\"single\",\"replaceOceanBiomes\":false,\"replaceBeachBiomes\":false,\"coordinateScale\":684.412,\"heightScale\":984.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":400.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":64,\"useCaves\":false,\"useDungeons\":false,\"dungeonChance\":8,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useRavines\":false,\"useWaterLakes\":false,\"waterLakeChance\":4,\"useLavaLakes\":false,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"modernbetaforge:infdev_415\",\"claySize\":33,\"clayCount\":0,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":0,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":0,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":10,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":0,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":0,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":false,\"useNewFlowers\":false,\"useLilyPads\":false,\"useMelons\":false,\"useDesertWells\":false,\"useFossils\":false}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/infdev.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.infdev"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"skylands\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":false,\"replaceBeachBiomes\":false,\"coordinateScale\":1368.824,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":0,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useRavines\":false,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":0,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":0,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":0,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useNewFlowers\":false,\"useLilyPads\":false,\"useMelons\":false,\"useDesertWells\":false,\"useFossils\":false}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/skylands.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.skylands"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"beta\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":true,\"replaceBeachBiomes\":true,\"coordinateScale\":3000.0,\"heightScale\":6000.0,\"lowerLimitScale\":512.0,\"upperLimitScale\":250.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":10.0,\"seaLevel\":64,\"height\":255,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useMansions\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":true,\"useNewFlowers\":true,\"useLilyPads\":true,\"useMelons\":true,\"useDesertWells\":true,\"useFossils\":true}");
        resourceLocation = new ResourceLocation("textures/gui/presets/isles.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.isleLandBeta"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"beta\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":true,\"replaceBeachBiomes\":true,\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":5000.0,\"mainNoiseScaleY\":1000.0,\"mainNoiseScaleZ\":5000.0,\"baseSize\":8.5,\"stretchY\":5.0,\"seaLevel\":64,\"height\":255,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useMansions\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":true,\"useNewFlowers\":true,\"useLilyPads\":true,\"useMelons\":true,\"useDesertWells\":true,\"useFossils\":true}");
        resourceLocation = new ResourceLocation("textures/gui/presets/delight.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.caveDelightBeta"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"beta\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":true,\"replaceBeachBiomes\":true,\"coordinateScale\":684.412,\"heightScale\":684.412,\"lowerLimitScale\":64.0,\"upperLimitScale\":2.0,\"depthNoiseScaleX\":200.0,\"depthNoiseScaleZ\":200.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":80.0,\"mainNoiseScaleY\":160.0,\"mainNoiseScaleZ\":80.0,\"baseSize\":8.5,\"stretchY\":12.0,\"seaLevel\":6,\"height\":255,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useMansions\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":true,\"useNewFlowers\":true,\"useLilyPads\":true,\"useMelons\":true,\"useDesertWells\":true,\"useFossils\":true}");
        resourceLocation = new ResourceLocation("textures/gui/presets/chaos.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.caveChaosBeta"), resourceLocation, factory));
        
        factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory("{\"chunkSource\":\"beta\",\"biomeSource\":\"beta\",\"replaceOceanBiomes\":true,\"replaceBeachBiomes\":true,\"coordinateScale\":171.103,\"heightScale\":342.206,\"lowerLimitScale\":512.0,\"upperLimitScale\":512.0,\"depthNoiseScaleX\":100.0,\"depthNoiseScaleZ\":100.0,\"depthNoiseScaleExponent\":0.5,\"mainNoiseScaleX\":5000.0,\"mainNoiseScaleY\":1000.0,\"mainNoiseScaleZ\":5000.0,\"baseSize\":8.5,\"stretchY\":2.5,\"seaLevel\":64,\"height\":255,\"useCaves\":true,\"useDungeons\":true,\"dungeonChance\":8,\"useStrongholds\":true,\"useVillages\":true,\"useMineShafts\":true,\"useTemples\":true,\"useMonuments\":true,\"useMansions\":true,\"useRavines\":true,\"useWaterLakes\":true,\"waterLakeChance\":4,\"useLavaLakes\":true,\"lavaLakeChance\":80,\"useLavaOceans\":false,\"fixedBiome\":\"minecraft:plains\",\"claySize\":33,\"clayCount\":10,\"clayMinHeight\":0,\"clayMaxHeight\":128,\"dirtSize\":33,\"dirtCount\":20,\"dirtMinHeight\":0,\"dirtMaxHeight\":128,\"gravelSize\":33,\"gravelCount\":10,\"gravelMinHeight\":0,\"gravelMaxHeight\":128,\"graniteSize\":33,\"graniteCount\":10,\"graniteMinHeight\":0,\"graniteMaxHeight\":80,\"dioriteSize\":33,\"dioriteCount\":10,\"dioriteMinHeight\":0,\"dioriteMaxHeight\":80,\"andesiteSize\":33,\"andesiteCount\":10,\"andesiteMinHeight\":0,\"andesiteMaxHeight\":80,\"coalSize\":17,\"coalCount\":20,\"coalMinHeight\":0,\"coalMaxHeight\":128,\"ironSize\":9,\"ironCount\":20,\"ironMinHeight\":0,\"ironMaxHeight\":64,\"goldSize\":9,\"goldCount\":2,\"goldMinHeight\":0,\"goldMaxHeight\":32,\"redstoneSize\":8,\"redstoneCount\":8,\"redstoneMinHeight\":0,\"redstoneMaxHeight\":16,\"diamondSize\":8,\"diamondCount\":1,\"diamondMinHeight\":0,\"diamondMaxHeight\":16,\"lapisSize\":7,\"lapisCount\":1,\"lapisCenterHeight\":16,\"lapisSpread\":16,\"useTallGrass\":true,\"useNewFlowers\":true,\"useLilyPads\":true,\"useMelons\":true,\"useDesertWells\":true,\"useFossils\":true,\"tempNoiseScale\":8.0,\"rainNoiseScale\":8.0,\"detailNoiseScale\":4.0}");
        resourceLocation = new ResourceLocation(ModernBeta.MODID, "textures/gui/presets/realistic.png");
        GuiScreenCustomizePresets.PRESETS.add(new Info(I18n.format("createWorld.customize.custom.preset.realisticBeta"), resourceLocation, factory));
    }
    
    @SideOnly(Side.CLIENT)
    class ListPreset extends GuiSlot {
        public int selected;
        
        public ListPreset() {
            super(GuiScreenCustomizePresets.this.mc, GuiScreenCustomizePresets.this.width, GuiScreenCustomizePresets.this.height, 80, GuiScreenCustomizePresets.this.height - 32, 38);
            this.selected = -1;
        }
        
        @Override
        protected int getSize() {
            return GuiScreenCustomizePresets.PRESETS.size();
        }
        
        @Override
        protected void elementClicked(int selected, boolean flag, int mouseX, int mouseY) {
            this.selected = selected;
            
            GuiScreenCustomizePresets.this.updateButtonValidity();
            GuiScreenCustomizePresets.this.export.setText(GuiScreenCustomizePresets.PRESETS.get(GuiScreenCustomizePresets.this.list.selected).settings.toString());
        }
        
        @Override
        protected boolean isSelected(int selected) {
            return selected == this.selected;
        }
        
        @Override
        protected void drawBackground() {
        }
        
        private void blitIcon(int x, int y, ResourceLocation resourceLocation) {
            int iX = x + 5;
            int iY = y;
            
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + 32, iY - 1, -2039584);
            GuiScreenCustomizePresets.this.drawHorizontalLine(iX - 1, iX + 32, iY + 32, -6250336);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX - 1, iY - 1, iY + 32, -2039584);
            GuiScreenCustomizePresets.this.drawVerticalLine(iX + 32, iY - 1, iY + 32, -6250336);
            
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(resourceLocation);
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferBuilder.pos(iX + 0, iY + 32, 0.0).tex(0.0, 1.0).endVertex();
            bufferBuilder.pos(iX + 32, iY + 32, 0.0).tex(1.0, 1.0).endVertex();
            bufferBuilder.pos(iX + 32, iY + 0, 0.0).tex(1.0, 0.0).endVertex();
            bufferBuilder.pos(iX + 0, iY + 0, 0.0).tex(0.0, 0.0).endVertex();
            
            tessellator.draw();
        }
        
        @Override
        protected void drawSlot(int preset, int x, int y, int paddingY, int mouseX, int mouseY, float partialTicks) {
            Info info = GuiScreenCustomizePresets.PRESETS.get(preset);
            
            this.blitIcon(x, y, info.texture);
            
            GuiScreenCustomizePresets.this.fontRenderer.drawString(info.name, x + 32 + 10, y + 14, 16777215);
        }
    }
    
    @SideOnly(Side.CLIENT)
    static class Info {
        public String name;
        public ResourceLocation texture;
        public ModernBetaChunkGeneratorSettings.Factory settings;
        
        public Info(String string, ResourceLocation resourceLocation, ModernBetaChunkGeneratorSettings.Factory factory) {
            this.name = string;
            this.texture = resourceLocation;
            this.settings = factory;
        }
    }
}
