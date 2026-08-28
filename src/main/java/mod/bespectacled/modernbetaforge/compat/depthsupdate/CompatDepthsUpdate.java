package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
import mod.bespectacled.modernbetaforge.util.BlockStates;
import mod.bespectacled.modernbetaforge.world.carver.MapGenBeta18Cave;
import mod.bespectacled.modernbetaforge.world.carver.MapGenNoOp;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class CompatDepthsUpdate implements Compat, ClientCompat, HeightCompat {
	public static final String MOD_ID = "depthsupdate";
    public static final String ADDON_ID = "compat" + MOD_ID;
	
	public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
	public static final ResourceLocation KEY_USE_DEEP_CAVES = new ResourceLocation(ADDON_ID, "useDeepCaves");
	public static final ResourceLocation KEY_DEEP_CAVES_WIDTH = new ResourceLocation(ADDON_ID, "deepCavesWidth");
	public static final ResourceLocation KEY_DEEP_CAVES_COUNT = new ResourceLocation(ADDON_ID, "deepCavesCount");
	public static final ResourceLocation KEY_DEEP_CAVES_CHANCE = new ResourceLocation(ADDON_ID, "deepCavesChance");
    
    public static final ResourceLocation KEY_DEEP_CAVES = new ResourceLocation(ADDON_ID, "deepCaves");

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
}
