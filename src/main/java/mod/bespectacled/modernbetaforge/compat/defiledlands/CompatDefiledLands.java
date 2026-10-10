package mod.bespectacled.modernbetaforge.compat.defiledlands;

import mod.bespectacled.modernbetaforge.api.client.gui.GuiPredicate;
import mod.bespectacled.modernbetaforge.api.property.BooleanProperty;
import mod.bespectacled.modernbetaforge.api.property.FloatProperty;
import mod.bespectacled.modernbetaforge.api.property.PropertyGuiType;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaClientRegistries;
import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.client.gui.GuiPredicates;
import mod.bespectacled.modernbetaforge.compat.ClientCompat;
import mod.bespectacled.modernbetaforge.compat.Compat;
import net.minecraft.util.ResourceLocation;

public class CompatDefiledLands implements Compat, ClientCompat {
    public static final String MOD_ID = "defiledlands";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
    
    public static final ResourceLocation KEY_DEFILED_DESERT_CHANCE = new ResourceLocation(ADDON_ID, "defiledDesertChance");
    public static final ResourceLocation KEY_DEFILED_HILLS_CHANCE = new ResourceLocation(ADDON_ID, "defiledHillsChance");
    public static final ResourceLocation KEY_DEFILED_ICE_PLAINS_CHANCE = new ResourceLocation(ADDON_ID, "defiledIcePlainsChance");
    public static final ResourceLocation KEY_DEFILED_PLAINS_CHANCE = new ResourceLocation(ADDON_ID, "defiledPlainsChance");
    public static final ResourceLocation KEY_DEFILED_SWAMP_CHANCE = new ResourceLocation(ADDON_ID, "defiledSwampChance");
    public static final ResourceLocation KEY_TENEBRA_FOREST_CHANCE = new ResourceLocation(ADDON_ID, "tenebraForestChance");
    public static final ResourceLocation KEY_VILESPINE_FOREST_CHANCE = new ResourceLocation(ADDON_ID, "vilespineForestChance");
    
    public static final ResourceLocation KEY_DEFILED_DESERT_NOISE_SCALE = new ResourceLocation(ADDON_ID, "defiledDesertNoiseScale");
    public static final ResourceLocation KEY_DEFILED_HILLS_NOISE_SCALE = new ResourceLocation(ADDON_ID, "defiledHillsNoiseScale");
    public static final ResourceLocation KEY_DEFILED_ICE_PLAINS_NOISE_SCALE = new ResourceLocation(ADDON_ID, "defiledIcePlainsNoiseScale");
    public static final ResourceLocation KEY_DEFILED_PLAINS_NOISE_SCALE = new ResourceLocation(ADDON_ID, "defiledPlainsNoiseScale");
    public static final ResourceLocation KEY_DEFILED_SWAMP_NOISE_SCALE = new ResourceLocation(ADDON_ID, "defiledSwampNoiseScale");
    public static final ResourceLocation KEY_TENEBRA_FOREST_NOISE_SCALE = new ResourceLocation(ADDON_ID, "tenebraForestNoiseScale");
    public static final ResourceLocation KEY_VILESPINE_FOREST_NOISE_SCALE = new ResourceLocation(ADDON_ID, "vilespineForestNoiseScale");
    
    public static final ResourceLocation KEY_DEFILED_DESERT_RESOLVER = new ResourceLocation(ADDON_ID, "defiledDesertResolver");
    public static final ResourceLocation KEY_DEFILED_HILLS_RESOLVER = new ResourceLocation(ADDON_ID, "defiledHillsResolver");
    public static final ResourceLocation KEY_DEFILED_ICE_PLAINS_RESOLVER = new ResourceLocation(ADDON_ID, "defiledIcePlainsResolver");
    public static final ResourceLocation KEY_DEFILED_PLAINS_RESOLVER = new ResourceLocation(ADDON_ID, "defiledPlainsResolver");
    public static final ResourceLocation KEY_DEFILED_SWAMP_RESOLVER = new ResourceLocation(ADDON_ID, "defiledSwampResolver");
    public static final ResourceLocation KEY_TENEBRA_FOREST_RESOLVER = new ResourceLocation(ADDON_ID, "tenebraForestResolver");
    public static final ResourceLocation KEY_VILESPINE_FOREST_RESOLVER = new ResourceLocation(ADDON_ID, "vilespineForestResolver");

    @Override
    public void load() {
        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(false));
        
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_DESERT_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_HILLS_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_ICE_PLAINS_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_PLAINS_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_SWAMP_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_TENEBRA_FOREST_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_VILESPINE_FOREST_CHANCE, new FloatProperty(0.1f, 0.0f, 1.0f, PropertyGuiType.SLIDER));

        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_DESERT_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_HILLS_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_ICE_PLAINS_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_PLAINS_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_DEFILED_SWAMP_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_TENEBRA_FOREST_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        ModernBetaRegistries.PROPERTY.register(KEY_VILESPINE_FOREST_NOISE_SCALE, new FloatProperty(1.0f, 1.0f, 20.f, PropertyGuiType.SLIDER));
        
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_DEFILED_DESERT_RESOLVER, DefiledDesertBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_DEFILED_HILLS_RESOLVER, DefiledHillsBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_DEFILED_ICE_PLAINS_RESOLVER, DefiledIcePlainsBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_DEFILED_PLAINS_RESOLVER, DefiledPlainsBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_DEFILED_SWAMP_RESOLVER, DefiledSwampBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_TENEBRA_FOREST_RESOLVER, TenebraForestBiomeResolver::new);
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_VILESPINE_FOREST_RESOLVER, VilespineForestBiomeResolver::new);
    }
    
    @Override
    public void loadClient() {
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_USE_COMPAT, new GuiPredicate(settings ->
            !GuiPredicates.isBiomeVanillaOrBoP(settings)
        ));
        
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_DESERT_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_HILLS_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_ICE_PLAINS_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_PLAINS_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_SWAMP_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_TENEBRA_FOREST_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_VILESPINE_FOREST_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_DESERT_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_HILLS_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_ICE_PLAINS_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_PLAINS_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_DEFILED_SWAMP_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_TENEBRA_FOREST_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_VILESPINE_FOREST_NOISE_SCALE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeVanillaOrBoP(settings) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
    }
    
    @Override
    public String getModId() {
        return MOD_ID;
    }

}
