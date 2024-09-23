package mod.bespectacled.modernbetaforge.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSourceType;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSourceType;
import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("deprecation")
@SideOnly(Side.CLIENT)
public class GuiCustomizeWorldScreen extends GuiScreen implements GuiSlider.FormatHelper, GuiPageButtonList.GuiResponder {
    private static final String PREFIX = "createWorld.customize.custom.";
    
    private static final int MAX_TEXT_LENGTH = 120;
    private static final int PAGELIST_ADDITIONAL_WIDTH = 96;
    private static final int BIOME_FIELD_ADDITIONAL_WIDTH = 36;
    
    private static final float MAX_HEIGHT = 255.0f;
    private static final float MIN_BIOME_SCALE = 0.1f;
    private static final float MAX_BIOME_SCALE = 8.0f;
    //private static final float MIN_OCEAN_SLIDE_TARGET = -1000.0f;
    //private static final float MAX_OCEAN_SLIDE_TARGET = 0.0f;
    
    private final GuiCreateWorld parent;
    
    protected String title;
    protected String subtitle;
    protected String pageTitle;
    protected String[] pageNames;
    
    private GuiPageButtonList pageList;
    private GuiButton done;
    private GuiButton randomize;
    private GuiButton defaults;
    private GuiButton previousPage;
    private GuiButton nextPage;
    private GuiButton confirm;
    private GuiButton cancel;
    private GuiButton presets;
    
    private boolean settingsModified;
    private int confirmMode;
    private boolean confirmDismissed;
    
    private final Predicate<String> floatFilter;
    private final Predicate<String> intFilter;
    private final Predicate<String> stringFilter;
    
    private final ModernBetaChunkGeneratorSettings.Factory defaultSettings;
    private ModernBetaChunkGeneratorSettings.Factory settings;
    
    private final Random random;

