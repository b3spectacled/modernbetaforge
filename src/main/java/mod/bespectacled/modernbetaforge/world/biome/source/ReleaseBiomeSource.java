package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.storage.WorldInfo;

public class ReleaseBiomeSource extends BiomeSource {
    private final BiomeProvider biomeProvider;

    public ReleaseBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);

        // Create new world info with Customized world type,
        // so biome provider will accept custom biome sizes
        ChunkGeneratorSettings.Factory factory = new ChunkGeneratorSettings.Factory();
        ModernBetaChunkGeneratorSettings settings = ModernBetaChunkGeneratorSettings.buildSettings(worldInfo.getGeneratorOptions());
        
        factory.biomeSize = settings.biomeSize;
        factory.riverSize = settings.riverSize;
        
        WorldInfo newWorldInfo = new WorldInfo(worldInfo);
        newWorldInfo.populateFromWorldSettings(new WorldSettings(worldInfo).setGeneratorOptions(factory.toString()));
        newWorldInfo.setTerrainType(WorldType.CUSTOMIZED);
        
        this.biomeProvider = new BiomeProvider(newWorldInfo);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biomeProvider.getBiome(new BlockPos(x, 0 , z));
    }
}
