package mod.bespectacled.modernbetaforge;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.bespectacled.modernbetaforge.registry.ModernBetaBuiltInRegistries;
import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixer;
import mod.bespectacled.modernbetaforge.world.ModernBetaWorldType;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeStructures;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ModernBeta.MODID, name = ModernBeta.NAME, version = ModernBeta.VERSION, acceptedMinecraftVersions = ModernBeta.MCVERSION)
public class ModernBeta {
    public static final String MODID = "modernbetaforge";
    public static final String NAME = "Modern Beta Forge";
    public static final String VERSION = "1.2.0.0";
    public static final String MCVERSION = "1.12.2";
    public static final int DATA_VERSION = 1200;

    private static Logger logger = LogManager.getLogger(MODID);
    
    public static ResourceLocation createId(String name) {
        return new ResourceLocation(MODID, name);
    }
    
    public static void log(Level level, String message) {
        logger.log(level, "{}", message);
    }
    
    @SidedProxy(
        clientSide = "mod.bespectacled.modernbetaforge.ModernBetaClientProxy",
        serverSide = "mod.bespectacled.modernbetaforge.ModernBetaServerProxy"
    )
    public static ModernBetaProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)  {
        ModernBetaWorldType.register();
        
        ModernBetaBiomeStructures.registerStructures();
        ModernBetaBiomeStructures.registerStructureBiomes();
        
        ModernBetaBuiltInRegistries.registerChunkSources();
        ModernBetaBuiltInRegistries.registerBiomeSources();
        ModernBetaBuiltInRegistries.registerNoiseSettings();
        ModernBetaBuiltInRegistries.registerSurfaceBuilders();
        ModernBetaBuiltInRegistries.registerDataFixes();
        
        ModDataFixer.INSTANCE.registerModDataFixes();
        
        proxy.init();
    }
    
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) { }

    @EventHandler
    private void onFMLServerAboutToStartEvent(FMLServerAboutToStartEvent event) { }
    
    @EventHandler
    private void onFMLServerStartingEvent(FMLServerStartingEvent event) { }
}
