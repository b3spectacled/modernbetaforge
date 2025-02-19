package mod.bespectacled.modernbetaforge.compat;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry.BiomeDataBasePopulatorRegistryEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBasePopulatorJson;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompatDynamicTrees implements Compat {
    @Override
    public void load() { 
        MinecraftForge.EVENT_BUS.register(CompatDynamicTrees.class);
    }
    
    @SubscribeEvent
    public static void registerDataBasePopulators(BiomeDataBasePopulatorRegistryEvent event) {
        event.register(new BiomeDataBasePopulator());
    }
    
    public static boolean isEnabled() {
        return WorldGenRegistry.isWorldGenEnabled();
    }

    private static class BiomeDataBasePopulator implements IBiomeDataBasePopulator {
        public static final String PATH = "worldgen/dynamic_trees.json";
        public static final String PATH_ALT = "worldgen/dynamic_trees_alternate.json";

        private final BiomeDataBasePopulatorJson jsonPopulator;

        public BiomeDataBasePopulator() {
            String jsonLocation = ModernBetaConfig.compatOptions.dynamictrees_useModernTrees ? PATH_ALT : PATH;
            this.jsonPopulator = new BiomeDataBasePopulatorJson(ModernBeta.createRegistryKey(jsonLocation));
        }

        @Override
        public void populate(BiomeDataBase database) {
            this.jsonPopulator.populate(database);
        }
    }
}
