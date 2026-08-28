package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.CarverCompat;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBeta18Cave;
import mod.bespectacled.modernbetaforge.world.carver.MapGenNoOp;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CompatDepthsUpdate implements Compat, ClientCompat, HeightCompat, CarverCompat {
	public static final String MOD_ID = "depthsupdate";
    public static final String ADDON_ID = "compat" + MOD_ID;
	
	public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
	public static final ResourceLocation KEY_USE_DEEP_CAVES = new ResourceLocation(ADDON_ID, "useDeepCaves");
	public static final ResourceLocation KEY_DEEP_CAVES_WIDTH = new ResourceLocation(ADDON_ID, "deepCavesWidth");
	public static final ResourceLocation KEY_DEEP_CAVES_COUNT = new ResourceLocation(ADDON_ID, "deepCavesCount");
	public static final ResourceLocation KEY_DEEP_CAVES_CHANCE = new ResourceLocation(ADDON_ID, "deepCavesChance");
    public static final ResourceLocation KEY_USE_DEEPSLATE = new ResourceLocation(ADDON_ID, "useDeepslate");
    public static final ResourceLocation KEY_DEEPSLATE_BLOCK = new ResourceLocation(ADDON_ID, "deepslateBlock");
    public static final ResourceLocation KEY_DEEPSLATE_MAX_Y = new ResourceLocation(ADDON_ID, "deepslateMaxY");
    public static final ResourceLocation KEY_DEEPSLATE_RANGE = new ResourceLocation(ADDON_ID, "deepslateRange");
	
    public static final ResourceLocation KEY_DEEP_CAVES = new ResourceLocation(ADDON_ID, "deepCaves");
    public static final ResourceLocation KEY_DEEPSLATE = new ResourceLocation(ADDON_ID, "deepslate");

	@Override
	public void load() { 
	    MinecraftForge.EVENT_BUS.register(DepthsUpdateConfig.class);
	    
	    float minCaveWidth = ModernBetaGeneratorSettings.MIN_CAVE_WIDTH;
	    float maxCaveWidth = ModernBetaGeneratorSettings.MAX_CAVE_WIDTH;
	    int minCaveCount = ModernBetaGeneratorSettings.MIN_CAVE_COUNT;
	    int maxCaveCount = ModernBetaGeneratorSettings.MAX_CAVE_COUNT;
	    int minCaveChance = ModernBetaGeneratorSettings.MIN_CAVE_CHANCE;
	    int maxCaveChance = ModernBetaGeneratorSettings.MAX_CAVE_CHANCE;

        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(false));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_DEEP_CAVES, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEP_CAVES_WIDTH, new FloatProperty(1.0f, minCaveWidth, maxCaveWidth, PropertyGuiType.SLIDER, 1));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEP_CAVES_COUNT, new IntProperty(40, minCaveCount, maxCaveCount, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEP_CAVES_CHANCE, new IntProperty(15, minCaveChance, maxCaveChance, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_DEEPSLATE, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_BLOCK, new BlockProperty(new ResourceLocation(MOD_ID, "deepslate")));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_MAX_Y, new IntProperty(8, -64, 64, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_RANGE, new IntProperty(8, 0, 32, PropertyGuiType.SLIDER));
        
        ModernBetaRegistries.CARVER.register(KEY_DEEP_CAVES, (chunkSource, settings) ->
            settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEP_CAVES) ?
                TerrainGen.getModdedMapGen(new MapGenBeta18Cave(
                    chunkSource.getDefaultBlock(),
                    chunkSource.getDefaultFluid(),
                    BlockStates.AIR,
                    settings.getFloatProperty(KEY_DEEP_CAVES_WIDTH),
                    8,
                    settings.getIntProperty(KEY_DEEP_CAVES_COUNT),
                    settings.getIntProperty(KEY_DEEP_CAVES_CHANCE),
                    settings.floor,
                    settings.floor
                ), EventType.CUSTOM) :
                new MapGenNoOp(chunkSource, settings)
        );
        ModernBetaRegistries.BLOCK_SOURCE.register(KEY_DEEPSLATE, BlockSourceDeepslate::new);
	}

	@Override
    public void loadClient() {
	    ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COMPAT, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight()
        ));
	    ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_DEEP_CAVES, new GuiPredicate(settings ->
	        DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEP_CAVES_WIDTH, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEP_CAVES)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEP_CAVES_COUNT, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEP_CAVES)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEP_CAVES_CHANCE, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEP_CAVES)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_DEEPSLATE, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEPSLATE_BLOCK, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEPSLATE)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEPSLATE_MAX_Y, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEPSLATE)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEEPSLATE_RANGE, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight() && settings.getBooleanProperty(KEY_USE_COMPAT) && settings.getBooleanProperty(KEY_USE_DEEPSLATE)
        ));
    }

    @Override
	public String getModId() {
		return MOD_ID;
	}

    @Override
    public boolean extendHeight() {
        return DepthsUpdateConfig.INSTANCE.extendHeight();
    }

    @Override
    public int getMinHeight() {
        return DepthsUpdateConfig.INSTANCE.getMinY();
    }

    @Override
    public int getMaxHeight() {
        return DepthsUpdateConfig.INSTANCE.getMaxY();
    }

    @Override
    public List<Block> getCarvables() {
        Function<ResourceLocation, Block> blockFunc = key -> ForgeRegistryUtil.getOrElse(key, new ResourceLocation("stone"), ForgeRegistries.BLOCKS);
        List<Block> carvables = new ArrayList<>();
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "cobbled_deepslate")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_coal_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_iron_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_gold_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_redstone_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_lapis_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_diamond_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_emerald_ore")));
        carvables.add(blockFunc.apply(new ResourceLocation(MOD_ID, "deepslate_copper_ore")));
        
        return carvables;
    }
}
