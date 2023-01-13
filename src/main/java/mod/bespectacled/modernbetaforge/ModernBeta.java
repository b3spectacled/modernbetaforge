package mod.bespectacled.modernbetaforge;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.gen.ChunkSource;
import mod.bespectacled.modernbetaforge.api.world.gen.NoiseChunkSource;
import mod.bespectacled.modernbetaforge.util.MathUtil;
import mod.bespectacled.modernbetaforge.util.chunk.HeightmapChunk;
import mod.bespectacled.modernbetaforge.util.noise.PerlinOctaveNoise;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeProvider;
import mod.bespectacled.modernbetaforge.world.biome.ModernBetaBiomeStructures;
import mod.bespectacled.modernbetaforge.world.biome.beta.BiomeBetaDesert;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.gen.ModernBetaWorldType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
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
    public static final String VERSION = "1.0.1.2";
    public static final String MCVERSION = "1.12.2";

    private static Logger logger = LogManager.getLogger(MODID);
    
    public static void log(Level level, String message) {
        logger.log(level, "{}", message);
    }
    
    @SidedProxy(
        clientSide = "mod.bespectacled.modernbetaforge.ModernBetaClientProxy",
        serverSide = "mod.bespectacled.modernbetaforge.ModernBetaCommonProxy"
    )
    public static ModernBetaProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { }

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
        int spawnX = currentSpawnPos.getX();
        int spawnZ = currentSpawnPos.getZ();
        
        if (chunkGenerator instanceof ModernBetaChunkGenerator && biomeProvider instanceof ModernBetaBiomeProvider) {
            ChunkSource chunkSource = ((ModernBetaChunkGenerator)chunkGenerator).getChunkSource();
            BiomeSource biomeSource = ((ModernBetaBiomeProvider)biomeProvider).getBiomeSource();
            
            if (chunkSource instanceof NoiseChunkSource && ((NoiseChunkSource)chunkSource).getBeachOctaveNoise().isPresent()) {
                NoiseChunkSource noiseChunkSource = (NoiseChunkSource)chunkSource;
                PerlinOctaveNoise beachOctaveNoise = noiseChunkSource.getBeachOctaveNoise().get();
                
                int x = 0;
                int z = 0;
                int attempts = 0;
                
                Random random = new Random();
                
                while(!this.isSandAt(x, z, chunkSource, biomeSource, beachOctaveNoise)) {
                    if (attempts > 10000) {
                        x = 0;
                        z = 0;
                        break;
                    }
                    
                    x += random.nextInt(64) - random.nextInt(64);
                    z += random.nextInt(64) - random.nextInt(64);
                    
                    attempts++;
                }
                
                int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE) + 1;
                
                BlockPos newSpawnPos = new BlockPos(x, y, z);
                if (!newSpawnPos.equals(currentSpawnPos)) {
                    worldServer.setSpawnPoint(new BlockPos(x, y, z));
                }
            } else {
                worldServer.setSpawnPoint(this.findSpawnInRadius(chunkSource, spawnX, spawnZ, 64));
            }
        }
    }
    
    private boolean isSandAt(int x, int z, ChunkSource chunkSource, BiomeSource biomeSource, PerlinOctaveNoise beachOctaveNoise) {
        int seaLevel = chunkSource.getSeaLevel();
        int y = chunkSource.getHeight(x, z, HeightmapChunk.Type.SURFACE);
        
        Biome biome = biomeSource.getBiome(x, y, z);
        
        return (biome instanceof BiomeBetaDesert && y >= seaLevel - 1) || (beachOctaveNoise.sample(x * 0.03125, z * 0.03125, 0.0) > 0.0 && y >= seaLevel - 1 && y < seaLevel + 2);
    }
    
    private BlockPos findSpawnInRadius(ChunkSource chunkSource, int x, int z, int radius) {
        int r2 = radius * radius;

        for (int dX = x - radius; dX < x + radius; ++dX) {
            for (int dZ = z - radius; dZ < z + radius; ++dZ) {
                double distance = MathUtil.distance(x, z, dX, dZ);
                
                if (distance < r2) {
                    int y = chunkSource.getHeight(dX, dZ, HeightmapChunk.Type.SURFACE);
                    
                    if (y > 16) {
                        return new BlockPos(dX, y + 1, dZ);
                    }
                }
            }
        }
        
        return findSpawnInRadius(chunkSource, x, z, radius * 2);
    }
}
