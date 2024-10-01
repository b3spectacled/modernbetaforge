package mod.bespectacled.modernbetaforge;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.chunk.ChunkSource;
import mod.bespectacled.modernbetaforge.config.ModernBetaConfig;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeStructures;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaWorldType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ModernBeta.MODID, name = ModernBeta.NAME, version = ModernBeta.VERSION, acceptedMinecraftVersions = ModernBeta.MCVERSION)
public class ModernBeta {
    public static final String MODID = "modernbetaforge";
    public static final String NAME = "Modern Beta Forge";
    public static final String VERSION = "1.0.4.1";
    public static final String MCVERSION = "1.12.2";

    private static Logger logger = LogManager.getLogger(MODID);
    
    public static ResourceLocation createId(String name) {
        return new ResourceLocation(MODID, name);
    }
    
    public static void log(Level level, String message) {
        logger.log(level, "{}", message);
    }
    
    @SidedProxy(
        clientSide = "mod.bespectacled.modernbetaforge.ModernBetaClientProxy",
        serverSide = "mod.bespectacled.modernbetaforge.ModernBetaCommonProxy"
    )
    public static ModernBetaProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.initColors();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)  {
        ModernBetaWorldType.register();
        
        ModernBetaBiomeStructures.registerStructures();
        ModernBetaBiomeStructures.registerStructureBiomes();
        
        proxy.init();
    }
    
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) { }
    
    /*
     * Modify player spawning algorithm
     */
    
    @EventHandler
    public void onFMLServerStartingEvent(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        WorldServer worldServer = server.getServer().getWorld(0);

        IChunkGenerator chunkGenerator = worldServer.getChunkProvider().chunkGenerator;
        BiomeProvider biomeProvider = worldServer.getBiomeProvider();
        
        BlockPos currentSpawnPos = worldServer.getSpawnPoint();
        boolean useOldSpawns = ModernBetaConfig.spawnOptions.useOldSpawns;
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
            
            BlockPos newSpawnPos = useOldSpawns ?
                chunkSource.getSpawnLocator().locateSpawn(currentSpawnPos, chunkSource, biomeSource) :
                null;
            
            if (newSpawnPos != null) {
                worldServer.setSpawnPoint(newSpawnPos);
            }
        }
    }
}
