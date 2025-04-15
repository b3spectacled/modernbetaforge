package mod.bespectacled.modernbetaforge.compat.buildcraft;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiPredicates;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import mod.bespectacled.modernbetaforge.world.biome.source.ReleaseBiomeSource;
import net.minecraft.util.ResourceLocation;

public class CompatBuildCraftEnergy implements Compat, ClientCompat {
    public static final String MOD_ID = "buildcraftenergy";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
    public static final ResourceLocation KEY_OIL_DESERT_CHANCE = new ResourceLocation(ADDON_ID, "oilDesertChance");
    public static final ResourceLocation KEY_OIL_OCEAN_CHANCE = new ResourceLocation(ADDON_ID, "oilOceanChance");
    
    public static final ResourceLocation KEY_OIL_DESERT_RESOLVER = new ResourceLocation(ADDON_ID, "resolverOilDesert");
    public static final ResourceLocation KEY_OIL_OCEAN_RESOLVER = new ResourceLocation(ADDON_ID, "resolverOilOcean");
    
    @Override
    public void load() {
        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(false));
        ModernBetaRegistries.PROPERTY.register(KEY_OIL_DESERT_CHANCE, new FloatProperty(
            0.25f,
            0.0f,
            1.0f,
            PropertyGuiType.SLIDER
        ));
        ModernBetaRegistries.PROPERTY.register(KEY_OIL_OCEAN_CHANCE, new FloatProperty(
            0.25f,
            0.0f,
            1.0f,
            PropertyGuiType.SLIDER
        ));
        
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_OIL_DESERT_RESOLVER, BuildCraftOilDesertResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_OIL_OCEAN_RESOLVER, BuildCraftOilOceanResolver::new);
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void loadClient() {
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COMPAT, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeInstanceOf(settings, ReleaseBiomeSource.class)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_OIL_DESERT_CHANCE, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeInstanceOf(settings, ReleaseBiomeSource.class) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_OIL_OCEAN_CHANCE, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeInstanceOf(settings, ReleaseBiomeSource.class) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
    }

}
