package mod.bespectacled.modernbetaforge.world.biome.source;

import mod.bespectacled.modernbetaforge.api.world.biome.BiomeSource;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SingleBiomeSource extends BiomeSource {
    private final Biome biome;

    public SingleBiomeSource(WorldInfo worldInfo) {
        super(worldInfo);
        
        ModernBetaChunkGeneratorSettings settings = worldInfo.getGeneratorOptions() != null ?
            ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(worldInfo.getGeneratorOptions()).build() :
            new ModernBetaChunkGeneratorSettings.Factory().build();
        
        this.biome = ForgeRegistries.BIOMES.getValue(new ResourceLocation(settings.fixedBiome));
    }

    @Override
    public Biome getBiome(int x, int z) {
        return this.biome;
    }
}
