package mod.bespectacled.modernbetaforge.world.biome;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.world.biome.biomes.alpha.BiomeAlphaNormal;
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
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSky;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSnowyBeach;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaSwampland;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaTaiga;
import mod.bespectacled.modernbetaforge.world.biome.biomes.beta.BiomeBetaTundra;
import mod.bespectacled.modernbetaforge.world.biome.biomes.indev.BiomeIndevNormal;
import mod.bespectacled.modernbetaforge.world.biome.biomes.indev.BiomeIndevParadise;
import mod.bespectacled.modernbetaforge.world.biome.biomes.indev.BiomeIndevWoods;
import mod.bespectacled.modernbetaforge.world.biome.biomes.infdev.BiomeInfdev227;
import mod.bespectacled.modernbetaforge.world.biome.biomes.infdev.BiomeInfdev415;
import mod.bespectacled.modernbetaforge.world.biome.biomes.infdev.BiomeInfdev420;
import mod.bespectacled.modernbetaforge.world.biome.biomes.infdev.BiomeInfdev611;
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
        
        biomeRegistry.register(new BiomeBetaIceDesert(), ModernBetaBiomeTags.BETA_ICE_DESERT, Type.SANDY, Type.COLD, Type.SPARSE);
        biomeRegistry.register(new BiomeBetaTundra(), ModernBetaBiomeTags.BETA_TUNDRA, Type.SNOWY, Type.COLD, Type.SPARSE);
        biomeRegistry.register(new BiomeBetaSavanna(), ModernBetaBiomeTags.BETA_SAVANNA, Type.PLAINS, Type.SAVANNA, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaDesert(), ModernBetaBiomeTags.BETA_DESERT, Type.SANDY, Type.HOT, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaSwampland(), ModernBetaBiomeTags.BETA_SWAMPLAND, Type.SWAMP, Type.SPARSE, Type.WET);
        biomeRegistry.register(new BiomeBetaTaiga(), ModernBetaBiomeTags.BETA_TAIGA, Type.SNOWY, Type.COLD, Type.CONIFEROUS);
        biomeRegistry.register(new BiomeBetaShrubland(), ModernBetaBiomeTags.BETA_SHRUBLAND, Type.PLAINS, Type.SPARSE, Type.DRY);
        biomeRegistry.register(new BiomeBetaForest(), ModernBetaBiomeTags.BETA_FOREST, Type.FOREST);
        biomeRegistry.register(new BiomeBetaPlains(), ModernBetaBiomeTags.BETA_PLAINS, Type.PLAINS, Type.DRY);
        biomeRegistry.register(new BiomeBetaSeasonalForest(), ModernBetaBiomeTags.BETA_SEASONAL_FOREST, Type.FOREST);
        biomeRegistry.register(new BiomeBetaRainforest(), ModernBetaBiomeTags.BETA_RAINFOREST, Type.FOREST, Type.LUSH, Type.JUNGLE, Type.DENSE, Type.WET);
        biomeRegistry.register(new BiomeBetaSky(), ModernBetaBiomeTags.BETA_SKY, Type.PLAINS, Type.SPARSE, Type.DRY);
        
        biomeRegistry.register(new BiomeBetaOcean(), ModernBetaBiomeTags.BETA_OCEAN, Type.OCEAN, Type.WET);
        biomeRegistry.register(new BiomeBetaFrozenOcean(), ModernBetaBiomeTags.BETA_FROZEN_OCEAN, Type.OCEAN, Type.SNOWY, Type.COLD, Type.WET);
        
        biomeRegistry.register(new BiomeBetaBeach(), ModernBetaBiomeTags.BETA_BEACH, Type.BEACH);
        biomeRegistry.register(new BiomeBetaSnowyBeach(), ModernBetaBiomeTags.BETA_SNOWY_BEACH, Type.BEACH, Type.SNOWY, Type.COLD);
        
        biomeRegistry.register(new BiomeAlphaNormal(), ModernBetaBiomeTags.ALPHA, Type.FOREST);
        biomeRegistry.register(new BiomeAlphaWinter(), ModernBetaBiomeTags.ALPHA_WINTER, Type.FOREST, Type.SNOWY, Type.COLD);
        
        biomeRegistry.register(new BiomeInfdev227(), ModernBetaBiomeTags.INFDEV_227, Type.SPARSE);
        biomeRegistry.register(new BiomeInfdev415(), ModernBetaBiomeTags.INFDEV_415, Type.FOREST);
        biomeRegistry.register(new BiomeInfdev420(), ModernBetaBiomeTags.INFDEV_420, Type.FOREST);
        biomeRegistry.register(new BiomeInfdev611(), ModernBetaBiomeTags.INFDEV_611, Type.FOREST);
        
        biomeRegistry.register(new BiomeIndevNormal(), ModernBetaBiomeTags.INDEV_NORMAL, Type.FOREST);
        biomeRegistry.register(new BiomeIndevParadise(), ModernBetaBiomeTags.INDEV_PARADISE, Type.FOREST, Type.LUSH);
        biomeRegistry.register(new BiomeIndevWoods(), ModernBetaBiomeTags.INDEV_WOODS, Type.FOREST, Type.DENSE);
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
