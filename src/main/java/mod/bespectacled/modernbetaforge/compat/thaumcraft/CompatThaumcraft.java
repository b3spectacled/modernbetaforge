package mod.bespectacled.modernbetaforge.compat.thaumcraft;

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

public class CompatThaumcraft implements Compat, ClientCompat {
    public static final String MOD_ID = "thaumcraft";
    public static final String ADDON_ID = "compat" + MOD_ID;
    
    public static final ResourceLocation KEY_USE_COMPAT = new ResourceLocation(ADDON_ID, "useCompat");
    public static final ResourceLocation KEY_MAGICAL_FOREST_CHANCE = new ResourceLocation(ADDON_ID, "magicalForestChance");
    
    public static final ResourceLocation KEY_MAGICAL_FOREST_RESOLVER = new ResourceLocation(ADDON_ID, "resolverMagicalForest");

    @Override
    public void load() {
        ModernBetaRegistries.PROPERTY.register(KEY_USE_COMPAT, new BooleanProperty(false));
        ModernBetaRegistries.PROPERTY.register(KEY_MAGICAL_FOREST_CHANCE, new FloatProperty(
            0.05f,
            0.0f,
            1.0f,
            PropertyGuiType.SLIDER
        ));
        
        ModernBetaRegistries.BIOME_RESOLVER.register(KEY_MAGICAL_FOREST_RESOLVER, ThaumcraftMagicalForestResolver::new);
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
        ModernBetaClientRegistries.GUI_PREDICATE.register(KEY_MAGICAL_FOREST_CHANCE, new GuiPredicate(settings -> 
            !GuiPredicates.isBiomeInstanceOf(settings, ReleaseBiomeSource.class) && settings.getBooleanProperty(KEY_USE_COMPAT)
        ));
        
    }

}
