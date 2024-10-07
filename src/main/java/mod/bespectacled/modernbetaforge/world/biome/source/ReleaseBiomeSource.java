package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;

public class ReleaseBiomeSource extends BiomeSource {
    private final BiomeProvider biomeProvider;

    public ReleaseBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);

        this.biomeProvider = new BiomeProvider(worldInfo);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biomeProvider.getBiome(new BlockPos(x, 0 , z));
    }
}
