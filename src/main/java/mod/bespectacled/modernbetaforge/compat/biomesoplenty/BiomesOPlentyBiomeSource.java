package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import biomesoplenty.common.init.ModBiomes;
import biomesoplenty.common.world.BOPWorldSettings;
import biomesoplenty.common.world.BiomeProviderBOP;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.source.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.util.chunk.BiomeChunk;
import mod.bespectacled.modernbetaforge.util.chunk.ChunkCache;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.Biome;

public class BiomesOPlentyBiomeSource extends BiomeSource implements NoiseBiomeSource {
    private final ChunkCache<BiomeChunk> biomeCache;
    private BiomeProviderBOP biomeProviderBOP;
    
    public BiomesOPlentyBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
        
        this.biomeCache = new ChunkCache<>("bop_biomes", (chunkX, chunkZ) -> new BiomeChunk(chunkX, chunkZ,this::getBiomes));
    }

    @Override
    public Biome getBiome(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        
        return this.biomeCache.get(chunkX, chunkZ).sample(x, z);
    }
    
    private Biome[] getBiomes(int startX, int startZ) {
        MutableBlockPos mutablePos = new MutableBlockPos();
        Biome[] biomes = new Biome[256];
        
        if (this.biomeProviderBOP == null) {
            BOPWorldSettings bopWorldSettings = createBOPWorldSettings(this.settings);
            
            this.biomeProviderBOP = new BiomeProviderBOP(this.seed, ModBiomes.worldTypeBOP, bopWorldSettings.toJson());
        }
        
        for (int localZ = 0; localZ < 16; ++localZ) {
            for (int localX = 0; localX < 16; ++localX) {
                int x = startX + localX;
                int z = startZ + localZ;
                
                biomes[localX + localZ * 16] = this.biomeProviderBOP.getBiome(mutablePos.setPos(x, 0, z));
            }
        }
        
        return biomes;
    }

    private static BOPWorldSettings createBOPWorldSettings(ModernBetaGeneratorSettings settings) {
        BOPWorldSettings bopWorldSettings = new BOPWorldSettings();

        String biomeSize = settings.getListProperty(CompatBiomesOPlenty.KEY_BIOME_SIZE).toUpperCase();
        String landScheme = settings.getListProperty(CompatBiomesOPlenty.KEY_LAND_SCHEME).toUpperCase();
        String tempScheme = settings.getListProperty(CompatBiomesOPlenty.KEY_TEMP_SCHEME).toUpperCase();
        String rainScheme = settings.getListProperty(CompatBiomesOPlenty.KEY_RAIN_SCHEME).toUpperCase();
        
        bopWorldSettings.biomeSize = BOPWorldSettings.BiomeSize.valueOf(biomeSize);
        bopWorldSettings.landScheme = BOPWorldSettings.LandMassScheme.valueOf(landScheme);
        bopWorldSettings.tempScheme = BOPWorldSettings.TemperatureVariationScheme.valueOf(tempScheme);
        bopWorldSettings.rainScheme = BOPWorldSettings.RainfallVariationScheme.valueOf(rainScheme);
        
        return bopWorldSettings;
    }
}
