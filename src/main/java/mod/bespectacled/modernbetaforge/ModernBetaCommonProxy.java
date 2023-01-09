package mod.bespectacled.modernbetaforge;

import mod.bespectacled.modernbetaforge.eventhandler.BiomeColorsEventHandler;
import mod.bespectacled.modernbetaforge.eventhandler.DebugInfoEventHandler;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeStructures;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaWorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModernBetaCommonProxy {
    public void preInit(FMLPreInitializationEvent event) {    
        ModernBetaWorldType.register();
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DebugInfoEventHandler());
        MinecraftForge.EVENT_BUS.register(new BiomeColorsEventHandler());
        
        ModernBetaBiomeStructures.registerStructures();
        ModernBetaBiomeStructures.registerStructureBiomes();
    }

    public void postInit(FMLPostInitializationEvent event) { }
}
