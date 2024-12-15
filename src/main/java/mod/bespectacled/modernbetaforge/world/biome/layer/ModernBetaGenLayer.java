package mod.bespectacled.modernbetaforge.world.biome.layer;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerEdge;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerHills;
import net.minecraft.world.gen.layer.GenLayerRareBiome;
import net.minecraft.world.gen.layer.GenLayerRiverInit;
import net.minecraft.world.gen.layer.GenLayerShore;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class ModernBetaGenLayer {
    public static GenLayer[] initLayers(long seed, WorldType worldType, ModernBetaGeneratorSettings settings) {
        ChunkGeneratorSettings vanillaSettings = new ChunkGeneratorSettings.Factory().build();
        int biomeSize = settings.biomeSize;
        
        GenLayer genLayer = new GenLayerContinent(1L);
        genLayer = new GenLayerFuzzyZoom(2000L, genLayer);
        genLayer = new GenLayerZoom(2001L, genLayer);
        genLayer = new GenLayerAddMoreSnow(2L, genLayer);
        genLayer = new GenLayerEdge(2L, genLayer, GenLayerEdge.Mode.COOL_WARM);
        genLayer = new GenLayerEdge(2L, genLayer, GenLayerEdge.Mode.HEAT_ICE);
        genLayer = new GenLayerEdge(3L, genLayer, GenLayerEdge.Mode.SPECIAL);
        genLayer = new GenLayerZoom(2002L, genLayer);
        genLayer = new GenLayerZoom(2003L, genLayer);
        genLayer = GenLayerZoom.magnify(1000L, genLayer, 0);
        GenLayer genLayerMutation = GenLayerZoom.magnify(1000L, genLayer, 0);
        genLayerMutation = new GenLayerRiverInit(100L, genLayerMutation);
        genLayerMutation = GenLayerZoom.magnify(1000L, genLayerMutation, 2);
        genLayer = getBiomeLayer(seed, genLayer, worldType, vanillaSettings, settings);
        genLayer = new GenLayerOceanlessMushroom(5L, genLayer);
        genLayer = new GenLayerHills(1000L, genLayer, genLayerMutation);
        genLayer = new GenLayerRareBiome(1001L, genLayer);
        
        for (int i = 0; i < biomeSize; ++i) {
            genLayer = new GenLayerZoom((long)(1000 + i), genLayer);

            if (i == 1 || biomeSize == 1) {
                genLayer = new GenLayerShore(1000L, genLayer);
            }
        }
        
        GenLayer genLayerNoise = new GenLayerSmooth(1000L, genLayer);
        GenLayer genLayerVoronoi = new GenLayerVoronoiZoom(10L, genLayerNoise);
        genLayerNoise.initWorldGenSeed(seed);
        genLayerVoronoi.initWorldGenSeed(seed);
        
        return new GenLayer[] { genLayerNoise, genLayerVoronoi, genLayerNoise };
    }
    
    private static GenLayer getBiomeLayer(
        long seed,
        GenLayer parent,
        WorldType worldType,
        ChunkGeneratorSettings vanillaSettings,
        ModernBetaGeneratorSettings settings
    ) {
        GenLayer biomeLayer = new GenLayerBiomeExtended(200L, parent, worldType, vanillaSettings, settings);
        biomeLayer = GenLayerZoom.magnify(1000L, biomeLayer, 2);
        biomeLayer = new GenLayerBiomeEdge(1000L, biomeLayer);
        
        return biomeLayer;
    }
}