    public GuiCustomizeWorldScreen(GuiScreen guiScreen, String string) {
        this.title = "Customize World Settings";
        this.subtitle = "Page 1 of 5";
        this.pageTitle = "Basic Settings";
        this.pageNames = new String[5];
        
        this.floatFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Float entryValue = Floats.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && Floats.isFinite(entryValue) && entryValue >= 0.0f);
            }
        };
        
        this.intFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                Integer entryValue = Ints.tryParse(entryString);
                
                return entryString.isEmpty() || (entryValue != null && entryValue >= 0 && entryValue <= (int)MAX_HEIGHT);
            }
        };
        
        this.stringFilter = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String entryString) {
                return entryString.isEmpty() || entryString != null;
            }
        };
        
        this.defaultSettings = new ModernBetaChunkGeneratorSettings.Factory();
        this.random = new Random();
        this.parent = (GuiCreateWorld)guiScreen;
        
        this.loadValues(string);
    }
    
    private void createPagedList() {
        IForgeRegistry<Biome> biomes = ForgeRegistries.BIOMES;
        int biomeId = biomes.getValues().indexOf(biomes.getValue(new ResourceLocation(this.settings.fixedBiome)));
        
        int chunkSourceId = Arrays.asList(ChunkSourceType.values()).indexOf(ChunkSourceType.fromId(this.settings.chunkSource));
        int biomeSourceId = Arrays.asList(BiomeSourceType.values()).indexOf(BiomeSourceType.fromId(this.settings.biomeSource));
        
        GuiPageButtonList.GuiListEntry[] pageList0 = {
            new GuiPageButtonList.GuiSlideEntry(700, I18n.format(PREFIX + NbtTags.CHUNK_SOURCE), true, this, 0f, ChunkSourceType.values().length - 1, chunkSourceId),
            new GuiPageButtonList.GuiSlideEntry(701, I18n.format(PREFIX + NbtTags.BIOME_SOURCE), true, this, 0f, BiomeSourceType.values().length - 1, biomeSourceId),
            
            new GuiPageButtonList.GuiLabelEntry(702, I18n.format(PREFIX + "fixedBiomeLabel"), true),
            new GuiPageButtonList.GuiSlideEntry(162, I18n.format(PREFIX + "fixedBiome"), true, this, 0f, (float)(biomes.getValues().size() - 1), biomeId),            
    
            new GuiPageButtonList.GuiLabelEntry(510, I18n.format(PREFIX + "betaLabel"), true),
            null,
            new GuiPageButtonList.GuiButtonEntry(500, I18n.format(PREFIX + NbtTags.REPLACE_OCEAN_BIOMES), true, this.settings.replaceOceanBiomes),
            new GuiPageButtonList.GuiButtonEntry(501, I18n.format(PREFIX + NbtTags.REPLACE_BEACH_BIOMES), true, this.settings.replaceBeachBiomes),
            new GuiPageButtonList.GuiButtonEntry(502, I18n.format(PREFIX + NbtTags.USE_TALL_GRASS), true, this.settings.useTallGrass),
            new GuiPageButtonList.GuiButtonEntry(503, I18n.format(PREFIX + NbtTags.USE_NEW_FLOWERS), true, this.settings.useNewFlowers),
            new GuiPageButtonList.GuiButtonEntry(504, I18n.format(PREFIX + NbtTags.USE_LILY_PADS), true, this.settings.useLilyPads),
            new GuiPageButtonList.GuiButtonEntry(505, I18n.format(PREFIX + NbtTags.USE_MELONS), true, this.settings.useMelons),
            new GuiPageButtonList.GuiButtonEntry(506, I18n.format(PREFIX + NbtTags.USE_DESERT_WELLS), true, this.settings.useDesertWells),
            new GuiPageButtonList.GuiButtonEntry(507, I18n.format(PREFIX + NbtTags.USE_FOSSILS), true, this.settings.useFossils),
            
            new GuiPageButtonList.GuiLabelEntry(511, I18n.format(PREFIX + "otherLabel"), true),
            null,
            new GuiPageButtonList.GuiSlideEntry(160, I18n.format(PREFIX + "seaLevel"), true, this, 0.0f, MAX_HEIGHT, (float)this.settings.seaLevel),
            new GuiPageButtonList.GuiButtonEntry(148, I18n.format(PREFIX + "useCaves"), true, this.settings.useCaves),
            new GuiPageButtonList.GuiButtonEntry(150, I18n.format(PREFIX + "useStrongholds"), true, this.settings.useStrongholds),
            new GuiPageButtonList.GuiButtonEntry(151, I18n.format(PREFIX + "useVillages"), true, this.settings.useVillages),
            new GuiPageButtonList.GuiButtonEntry(152, I18n.format(PREFIX + "useMineShafts"), true, this.settings.useMineShafts),
            new GuiPageButtonList.GuiButtonEntry(153, I18n.format(PREFIX + "useTemples"), true, this.settings.useTemples),
            new GuiPageButtonList.GuiButtonEntry(210, I18n.format(PREFIX + "useMonuments"), true, this.settings.useMonuments),
            new GuiPageButtonList.GuiButtonEntry(211, I18n.format(PREFIX + "useMansions"), true, this.settings.useMansions),
            new GuiPageButtonList.GuiButtonEntry(154, I18n.format(PREFIX + "useRavines"), true, this.settings.useRavines),
            new GuiPageButtonList.GuiButtonEntry(149, I18n.format(PREFIX + "useDungeons"), true, this.settings.useDungeons),
            new GuiPageButtonList.GuiSlideEntry(157, I18n.format(PREFIX + "dungeonChance"), true, this, 1.0f, 100.0f, (float)this.settings.dungeonChance),
            new GuiPageButtonList.GuiButtonEntry(155, I18n.format(PREFIX + "useWaterLakes"), true, this.settings.useWaterLakes),
            new GuiPageButtonList.GuiSlideEntry(158, I18n.format(PREFIX + "waterLakeChance"), true, this, 1.0f, 100.0f, (float)this.settings.waterLakeChance),
            new GuiPageButtonList.GuiButtonEntry(156, I18n.format(PREFIX + "useLavaLakes"), true, this.settings.useLavaLakes),
            new GuiPageButtonList.GuiSlideEntry(159, I18n.format(PREFIX + "lavaLakeChance"), true, this, 10.0f, 100.0f, (float)this.settings.lavaLakeChance),
            new GuiPageButtonList.GuiButtonEntry(161, I18n.format(PREFIX + "useLavaOceans"), true, this.settings.useLavaOceans)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList1 = {
            new GuiPageButtonList.GuiLabelEntry(600, I18n.format("tile.clay.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(601, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.claySize),
            new GuiPageButtonList.GuiSlideEntry(602, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.clayCount),
            new GuiPageButtonList.GuiSlideEntry(603, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.clayMinHeight),
            new GuiPageButtonList.GuiSlideEntry(604, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.clayMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(416, I18n.format("tile.dirt.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(165, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.dirtSize),
            new GuiPageButtonList.GuiSlideEntry(166, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.dirtCount),
            new GuiPageButtonList.GuiSlideEntry(167, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dirtMinHeight),
            new GuiPageButtonList.GuiSlideEntry(168, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dirtMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(417, I18n.format("tile.gravel.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(169, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.gravelSize),
            new GuiPageButtonList.GuiSlideEntry(170, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.gravelCount),
            new GuiPageButtonList.GuiSlideEntry(171, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.gravelMinHeight),
            new GuiPageButtonList.GuiSlideEntry(172, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.gravelMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(418, I18n.format("tile.stone.granite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(173, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.graniteSize),
            new GuiPageButtonList.GuiSlideEntry(174, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.graniteCount),
            new GuiPageButtonList.GuiSlideEntry(175, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.graniteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(176, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.graniteMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(419, I18n.format("tile.stone.diorite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(177, I18n.format(PREFIX + "size"),false, this, 1.0f, 50.0f, (float)this.settings.dioriteSize),
            new GuiPageButtonList.GuiSlideEntry(178, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.dioriteCount),
            new GuiPageButtonList.GuiSlideEntry(179, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dioriteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(180, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.dioriteMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(420, I18n.format("tile.stone.andesite.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(181, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.andesiteSize),
            new GuiPageButtonList.GuiSlideEntry(182, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.andesiteCount),
            new GuiPageButtonList.GuiSlideEntry(183, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.andesiteMinHeight),
            new GuiPageButtonList.GuiSlideEntry(184, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.andesiteMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(421, I18n.format("tile.oreCoal.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(185, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.coalSize),
            new GuiPageButtonList.GuiSlideEntry(186, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.coalCount),
            new GuiPageButtonList.GuiSlideEntry(187, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.coalMinHeight),
            new GuiPageButtonList.GuiSlideEntry(189, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.coalMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(422, I18n.format("tile.oreIron.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(190, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.ironSize),
            new GuiPageButtonList.GuiSlideEntry(191, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.ironCount),
            new GuiPageButtonList.GuiSlideEntry(192, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.ironMinHeight),
            new GuiPageButtonList.GuiSlideEntry(193, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.ironMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(423, I18n.format("tile.oreGold.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(194, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.goldSize),
            new GuiPageButtonList.GuiSlideEntry(195, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.goldCount),
            new GuiPageButtonList.GuiSlideEntry(196, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.goldMinHeight),
            new GuiPageButtonList.GuiSlideEntry(197, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.goldMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(424, I18n.format("tile.oreRedstone.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(198, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.redstoneSize),
            new GuiPageButtonList.GuiSlideEntry(199, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.redstoneCount),
            new GuiPageButtonList.GuiSlideEntry(200, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.redstoneMinHeight),
            new GuiPageButtonList.GuiSlideEntry(201, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.redstoneMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(425, I18n.format("tile.oreDiamond.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(202, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.diamondSize),
            new GuiPageButtonList.GuiSlideEntry(203, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.diamondCount),
            new GuiPageButtonList.GuiSlideEntry(204, I18n.format(PREFIX + "minHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.diamondMinHeight),
            new GuiPageButtonList.GuiSlideEntry(205, I18n.format(PREFIX + "maxHeight"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.diamondMaxHeight),
            new GuiPageButtonList.GuiLabelEntry(426, I18n.format("tile.oreLapis.name"), false),
            null,
            new GuiPageButtonList.GuiSlideEntry(206, I18n.format(PREFIX + "size"), false, this, 1.0f, 50.0f, (float)this.settings.lapisSize),
            new GuiPageButtonList.GuiSlideEntry(207, I18n.format(PREFIX + "count"), false, this, 0.0f, 40.0f, (float)this.settings.lapisCount),
            new GuiPageButtonList.GuiSlideEntry(208, I18n.format(PREFIX + "center"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.lapisCenterHeight),
            new GuiPageButtonList.GuiSlideEntry(209, I18n.format(PREFIX + "spread"), false, this, 0.0f, MAX_HEIGHT, (float)this.settings.lapisSpread)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList2 = {
            new GuiPageButtonList.GuiSlideEntry(100, I18n.format(PREFIX + "mainNoiseScaleX"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleX),
            new GuiPageButtonList.GuiSlideEntry(101, I18n.format(PREFIX + "mainNoiseScaleY"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleY),
            new GuiPageButtonList.GuiSlideEntry(102, I18n.format(PREFIX + "mainNoiseScaleZ"), false, this, 1.0f, 5000.0f, this.settings.mainNoiseScaleZ),
            new GuiPageButtonList.GuiSlideEntry(103, I18n.format(PREFIX + "depthNoiseScaleX"), false, this, 1.0f, 2000.0f, this.settings.depthNoiseScaleX),
            new GuiPageButtonList.GuiSlideEntry(104, I18n.format(PREFIX + "depthNoiseScaleZ"), false, this, 1.0f, 2000.0f, this.settings.depthNoiseScaleZ),
            new GuiPageButtonList.GuiSlideEntry(105, I18n.format(PREFIX + "depthNoiseScaleExponent"), false, this, 0.01f, 20.0f, this.settings.depthNoiseScaleExponent),
            new GuiPageButtonList.GuiSlideEntry(106, I18n.format(PREFIX + "baseSize"), false, this, 1.0f, 25.0f, this.settings.baseSize),
            new GuiPageButtonList.GuiSlideEntry(107, I18n.format(PREFIX + "coordinateScale"), false, this, 1.0f, 6000.0f, this.settings.coordinateScale),
            new GuiPageButtonList.GuiSlideEntry(108, I18n.format(PREFIX + "heightScale"), false, this, 1.0f, 6000.0f, this.settings.heightScale),
            new GuiPageButtonList.GuiSlideEntry(109, I18n.format(PREFIX + "stretchY"), false, this, 0.01f, 50.0f, this.settings.stretchY),
            new GuiPageButtonList.GuiSlideEntry(110, I18n.format(PREFIX + "upperLimitScale"), false, this, 1.0f, 5000.0f, this.settings.upperLimitScale),
            new GuiPageButtonList.GuiSlideEntry(111, I18n.format(PREFIX + "lowerLimitScale"), false, this, 1.0f, 5000.0f, this.settings.lowerLimitScale),
            new GuiPageButtonList.GuiSlideEntry(113, I18n.format(PREFIX + NbtTags.TEMP_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.tempNoiseScale),
            new GuiPageButtonList.GuiSlideEntry(114, I18n.format(PREFIX + NbtTags.RAIN_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.rainNoiseScale),
            new GuiPageButtonList.GuiSlideEntry(115, I18n.format(PREFIX + NbtTags.DETAIL_NOISE_SCALE), false, this, MIN_BIOME_SCALE, MAX_BIOME_SCALE, this.settings.detailNoiseScale),
            new GuiPageButtonList.GuiSlideEntry(112, I18n.format(PREFIX + NbtTags.HEIGHT), false, this, 1.0f, MAX_HEIGHT, this.settings.height)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList3 = {
            new GuiPageButtonList.GuiLabelEntry(400, I18n.format(PREFIX + "mainNoiseScaleX") + ":", false),
            new GuiPageButtonList.EditBoxEntry(132, String.format("%5.3f", this.settings.mainNoiseScaleX), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(401, I18n.format(PREFIX + "mainNoiseScaleY") + ":", false),
            new GuiPageButtonList.EditBoxEntry(133, String.format("%5.3f", this.settings.mainNoiseScaleY), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(402, I18n.format(PREFIX + "mainNoiseScaleZ") + ":", false),
            new GuiPageButtonList.EditBoxEntry(134, String.format("%5.3f", this.settings.mainNoiseScaleZ), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(403, I18n.format(PREFIX + "depthNoiseScaleX") + ":", false),
            new GuiPageButtonList.EditBoxEntry(135, String.format("%5.3f", this.settings.depthNoiseScaleX), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(404, I18n.format(PREFIX + "depthNoiseScaleZ") + ":", false),
            new GuiPageButtonList.EditBoxEntry(136, String.format("%5.3f", this.settings.depthNoiseScaleZ), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(405, I18n.format(PREFIX + "depthNoiseScaleExponent") + ":", false),
            new GuiPageButtonList.EditBoxEntry(137, String.format("%2.3f", this.settings.depthNoiseScaleExponent), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(406, I18n.format(PREFIX + "baseSize") + ":", false),
            new GuiPageButtonList.EditBoxEntry(138, String.format("%2.3f", this.settings.baseSize), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(407, I18n.format(PREFIX + "coordinateScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(139, String.format("%5.3f", this.settings.coordinateScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(408, I18n.format(PREFIX + "heightScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(140, String.format("%5.3f", this.settings.heightScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(409, I18n.format(PREFIX + "stretchY") + ":", false),
            new GuiPageButtonList.EditBoxEntry(141, String.format("%2.3f", this.settings.stretchY), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(410, I18n.format(PREFIX + "upperLimitScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(142, String.format("%5.3f", this.settings.upperLimitScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(411, I18n.format(PREFIX + "lowerLimitScale") + ":", false),
            new GuiPageButtonList.EditBoxEntry(143, String.format("%5.3f", this.settings.lowerLimitScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(413, I18n.format(PREFIX + NbtTags.TEMP_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(145, String.format("%2.3f", this.settings.tempNoiseScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(414, I18n.format(PREFIX + NbtTags.RAIN_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(146, String.format("%2.3f", this.settings.rainNoiseScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(415, I18n.format(PREFIX + NbtTags.DETAIL_NOISE_SCALE) + ":", false),
            new GuiPageButtonList.EditBoxEntry(147, String.format("%2.3f", this.settings.detailNoiseScale), false, this.floatFilter),
            new GuiPageButtonList.GuiLabelEntry(412, I18n.format(PREFIX + NbtTags.HEIGHT) + ":", false),
            new GuiPageButtonList.EditBoxEntry(144, String.format("%d", this.settings.height), false, this.intFilter)
        };
        
        GuiPageButtonList.GuiListEntry[] pageList4 = {
            new GuiPageButtonList.GuiLabelEntry(512, I18n.format(PREFIX + NbtTags.DESERT_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(900, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(800, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(901, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(801, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(902, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(802, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(513, I18n.format(PREFIX + NbtTags.FOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(903, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(803, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(904, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(804, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(905, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(805, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(514, I18n.format(PREFIX + NbtTags.ICE_DESERT_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(906, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(806, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(907, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(807, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(908, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(808, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(515, I18n.format(PREFIX + NbtTags.PLAINS_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(909, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(809, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(910, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(810, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(911, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(811, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(516, I18n.format(PREFIX + NbtTags.RAINFOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(912, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(812, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(913, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(813, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(914, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(814, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(517, I18n.format(PREFIX + NbtTags.SAVANNA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(915, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(815, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(916, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(816, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(917, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(817, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(518, I18n.format(PREFIX + NbtTags.SHRUBLAND_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(918, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(818, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(919, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(819, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(920, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(820, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(519, I18n.format(PREFIX + NbtTags.SEASONAL_FOREST_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(921, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(821, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(922, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(822, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(923, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(823, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(520, I18n.format(PREFIX + NbtTags.SWAMPLAND_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(924, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(824, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(925, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(825, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(926, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(826, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(521, I18n.format(PREFIX + NbtTags.TAIGA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(927, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(827, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(928, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(828, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(929, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(829, "", false, this.stringFilter),
            
            new GuiPageButtonList.GuiLabelEntry(522, I18n.format(PREFIX + NbtTags.TUNDRA_BIOMES), true),
            null,
            new GuiPageButtonList.GuiLabelEntry(930, I18n.format(PREFIX + NbtTags.LAND_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(830, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(931, I18n.format(PREFIX + NbtTags.OCEAN_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(831, "", false, this.stringFilter),
            new GuiPageButtonList.GuiLabelEntry(932, I18n.format(PREFIX + NbtTags.BEACH_BIOME) + ":", false),
            new GuiPageButtonList.EditBoxEntry(832, "", false, this.stringFilter)
        };
        
        this.pageList = new GuiPageButtonList(
            this.mc,
            this.width,
            this.height,
            32,
            this.height - 32,
            25,
            this,
            new GuiPageButtonList.GuiListEntry[][] {
                pageList0,
                pageList1,
                pageList2,
                pageList3,
                pageList4
            }
        );
        
        this.pageList.width += PAGELIST_ADDITIONAL_WIDTH;
        
        // Increase text length for biome fields
        this.increaseMaxTextLength(this.pageList, 800);
        this.increaseMaxTextLength(this.pageList, 801);
        this.increaseMaxTextLength(this.pageList, 802);
        
        this.increaseMaxTextLength(this.pageList, 803);
        this.increaseMaxTextLength(this.pageList, 804);
        this.increaseMaxTextLength(this.pageList, 805);
        
        this.increaseMaxTextLength(this.pageList, 806);
        this.increaseMaxTextLength(this.pageList, 807);
        this.increaseMaxTextLength(this.pageList, 808);
        
        this.increaseMaxTextLength(this.pageList, 809);
        this.increaseMaxTextLength(this.pageList, 810);
        this.increaseMaxTextLength(this.pageList, 811);
        
        this.increaseMaxTextLength(this.pageList, 812);
        this.increaseMaxTextLength(this.pageList, 813);
        this.increaseMaxTextLength(this.pageList, 814);
        
        this.increaseMaxTextLength(this.pageList, 815);
        this.increaseMaxTextLength(this.pageList, 816);
        this.increaseMaxTextLength(this.pageList, 817);
        
        this.increaseMaxTextLength(this.pageList, 818);
        this.increaseMaxTextLength(this.pageList, 819);
        this.increaseMaxTextLength(this.pageList, 820);
        
        this.increaseMaxTextLength(this.pageList, 821);
        this.increaseMaxTextLength(this.pageList, 822);
        this.increaseMaxTextLength(this.pageList, 823);
        
        this.increaseMaxTextLength(this.pageList, 824);
        this.increaseMaxTextLength(this.pageList, 825);
        this.increaseMaxTextLength(this.pageList, 826);
        
        this.increaseMaxTextLength(this.pageList, 827);
        this.increaseMaxTextLength(this.pageList, 828);
        this.increaseMaxTextLength(this.pageList, 829);
        
        this.increaseMaxTextLength(this.pageList, 830);
        this.increaseMaxTextLength(this.pageList, 831);
        this.increaseMaxTextLength(this.pageList, 832);
        
        // Set text here instead of at instantiation, so updated text length is utilized
        this.setInitialText(this.pageList, 800, this.settings.desertBiomes.landBiome);
        this.setInitialText(this.pageList, 801, this.settings.desertBiomes.oceanBiome);
        this.setInitialText(this.pageList, 802, this.settings.desertBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 803, this.settings.forestBiomes.landBiome);
        this.setInitialText(this.pageList, 804, this.settings.forestBiomes.oceanBiome);
        this.setInitialText(this.pageList, 805, this.settings.forestBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 806, this.settings.iceDesertBiomes.landBiome);
        this.setInitialText(this.pageList, 807, this.settings.iceDesertBiomes.oceanBiome);
        this.setInitialText(this.pageList, 808, this.settings.iceDesertBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 809, this.settings.plainsBiomes.landBiome);
        this.setInitialText(this.pageList, 810, this.settings.plainsBiomes.oceanBiome);
        this.setInitialText(this.pageList, 811, this.settings.plainsBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 812, this.settings.rainforestBiomes.landBiome);
        this.setInitialText(this.pageList, 813, this.settings.rainforestBiomes.oceanBiome);
        this.setInitialText(this.pageList, 814, this.settings.rainforestBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 815, this.settings.savannaBiomes.landBiome);
        this.setInitialText(this.pageList, 816, this.settings.savannaBiomes.oceanBiome);
        this.setInitialText(this.pageList, 817, this.settings.savannaBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 818, this.settings.shrublandBiomes.landBiome);
        this.setInitialText(this.pageList, 819, this.settings.shrublandBiomes.oceanBiome);
        this.setInitialText(this.pageList, 820, this.settings.shrublandBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 821, this.settings.seasonalForestBiomes.landBiome);
        this.setInitialText(this.pageList, 822, this.settings.seasonalForestBiomes.oceanBiome);
        this.setInitialText(this.pageList, 823, this.settings.seasonalForestBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 824, this.settings.swamplandBiomes.landBiome);
        this.setInitialText(this.pageList, 825, this.settings.swamplandBiomes.oceanBiome);
        this.setInitialText(this.pageList, 826, this.settings.swamplandBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 827, this.settings.taigaBiomes.landBiome);
        this.setInitialText(this.pageList, 828, this.settings.taigaBiomes.oceanBiome);
        this.setInitialText(this.pageList, 829, this.settings.taigaBiomes.beachBiome);
        
        this.setInitialText(this.pageList, 830, this.settings.tundraBiomes.landBiome);
        this.setInitialText(this.pageList, 831, this.settings.tundraBiomes.oceanBiome);
        this.setInitialText(this.pageList, 832, this.settings.tundraBiomes.beachBiome);
        
        for (int page = 0; page < 5; ++page) {
            this.pageNames[page] = I18n.format(PREFIX + "page" + page);
        }
        
        this.updatePageControls();
    }

    @Override
    public void initGui() {
        int curPage = 0;
        int curScroll = 0;
        
        if (this.pageList != null) {
            curPage = this.pageList.getPage();
            curScroll = this.pageList.getAmountScrolled();
        }
        
        this.title = I18n.format("options.customizeTitle");
        this.buttonList.clear();
        
        this.previousPage = this.<GuiButton>addButton(new GuiButton(302, 20, 5, 80, 20, I18n.format(PREFIX + "prev")));
        this.nextPage = this.<GuiButton>addButton(new GuiButton(303, this.width - 100, 5, 80, 20, I18n.format(PREFIX + "next")));
        this.defaults = this.<GuiButton>addButton(new GuiButton(304, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format(PREFIX + "defaults")));
        this.randomize = this.<GuiButton>addButton(new GuiButton(301, this.width / 2 - 92, this.height - 27, 90, 20, I18n.format(PREFIX + "randomize")));
        this.presets = this.<GuiButton>addButton(new GuiButton(305, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format(PREFIX + "presets")));
        this.done = this.<GuiButton>addButton(new GuiButton(300, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done")));
        
        this.defaults.enabled = this.settingsModified;
        
        this.confirm = new GuiButton(306, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
        this.confirm.visible = false;
        
        this.cancel = new GuiButton(307, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
        this.cancel.visible = false;

        this.buttonList.add(this.confirm);
        this.buttonList.add(this.cancel);
        
        if (this.confirmMode != 0) {
            this.confirm.visible = true;
            this.cancel.visible = true;
        }
        
        this.createPagedList();
        
        if (curPage != 0) {
            this.pageList.setPage(curPage);
            this.pageList.scrollBy(curScroll);
            this.updatePageControls();
        }
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.pageList.handleMouseInput();
    }
    
    @Override
    public String getText(int entry, String entryString, float entryValue) {
        return entryString + ": " + this.getFormattedValue(entry, entryValue);
    }

    @Override
    public void setEntryValue(int entry, String string) {
        if (entry >= 800 && entry <= 832) {
            String entryBiome = string;
            
            String newEntryBiome = "";
            switch (entry) {
                case 800:
                    this.settings.desertBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.desertBiomes.landBiome;
                    break;
                case 801:
                    this.settings.desertBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.desertBiomes.oceanBiome;
                    break;
                case 802:
                    this.settings.desertBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.desertBiomes.beachBiome;
                    break;
                    
                case 803:
                    this.settings.forestBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.forestBiomes.landBiome;
                    break;
                case 804:
                    this.settings.forestBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.forestBiomes.oceanBiome;
                    break;
                case 805:
                    this.settings.forestBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.forestBiomes.beachBiome;
                    break;
                    
                case 806:
                    this.settings.iceDesertBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.iceDesertBiomes.landBiome;
                    break;
                case 807:
                    this.settings.iceDesertBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.iceDesertBiomes.oceanBiome;
                    break;
                case 808:
                    this.settings.iceDesertBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.iceDesertBiomes.beachBiome;
                    break;
                    
                case 809:
                    this.settings.plainsBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.plainsBiomes.landBiome;
                    break;
                case 810:
                    this.settings.plainsBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.plainsBiomes.oceanBiome;
                    break;
                case 811:
                    this.settings.plainsBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.plainsBiomes.beachBiome;
                    break;
                    
                case 812:
                    this.settings.rainforestBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.rainforestBiomes.landBiome;
                    break;
                case 813:
                    this.settings.rainforestBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.rainforestBiomes.oceanBiome;
                    break;
                case 814:
                    this.settings.rainforestBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.rainforestBiomes.beachBiome;
                    break;
                    
                case 815:
                    this.settings.savannaBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.savannaBiomes.landBiome;
                    break;
                case 816:
                    this.settings.savannaBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.savannaBiomes.oceanBiome;
                    break;
                case 817:
                    this.settings.savannaBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.savannaBiomes.beachBiome;
                    break;
                    
                case 818:
                    this.settings.shrublandBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.shrublandBiomes.landBiome;
                    break;
                case 819:
                    this.settings.shrublandBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.shrublandBiomes.oceanBiome;
                    break;
                case 820:
                    this.settings.shrublandBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.shrublandBiomes.beachBiome;
                    break;
                    
                case 821:
                    this.settings.seasonalForestBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.seasonalForestBiomes.landBiome;
                    break;
                case 822:
                    this.settings.seasonalForestBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.seasonalForestBiomes.oceanBiome;
                    break;
                case 823:
                    this.settings.seasonalForestBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.seasonalForestBiomes.beachBiome;
                    break;
                    
                case 824:
                    this.settings.swamplandBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.swamplandBiomes.landBiome;
                    break;
                case 825:
                    this.settings.swamplandBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.swamplandBiomes.oceanBiome;
                    break;
                case 826:
                    this.settings.swamplandBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.swamplandBiomes.beachBiome;
                    break;
                    
                case 827:
                    this.settings.taigaBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.taigaBiomes.landBiome;
                    break;
                case 828:
                    this.settings.taigaBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.taigaBiomes.oceanBiome;
                    break;
                case 829:
                    this.settings.taigaBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.taigaBiomes.beachBiome;
                    break;
                    
                case 830:
                    this.settings.tundraBiomes.landBiome = entryBiome;
                    newEntryBiome = this.settings.tundraBiomes.landBiome;
                    break;
                case 831:
                    this.settings.tundraBiomes.oceanBiome = entryBiome;
                    newEntryBiome = this.settings.tundraBiomes.oceanBiome;
                    break;
                case 832:
                    this.settings.tundraBiomes.beachBiome = entryBiome;
                    newEntryBiome = this.settings.tundraBiomes.beachBiome;
                    break;
            }
            
            if (newEntryBiome.equals(entryBiome)) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(newEntryBiome);
            }
        } else {
            float entryValue = 0.0f;
            
            try {
                entryValue = Float.parseFloat(string);
                
            } catch (NumberFormatException ex) {}
            
            float newEntryValue = 0.0f;
            switch (entry) {
                case 139:
                    this.settings.coordinateScale = MathHelper.clamp(entryValue, 1.0f, 6000.0f);
                    newEntryValue = this.settings.coordinateScale;
                    break;
                case 140:
                    this.settings.heightScale = MathHelper.clamp(entryValue, 1.0f, 6000.0f);
                    newEntryValue = this.settings.heightScale;
                    break;
                case 142:
                    this.settings.upperLimitScale = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.upperLimitScale;
                    break;
                case 143:
                    this.settings.lowerLimitScale = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.lowerLimitScale;
                    break;
                case 144:
                    this.settings.height = (int)MathHelper.clamp(entryValue, 1.0f, MAX_HEIGHT);
                    newEntryValue = this.settings.height;
                    break;
                case 145:
                    this.settings.tempNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.tempNoiseScale;
                    break;
                case 146:
                    this.settings.rainNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.rainNoiseScale;
                    break;
                case 147:
                    this.settings.detailNoiseScale = MathHelper.clamp(entryValue, MIN_BIOME_SCALE, MAX_BIOME_SCALE);
                    newEntryValue = this.settings.detailNoiseScale;
                    break;
                case 135:
                    this.settings.depthNoiseScaleX = MathHelper.clamp(entryValue, 1.0f, 2000.0f);
                    newEntryValue = this.settings.depthNoiseScaleX;
                    break;
                case 136:
                    this.settings.depthNoiseScaleZ = MathHelper.clamp(entryValue, 1.0f, 2000.0f);
                    newEntryValue = this.settings.depthNoiseScaleZ;
                    break;
                case 137:
                    this.settings.depthNoiseScaleExponent = MathHelper.clamp(entryValue, 0.01f, 20.0f);
                    newEntryValue = this.settings.depthNoiseScaleExponent;
                    break;
                case 132:
                    this.settings.mainNoiseScaleX = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleX;
                    break;
                case 133:
                    this.settings.mainNoiseScaleY = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleY;
                    break;
                case 134:
                    this.settings.mainNoiseScaleZ = MathHelper.clamp(entryValue, 1.0f, 5000.0f);
                    newEntryValue = this.settings.mainNoiseScaleZ;
                    break;
                case 138:
                    this.settings.baseSize = MathHelper.clamp(entryValue, 1.0f, 25.0f);
                    newEntryValue = this.settings.baseSize;
                    break;
                case 141:
                    this.settings.stretchY = MathHelper.clamp(entryValue, 0.01f, 50.0f);
                    newEntryValue = this.settings.stretchY;
                    break;
            }
            
            if (newEntryValue != entryValue && entryValue != 0.0f) {
                ((GuiTextField)this.pageList.getComponent(entry)).setText(this.getFormattedValue(entry, newEntryValue));
            }
            
            ((GuiSlider)this.pageList.getComponent(entry - 132 + 100)).setSliderValue(newEntryValue, false);
        }
        
        if (!this.settings.equals(this.defaultSettings)) {
            this.setSettingsModified(true);
        }
    }

    @Override
    public void setEntryValue(int integer, boolean entryValue) {
        switch (integer) {
            case 148:
                this.settings.useCaves = entryValue;
                break;
            case 149:
                this.settings.useDungeons = entryValue;
                break;
            case 150:
                this.settings.useStrongholds = entryValue;
                break;
            case 151:
                this.settings.useVillages = entryValue;
                break;
            case 152:
                this.settings.useMineShafts = entryValue;
                break;
            case 153:
                this.settings.useTemples = entryValue;
                break;
            case 154:
                this.settings.useRavines = entryValue;
                break;
            case 210:
                this.settings.useMonuments = entryValue;
                break;
            case 211:
                this.settings.useMansions = entryValue;
                break;
            case 155:
                this.settings.useWaterLakes = entryValue;
                break;
            case 156:
                this.settings.useLavaLakes = entryValue;
                break;
            case 161:
                this.settings.useLavaOceans = entryValue;
                break;
    
            case 500:
                this.settings.replaceOceanBiomes = entryValue;
                break;
            case 501:
                this.settings.replaceBeachBiomes = entryValue;
                break;
                
            case 502:
                this.settings.useTallGrass = entryValue;
                break;
            case 503:
                this.settings.useNewFlowers = entryValue;
                break;
            case 504:
                this.settings.useLilyPads = entryValue;
                break;
            case 505:
                this.settings.useMelons = entryValue;
                break;
            case 506:
                this.settings.useDesertWells = entryValue;
                break;
            case 507:
                this.settings.useFossils = entryValue;
                break;
            
        }
        
        if (!this.settings.equals(this.defaultSettings)) {
            this.setSettingsModified(true);
        }
    }

    @Override
    public void setEntryValue(int entry, float entryValue) {
        switch (entry) {
            case 107:
                this.settings.coordinateScale = entryValue;
                break;
            case 108:
                this.settings.heightScale = entryValue;
                break;
            case 110:
                this.settings.upperLimitScale = entryValue;
                break;
            case 111:
                this.settings.lowerLimitScale = entryValue;
                break;
            case 112:
                this.settings.height = (int)entryValue;
                break;
            case 113:
                this.settings.tempNoiseScale = entryValue;
                break;
            case 114:
                this.settings.rainNoiseScale = entryValue;
                break;
            case 115:
                this.settings.detailNoiseScale = entryValue;
                break;
            case 103:
                this.settings.depthNoiseScaleX = entryValue;
                break;
            case 104:
                this.settings.depthNoiseScaleZ = entryValue;
                break;
            case 105:
                this.settings.depthNoiseScaleExponent = entryValue;
                break;
            case 100:
                this.settings.mainNoiseScaleX = entryValue;
                break;
            case 101:
                this.settings.mainNoiseScaleY = entryValue;
                break;
            case 102:
                this.settings.mainNoiseScaleZ = entryValue;
                break;
            case 106:
                this.settings.baseSize = entryValue;
                break;
            case 109:
                this.settings.stretchY = entryValue;
                break;
            case 157:
                this.settings.dungeonChance = (int)entryValue;
                break;
            case 158:
                this.settings.waterLakeChance = (int)entryValue;
                break;
            case 159:
                this.settings.lavaLakeChance = (int)entryValue;
                break;
            case 160:
                this.settings.seaLevel = (int)entryValue;
                break;
            case 162:
                this.settings.fixedBiome = ForgeRegistries.BIOMES.getValues().get((int)entryValue).getRegistryName().toString();
                break;
            case 166:
                this.settings.dirtCount = (int)entryValue;
                break;
            case 165:
                this.settings.dirtSize = (int)entryValue;
                break;
            case 167:
                this.settings.dirtMinHeight = (int)entryValue;
                break;
            case 168:
                this.settings.dirtMaxHeight = (int)entryValue;
                break;
            case 170:
                this.settings.gravelCount = (int)entryValue;
                break;
            case 169:
                this.settings.gravelSize = (int)entryValue;
                break;
            case 171:
                this.settings.gravelMinHeight = (int)entryValue;
                break;
            case 172:
                this.settings.gravelMaxHeight = (int)entryValue;
                break;
            case 174:
                this.settings.graniteCount = (int)entryValue;
                break;
            case 173:
                this.settings.graniteSize = (int)entryValue;
                break;
            case 175:
                this.settings.graniteMinHeight = (int)entryValue;
                break;
            case 176:
                this.settings.graniteMaxHeight = (int)entryValue;
                break;
            case 178:
                this.settings.dioriteCount = (int)entryValue;
                break;
            case 177:
                this.settings.dioriteSize = (int)entryValue;
                break;
            case 179:
                this.settings.dioriteMinHeight = (int)entryValue;
                break;
            case 180:
                this.settings.dioriteMaxHeight = (int)entryValue;
                break;
            case 182:
                this.settings.andesiteCount = (int)entryValue;
                break;
            case 181:
                this.settings.andesiteSize = (int)entryValue;
                break;
            case 183:
                this.settings.andesiteMinHeight = (int)entryValue;
                break;
            case 184:
                this.settings.andesiteMaxHeight = (int)entryValue;
                break;
            case 186:
                this.settings.coalCount = (int)entryValue;
                break;
            case 185:
                this.settings.coalSize = (int)entryValue;
                break;
            case 187:
                this.settings.coalMinHeight = (int)entryValue;
                break;
            case 189:
                this.settings.coalMaxHeight = (int)entryValue;
                break;
            case 191:
                this.settings.ironCount = (int)entryValue;
                break;
            case 190:
                this.settings.ironSize = (int)entryValue;
                break;
            case 192:
                this.settings.ironMinHeight = (int)entryValue;
                break;
            case 193:
                this.settings.ironMaxHeight = (int)entryValue;
                break;
            case 195:
                this.settings.goldCount = (int)entryValue;
                break;
            case 194:
                this.settings.goldSize = (int)entryValue;
                break;
            case 196:
                this.settings.goldMinHeight = (int)entryValue;
                break;
            case 197:
                this.settings.goldMaxHeight = (int)entryValue;
                break;
            case 199:
                this.settings.redstoneCount = (int)entryValue;
                break;
            case 198:
                this.settings.redstoneSize = (int)entryValue;
                break;
            case 200:
                this.settings.redstoneMinHeight = (int)entryValue;
                break;
            case 201:
                this.settings.redstoneMaxHeight = (int)entryValue;
                break;
            case 203:
                this.settings.diamondCount = (int)entryValue;
                break;
            case 202:
                this.settings.diamondSize = (int)entryValue;
                break;
            case 204:
                this.settings.diamondMinHeight = (int)entryValue;
                break;
            case 205:
                this.settings.diamondMaxHeight = (int)entryValue;
                break;
            case 207:
                this.settings.lapisCount = (int)entryValue;
                break;
            case 206:
                this.settings.lapisSize = (int)entryValue;
                break;
            case 208:
                this.settings.lapisCenterHeight = (int)entryValue;
                break;
            case 209:
                this.settings.lapisSpread = (int)entryValue;
                break;
            
            case 602:
                this.settings.clayCount = (int)entryValue;
                break;
            case 601:
                this.settings.claySize = (int)entryValue;
                break;
            case 603:
                this.settings.clayMinHeight = (int)entryValue;
                break;
            case 604:
                this.settings.clayMaxHeight = (int)entryValue;
                break;
                
            case 700:
                this.settings.chunkSource = ChunkSourceType.values()[(int)entryValue].getId();
                break;
            case 701:
                this.settings.biomeSource = BiomeSourceType.values()[(int)entryValue].getId();
                break;
        }
        
        if (entry >= 100 && entry < 116) {
            Gui gui = this.pageList.getComponent(entry - 100 + 132);
            if (gui != null) {
                ((GuiTextField)gui).setText(this.getFormattedValue(entry, entryValue));
            }
        }
        
        if (!this.settings.equals(this.defaultSettings)) {
            this.setSettingsModified(true);
        }
    }

    public String saveValues() {
        return this.settings.toString().replace("\n", "");
    }

    public void loadValues(String string) {
        if (string != null && !string.isEmpty()) {
            this.settings = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(string);
        } else {
            this.settings = new ModernBetaChunkGeneratorSettings.Factory();
        }
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException {
        if (!guiButton.enabled) {
            return;
        }
        
        switch (guiButton.id) {
            case 300:
                this.parent.chunkProviderSettingsJson = this.settings.toString();
                this.mc.displayGuiScreen(this.parent);
                break;
            case 305:
                this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this)); //TODO: FIX
                break;
            case 301: // Randomize
                for (int page = 0; page < this.pageList.getSize(); ++page) {
                    GuiPageButtonList.GuiEntry guiEntry = this.pageList.getListEntry(page);
                    Gui guiComponent = guiEntry.getComponent1();
                    
                    if (guiComponent instanceof GuiButton) {
                        GuiButton guiButtonComponent = (GuiButton)guiComponent;
                        
                        if (guiButtonComponent instanceof GuiSlider) {
                            float randomFloat = ((GuiSlider)guiButtonComponent).getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                            ((GuiSlider)guiButtonComponent).setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                            
                        } else if (guiButtonComponent instanceof GuiListButton) {
                            ((GuiListButton)guiButtonComponent).setValue(this.random.nextBoolean());
                        }
                    }
                    
                    guiComponent = guiEntry.getComponent2();
                    if (guiComponent instanceof GuiButton) {
                        GuiButton guiButtonComponent = (GuiButton)guiComponent;
                        
                        if (guiButtonComponent instanceof GuiSlider) {
                            float randomFloat = ((GuiSlider)guiButtonComponent).getSliderPosition() * (0.75f + this.random.nextFloat() * 0.5f) + (this.random.nextFloat() * 0.1f - 0.05f);
                            ((GuiSlider)guiButtonComponent).setSliderPosition(MathHelper.clamp(randomFloat, 0.0f, 1.0f));
                            
                        } else if (guiButtonComponent instanceof GuiListButton) {
                            ((GuiListButton)guiButtonComponent).setValue(this.random.nextBoolean());
                        }
                    }
                }
                
                break;
            case 302:
                this.pageList.previousPage();
                this.updatePageControls();
                break;
            case 303:
                this.pageList.nextPage();
                this.updatePageControls();
                break;
            case 304:
                if (!this.settingsModified) {
                    break;
                }
                this.enterConfirmation(304);
                break;
            case 307:
                this.confirmMode = 0;
                this.exitConfirmation();
                break;
            case 306:
                this.exitConfirmation();
                break;
        }
    }

    @Override
    protected void keyTyped(char character, int integer) throws IOException {
        super.keyTyped(character, integer);
        
        if (this.confirmMode != 0) {
            return;
        }
        
        switch (integer) {
            case 208:
                this.modifyFocusValue(-1.0f);
                break;
            case 200:
                this.modifyFocusValue(1.0f);
                break;
            default:
                this.pageList.onKeyPressed(character, integer);
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clicked) throws IOException {
        super.mouseClicked(mouseX, mouseY, clicked);
        
        if (this.confirmMode != 0 || this.confirmDismissed) {
            return;
        }
        
        this.pageList.mouseClicked(mouseX, mouseY, clicked);
    }

    @Override
    protected void mouseReleased(int integer2, int integer3, int integer4) {
        super.mouseReleased(integer2, integer3, integer4);
        if (this.confirmDismissed) {
            this.confirmDismissed = false;
            return;
        }
        if (this.confirmMode != 0) {
            return;
        }
        this.pageList.mouseReleased(integer2, integer3, integer4);
    }

    private String getFormattedValue(int entry, float entryValue) {
        switch (entry) {
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 107:
            case 108:
            case 110:
            case 111:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 139:
            case 140:
            case 142:
            case 143: return String.format("%5.3f", entryValue);
            
            case 105:
            case 106:
            case 109:
            case 113:
            case 114:
            case 115:
            case 137:
            case 138:
            case 141:
            case 145:
            case 146:
            case 147: return String.format("%2.3f", entryValue);
            
            case 162:
                Biome biome = ForgeRegistries.BIOMES.getValues().get((int)entryValue);
                return (biome != null) ? biome.getBiomeName() : "?";
                
            case 700: return ChunkSourceType.values()[(int)entryValue].getName();
            case 701: return BiomeSourceType.values()[(int)entryValue].getName();
            
            default: return String.format("%d", (int)entryValue);
        }
    }

    private void setSettingsModified(boolean settingsModified) {
        this.settingsModified = settingsModified;
        this.defaults.enabled = settingsModified;
    }

    private void restoreDefaults() {
        this.settings.setDefaults();
        this.createPagedList();
        this.setSettingsModified(false);
    }
    
    private void enterConfirmation(int integer) {
        this.confirmMode = integer;
        this.setConfirmationControls(true);
    }
    
    private void exitConfirmation() throws IOException {
        switch (this.confirmMode) {
            case 300:
                this.actionPerformed((GuiButton)this.pageList.getComponent(300));
                break;
            case 304:
                this.restoreDefaults();
                break;
        }
        this.confirmMode = 0;
        this.confirmDismissed = true;
        this.setConfirmationControls(false);
    }
    
    private void setConfirmationControls(boolean setConfirm) {
        this.confirm.visible = setConfirm;
        this.cancel.visible = setConfirm;
        this.randomize.enabled = !setConfirm;
        this.done.enabled = !setConfirm;
        this.previousPage.enabled = !setConfirm;
        this.nextPage.enabled = !setConfirm;
        this.defaults.enabled = (this.settingsModified && !setConfirm);
        this.presets.enabled = !setConfirm;
        this.pageList.setActive(!setConfirm);
    }
    
    private void updatePageControls() {
        this.previousPage.enabled = (this.pageList.getPage() != 0);
        this.nextPage.enabled = (this.pageList.getPage() != this.pageList.getPageCount() - 1);
        this.subtitle = I18n.format("book.pageIndicator", this.pageList.getPage() + 1, this.pageList.getPageCount());
        this.pageTitle = this.pageNames[this.pageList.getPage()];
        this.randomize.enabled = (this.pageList.getPage() != this.pageList.getPageCount() - 1);
    }
    
    private void modifyFocusValue(float float2) {
        Gui gui3 = this.pageList.getFocusedControl();
        if (!(gui3 instanceof GuiTextField)) {
            return;
        }
        
        float float4 = float2;
        
        if (GuiScreen.isShiftKeyDown()) {
            float4 *= 0.1f;
            
            if (GuiScreen.isCtrlKeyDown()) {
                float4 *= 0.1f;
            }
            
        } else if (GuiScreen.isCtrlKeyDown()) {
            float4 *= 10.0f;
            
            if (GuiScreen.isAltKeyDown()) {
                float4 *= 10.0f;
            }
            
        }
        
        GuiTextField guiTextField5 = (GuiTextField)gui3;
        Float float6 = Floats.tryParse(guiTextField5.getText());
        
        if (float6 == null) {
            return;
        }
        
        float6 += float4;
        int integer7 = guiTextField5.getId();
        String string8 = this.getFormattedValue(guiTextField5.getId(), float6);
        guiTextField5.setText(string8);
        
        this.setEntryValue(integer7, string8);
    }
    
    private void increaseMaxTextLength(GuiPageButtonList pageList, int id) {
        ((GuiTextField)pageList.getComponent(id)).setMaxStringLength(MAX_TEXT_LENGTH);
        ((GuiTextField)pageList.getComponent(id)).width += BIOME_FIELD_ADDITIONAL_WIDTH;
    }
    
    private void setInitialText(GuiPageButtonList pageList, int id, String initial) {
        ((GuiTextField)pageList.getComponent(id)).setText(initial);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.pageList.drawScreen(mouseX, mouseY, partialTicks);
        
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 2, 16777215);
        this.drawCenteredString(this.fontRenderer, this.subtitle, this.width / 2, 12, 16777215);
        this.drawCenteredString(this.fontRenderer, this.pageTitle, this.width / 2, 22, 16777215);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        if (this.confirmMode != 0) {
            Gui.drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
            
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
            this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
            this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
            this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
            
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            
            this.mc.getTextureManager().bindTexture(GuiCustomizeWorldScreen.OPTIONS_BACKGROUND);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            
            bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferBuilder.pos(this.width / 2 - 90, 185.0, 0.0).tex(0.0, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 + 90, 185.0, 0.0).tex(5.625, 2.65625).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 + 90, 100.0, 0.0).tex(5.625, 0.0).color(64, 64, 64, 64).endVertex();
            bufferBuilder.pos(this.width / 2 - 90, 100.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
            
            tessellator.draw();
            
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirmTitle"), this.width / 2, 105, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirm1"), this.width / 2, 125, 16777215);
            this.drawCenteredString(this.fontRenderer, I18n.format(PREFIX + "confirm2"), this.width / 2, 135, 16777215);
            
            this.confirm.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.cancel.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }
}