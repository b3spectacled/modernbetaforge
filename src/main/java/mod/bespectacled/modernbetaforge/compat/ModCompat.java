package mod.bespectacled.modernbetaforge.compat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.compat.bettermineshafts.CompatBetterMineshafts;
import mod.bespectacled.modernbetaforge.compat.biomesoplenty.CompatBiomesOPlenty;
import mod.bespectacled.modernbetaforge.compat.buildcraft.CompatBuildCraftEnergy;
import mod.bespectacled.modernbetaforge.compat.dynamictrees.CompatDynamicTrees;
import mod.bespectacled.modernbetaforge.compat.futuremc.CompatFutureMC;
import mod.bespectacled.modernbetaforge.compat.galacticraft.CompatGalacticraft;
import mod.bespectacled.modernbetaforge.compat.nether_api.CompatNetherAPI;
import mod.bespectacled.modernbetaforge.compat.oe.CompatOE;
import mod.bespectacled.modernbetaforge.compat.thaumcraft.CompatThaumcraft;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraftforge.fml.common.Loader;

public class ModCompat {
    public static final Map<String, Compat> LOADED_COMPATS = new LinkedHashMap<>();
    public static final HeightManager HEIGHT_MANAGER = new HeightManager();
    public static final NetherManager NETHER_MANAGER = new NetherManager();
    
    public static void loadCompat() {
        if (!Loader.isModLoaded("mixinbooter")) {
            ModernBeta.log(Level.WARN, "MixinBooter was not found or an alternate mixin loader was installed..");
        }
        
        loadCompat(new CompatBiomesOPlenty());
        loadCompat(new CompatGalacticraft());
        loadCompat(new CompatNetherAPI());
        loadCompat(new CompatDynamicTrees());
        loadCompat(new CompatBuildCraftEnergy());
        loadCompat(new CompatThaumcraft());
        loadCompat(new CompatBetterMineshafts());
        loadCompat(new CompatFutureMC());
        loadCompat(new CompatOE());
    }
    
    public static boolean isCompatLoaded(String modId) {
        return LOADED_COMPATS.containsKey(modId);
    }
    
    private static void loadCompat(Compat compat) {
        String modId = compat.getModId();
        
        if (Loader.isModLoaded(modId)) {
            try {
                ModernBeta.log(Level.INFO, String.format("Found mod '%s'..", modId));
                compat.load();
                HEIGHT_MANAGER.checkCompat(compat);
                NETHER_MANAGER.checkCompat(compat);
                LOADED_COMPATS.put(modId, compat);
                
            } catch (Exception e) {
                ModernBeta.log(Level.ERROR, String.format("Couldn't load compat for mod '%s'!", modId));
                
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static class HeightManager {
        private HeightCompat heightCompat;
        private int numHeightMods;
        private boolean warned;
        
        private HeightManager() { }
        
        public boolean extendsHeight() {
            return this.numHeightMods == 1 && this.heightCompat.extendsHeight();
        }
        
        public int getMinHeight() {
            return this.heightCompat != null ? this.heightCompat.getMinHeight() : ModernBetaGeneratorSettings.MIN_FLOOR;
        }
        
        public int getMaxHeight() {
            return this.heightCompat != null ?  this.heightCompat.getMaxHeight() : ModernBetaGeneratorSettings.MAX_HEIGHT;
        }
        
        private void checkCompat(Compat compat) {
            if (compat instanceof HeightCompat) {
                this.heightCompat = (HeightCompat)compat;
                this.numHeightMods++;
            }
            
            if (this.numHeightMods > 1 && !this.warned) {
                ModernBeta.log(Level.WARN, "More than one height extension mod is installed. Related generator settings will be disabled.");
                this.warned = true;
            }
        }
    }
    
    public static class NetherManager {
        private List<String> incompatibleMods;
        private boolean isCompatible;
        
        private NetherManager() { 
            this.incompatibleMods = new ArrayList<>();
        }
        
        public boolean isCompatible() {
            return this.isCompatible;
        }
        
        public List<String> getIncompatibleMods() {
            return new ArrayList<>(this.incompatibleMods);
        }
        
        private void checkCompat(Compat compat) {
            if (compat instanceof NetherCompat && !((NetherCompat)compat).isCompatible()) {
                this.isCompatible = false;
                this.incompatibleMods.add(compat.getModId());
            }
        }
    }
} 
