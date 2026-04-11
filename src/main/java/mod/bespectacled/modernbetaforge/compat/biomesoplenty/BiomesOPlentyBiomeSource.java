package mod.bespectacled.modernbetaforge.compat.biomesoplenty;

import biomesoplenty.common.init.ModBiomes;
import biomesoplenty.common.world.BOPWorldSettings;
import biomesoplenty.common.world.BiomeProviderBOP;
import mod.bespectacled.modernbetaforge.api.world.biome.source.BiomeSource;
import mod.bespectacled.modernbetaforge.api.world.biome.source.NoiseBiomeSource;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class BiomesOPlentyBiomeSource extends BiomeSource implements NoiseBiomeSource {
    private BiomeProviderBOP biomeProviderBOP;
    
    public BiomesOPlentyBiomeSource(long seed, ModernBetaGeneratorSettings settings) {
        super(seed, settings);
    }

    @Override
    public Biome getBiome(int x, int z) {
        if (this.biomeProviderBOP == null) {
            BOPWorldSettings bopWorldSettings = createBOPWorldSettings(this.settings);
            
            this.biomeProviderBOP = new BiomeProviderBOP(this.seed, ModBiomes.worldTypeBOP, bopWorldSettings.toJson());
        }
        
        return this.biomeProviderBOP.getBiome(new BlockPos(x, 0, z));
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
