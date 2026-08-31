package mod.bespectacled.modernbetaforge.compat.depthsupdate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BlockProperty;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.IntProperty;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.compat.CarverCompat;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.compat.HeightCompat;
import mod.bespectacled.modernbetaforge.util.ForgeRegistryUtil;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CompatDepthsUpdate implements Compat, ClientCompat, HeightCompat, CarverCompat {
	public static final String MOD_ID = "depthsupdate";
	public static final String RECOMMENDED_MOD_VERSION = "1.12.2-1.0.0-a12";
    public static final String ADDON_ID = "compat" + MOD_ID;
	
	public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
    public static final ResourceLocation KEY_USE_DEEPSLATE = new ResourceLocation(ADDON_ID, "useDeepslate");
    public static final ResourceLocation KEY_DEEPSLATE_BLOCK = new ResourceLocation(ADDON_ID, "deepslateBlock");
    public static final ResourceLocation KEY_DEEPSLATE_MAX_Y = new ResourceLocation(ADDON_ID, "deepslateMaxY");
    public static final ResourceLocation KEY_DEEPSLATE_RANGE = new ResourceLocation(ADDON_ID, "deepslateRange");
	
    public static final ResourceLocation KEY_DEEPSLATE = new ResourceLocation(ADDON_ID, "deepslate");

	@Override
	public void load() { 
	    MinecraftForge.EVENT_BUS.register(DepthsUpdateConfig.class);

        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(false));
        ModernBetaRegistries.PROPERTY.register(KEY_USE_DEEPSLATE, new BooleanProperty(true));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_BLOCK, new BlockProperty(new ResourceLocation(MOD_ID, "deepslate")));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_MAX_Y, new IntProperty(8, -64, 64, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEEPSLATE_RANGE, new IntProperty(8, 0, 32, PropertyGuiType.SLIDER));

        ModernBetaRegistries.BLOCK_SOURCE.register(KEY_DEEPSLATE, BlockSourceDeepslate::new);
	}

	@Override
    public void loadClient() {
	    ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COMPAT, new GuiPredicate(settings ->
            DepthsUpdateConfig.INSTANCE.extendHeight()
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
    public String getRecommendedModVersion() {
        return RECOMMENDED_MOD_VERSION;
    }
    
    @Override
    public String getModTooltip() {
        return I18n.format(String.format("createWorld.customize.custom.%s.tooltip", ADDON_ID));
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
