package mod.bespectacled.modernbetaforge.world.biome.layer;

import mod.bespectacled.modernbetaforge.world.biome.layer.custom.GenLayerAddMoreSnow;
import mod.bespectacled.modernbetaforge.world.biome.layer.custom.GenLayerBiomeExtended;
import mod.bespectacled.modernbetaforge.world.biome.layer.custom.GenLayerOceanless;
import mod.bespectacled.modernbetaforge.world.biome.layer.custom.GenLayerOceanlessMushroom;
import mod.bespectacled.modernbetaforge.world.biome.layer.custom.GenLayerSmallIslands;
import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerAddIsland;
import net.minecraft.world.gen.layer.GenLayerAddMushroomIsland;
import net.minecraft.world.gen.layer.GenLayerAddSnow;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerDeepOcean;
import net.minecraft.world.gen.layer.GenLayerEdge;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerHills;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerRareBiome;
import net.minecraft.world.gen.layer.GenLayerRemoveTooMuchOcean;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerRiverInit;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraft.world.gen.layer.GenLayerShore;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class ModernBetaGenLayer {
    public static GenLayer[] initBiomeLayers(long seed, WorldType worldType, ModernBetaGeneratorSettings settings) {
        ChunkGeneratorSettings vanillaSettings = new ChunkGeneratorSettings.Factory().build();
        int biomeSize = settings.biomeSize;
        
        GenLayer genLayer = new GenLayerOceanless(1L);
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
    
    public static GenLayer[] initNoiseLayers(long seed, WorldType worldType, ChunkGeneratorSettings vanillaSettings, ModernBetaGeneratorSettings settings) {
        int biomeSize = settings.layerSize;
        int riverSize = settings.riverSize;
        
        GenLayer genLayer = createInitialLayer(settings);
        genLayer = new GenLayerAddMushroomIsland(5L, genLayer);
        genLayer = new GenLayerDeepOcean(4L, genLayer);
        genLayer = GenLayerZoom.magnify(1000L, genLayer, 0);
        GenLayer genLayerMutation = GenLayerZoom.magnify(1000L, genLayer, 0);
        genLayerMutation = new GenLayerRiverInit(100L, genLayerMutation);
        GenLayer genLayerMutation2 = GenLayerZoom.magnify(1000L, genLayerMutation, 2);
        genLayerMutation = GenLayerZoom.magnify(1000L, genLayerMutation, 2);
        genLayerMutation = GenLayerZoom.magnify(1000L, genLayerMutation, riverSize);
        genLayerMutation = new GenLayerRiver(1L, genLayerMutation);
        genLayerMutation = new GenLayerSmooth(1000L, genLayerMutation);
        genLayer = worldType.getBiomeLayer(seed, genLayer, vanillaSettings);
        genLayer = new GenLayerHills(1000L, genLayer, genLayerMutation2);
        genLayer = new GenLayerRareBiome(1001L, genLayer);
        
        for (int i = 0; i < biomeSize; ++i) {
            genLayer = new GenLayerZoom((long)(1000 + i), genLayer);
            
            if (i == 0) {
                genLayer = new GenLayerAddIsland(3L, genLayer);
            }

            if (i == 1 || biomeSize == 1) {
                genLayer = new GenLayerShore(1000L, genLayer);
            }
        }
        
        GenLayer genLayerNoise = new GenLayerSmooth(1000L, genLayer);
        genLayerNoise = new GenLayerRiverMix(100L, genLayerNoise, genLayerMutation);
        GenLayer genLayerVoronoi = new GenLayerVoronoiZoom(10L, genLayerNoise);
        genLayerNoise.initWorldGenSeed(seed);
        genLayerVoronoi.initWorldGenSeed(seed);
        
        return new GenLayer[] { genLayerNoise, genLayerVoronoi, genLayerNoise };
    }
    
    private static GenLayer createInitialLayer(ModernBetaGeneratorSettings settings) {
        GenLayerType type = GenLayerType.fromId(settings.layerType);
        
        GenLayer genLayer = new GenLayerIsland(1L);
        switch (type) {
            case SMALL_ISLANDS:
                genLayer = new GenLayerSmallIslands(1L);
                genLayer = addClimateLayers(genLayer);
                break;
            case ISLANDS:
                genLayer = new GenLayerAddSnow(2L, genLayer);
                genLayer = addClimateLayers(genLayer);
                break;
            case CONTINENTAL:
                genLayer = new GenLayerFuzzyZoom(2000L, genLayer);
                genLayer = new GenLayerAddIsland(1L, genLayer);
                genLayer = new GenLayerZoom(2001L, genLayer);
                genLayer = new GenLayerAddIsland(2L, genLayer);
                genLayer = new GenLayerAddSnow(2L, genLayer);
                genLayer = addClimateLayers(genLayer);
                genLayer = new GenLayerZoom(2002L, genLayer);
                genLayer = new GenLayerAddIsland(3L, genLayer);
                genLayer = new GenLayerZoom(2003L, genLayer);
                genLayer = new GenLayerAddIsland(4L, genLayer);
                break;
            default:
                genLayer = new GenLayerFuzzyZoom(2000L, genLayer);
                genLayer = new GenLayerAddIsland(1L, genLayer);
                genLayer = new GenLayerZoom(2001L, genLayer);
                genLayer = new GenLayerAddIsland(2L, genLayer);
                genLayer = new GenLayerAddIsland(50L, genLayer);
                genLayer = new GenLayerAddIsland(70L, genLayer);
                genLayer = new GenLayerRemoveTooMuchOcean(2L, genLayer);
                genLayer = new GenLayerAddSnow(2L, genLayer);
                genLayer = new GenLayerAddIsland(3L, genLayer);
                genLayer = addClimateLayers(genLayer);
                genLayer = new GenLayerZoom(2002L, genLayer);
                genLayer = new GenLayerZoom(2003L, genLayer);
                genLayer = new GenLayerAddIsland(4L, genLayer);
        }
        
        return genLayer;
    }
    
    private static GenLayer addClimateLayers(GenLayer genLayer) {
        genLayer = new GenLayerEdge(2L, genLayer, GenLayerEdge.Mode.COOL_WARM);
        genLayer = new GenLayerEdge(2L, genLayer, GenLayerEdge.Mode.HEAT_ICE);
        genLayer = new GenLayerEdge(3L, genLayer, GenLayerEdge.Mode.SPECIAL);
        
        return genLayer;
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
