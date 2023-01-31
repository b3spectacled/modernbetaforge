package mod.bespectacled.modernbetaforge.api.world.chunk;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGenerator;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaNoiseSettings;
import mod.bespectacled.modernbetaforge.world.chunk.source.AlphaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.BetaChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.Infdev415ChunkSource;
import mod.bespectacled.modernbetaforge.world.chunk.source.SkylandsChunkSource;
import net.minecraft.world.World;

public enum ChunkSourceType {
    BETA("beta", "Beta"),
    ALPHA("alpha", "Alpha"),
    SKYLANDS("skylands", "Skylands"),
    INFDEV_415("infdev_415", "Infdev 415");

    private final String id;
    private final String name;
    
    private ChunkSourceType(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ChunkSource create(World world, ModernBetaChunkGenerator chunkGenerator, ModernBetaChunkGeneratorSettings chunkGeneratorSettings, long seed, boolean mapFeaturesEnabled) {
        switch(this) {
            case BETA: return new BetaChunkSource(world, chunkGenerator, chunkGeneratorSettings, seed, mapFeaturesEnabled, ModernBetaNoiseSettings.BETA);
            case ALPHA: return new AlphaChunkSource(world, chunkGenerator, chunkGeneratorSettings, seed, mapFeaturesEnabled, ModernBetaNoiseSettings.ALPHA);
            case SKYLANDS: return new SkylandsChunkSource(world, chunkGenerator, chunkGeneratorSettings, seed, mapFeaturesEnabled, ModernBetaNoiseSettings.SKYLANDS);
            case INFDEV_415: return new Infdev415ChunkSource(world, chunkGenerator, chunkGeneratorSettings, seed, mapFeaturesEnabled, ModernBetaNoiseSettings.INFDEV_415);
            default: return new BetaChunkSource(world, chunkGenerator, chunkGeneratorSettings, seed, mapFeaturesEnabled, ModernBetaNoiseSettings.BETA);
        }
    }
    
    public static ChunkSourceType fromId(String id) throws IllegalArgumentException {
        for (ChunkSourceType type : ChunkSourceType.values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("[Modern Beta] Invalid chunk source id!");
    }
}
