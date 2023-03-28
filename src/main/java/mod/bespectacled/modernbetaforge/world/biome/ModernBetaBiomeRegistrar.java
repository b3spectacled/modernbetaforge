package mod.bespectacled.modernbetaforge.world.biome;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.world.biome.biomes.alpha.BiomeAlpha;
import mod.bespectacled.modernbetaforge.world.biome.biomes.alpha.BiomeAlphaWinter;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaBeach;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaDesert;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaForest;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaFrozenOcean;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaIceDesert;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaOcean;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaPlains;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaRainforest;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSavanna;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSeasonalForest;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaShrubland;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSnowyBeach;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSwampland;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaTaiga;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaTundra;
import mod.bespectacled.modernbetaforge.world.biome.biomes.infdev.BiomeInfdev415;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = ModernBeta.MODID)
public class ModernBetaBiomeRegistrar {
    @SubscribeEvent
    public static void register(Register<Biome> event) {
        BiomeRegistry biomeRegistry = new BiomeRegistry(event.getRegistry());
        
        biomeRegistry.register(new BiomeBetaIceDesert(), "beta_ice_desert", Type.SANDY, Type.COLD, Type.SPARSE);
        biomeRegistry.register(new BiomeBetaTundra(), "beta_tundra", Type.SNOWY, Type.COLD, Type.SPARSE);
        biomeRegistry.register(new BiomeBetaSavanna(), "beta_savanna", Type.PLAINS, Type.SAVANNA, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaDesert(), "beta_desert", Type.SANDY, Type.HOT, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaSwampland(), "beta_swampland", Type.SWAMP, Type.SPARSE, Type.WET);
        biomeRegistry.register(new BiomeBetaTaiga(), "beta_taiga", Type.SNOWY, Type.COLD, Type.CONIFEROUS);
        biomeRegistry.register(new BiomeBetaShrubland(), "beta_shrubland", Type.PLAINS, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaForest(), "beta_forest", Type.FOREST);
        biomeRegistry.register(new BiomeBetaPlains(), "beta_plains", Type.PLAINS, Type.DRY);
        biomeRegistry.register(new BiomeBetaSeasonalForest(), "beta_seasonal_forest", Type.FOREST);
        biomeRegistry.register(new BiomeBetaRainforest(), "beta_rainforest", Type.FOREST, Type.LUSH, Type.JUNGLE, Type.DENSE, Type.WET);
        
        biomeRegistry.register(new BiomeBetaOcean(), "beta_ocean", Type.OCEAN, Type.WET);
        biomeRegistry.register(new BiomeBetaFrozenOcean(), "beta_frozen_ocean", Type.OCEAN, Type.SNOWY, Type.COLD, Type.WET);
        
        biomeRegistry.register(new BiomeBetaBeach(), "beta_beach", Type.BEACH, Type.WET);
        biomeRegistry.register(new BiomeBetaSnowyBeach(), "beta_snowy_beach", Type.BEACH, Type.SNOWY, Type.COLD, Type.WET);
        
        biomeRegistry.register(new BiomeAlpha(), "alpha", Type.FOREST);
        biomeRegistry.register(new BiomeAlphaWinter(), "alpha_winter", Type.FOREST, Type.SNOWY, Type.COLD);
        
        biomeRegistry.register(new BiomeInfdev415(), "infdev_415", Type.FOREST);
    }
    
    private static class BiomeRegistry {
        private final IForgeRegistry<Biome> registry;
        
        BiomeRegistry(IForgeRegistry<Biome> registry) {
            this.registry = registry;
        }
        
        public void register(Biome biome, String name, Type... types) {
            biome.setRegistryName(ModernBeta.MODID, name);
            this.registry.register(biome);
            BiomeDictionary.addTypes(biome, types);
        }
    }
}
