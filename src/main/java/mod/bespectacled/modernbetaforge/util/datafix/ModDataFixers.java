package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.world.setting.ModernBetaGeneratorSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public class ModDataFixers {
    private static final int DATA_VERSION_V1_1_0_0 = 1100;
    private static final int DATA_VERSION_V1_2_0_0 = 1200;
    private static final int DATA_VERSION_V1_2_2_2 = 1222;
    private static final int DATA_VERSION_V1_3_0_0 = 1300;
    private static final int DATA_VERSION_V1_3_1_0 = 1310;
    private static final int DATA_VERSION_V1_4_0_0 = 1400;
    private static final int DATA_VERSION_V1_5_0_0 = 1500;
    private static final int DATA_VERSION_V1_5_2_0 = 1520;
    private static final int DATA_VERSION_V1_6_0_0 = 1600;
    private static final int DATA_VERSION_V1_6_1_0 = 1610;
    private static final int DATA_VERSION_V1_7_0_0 = 1700;
    private static final int DATA_VERSION_V1_7_1_0 = 1710;
    
    /*
     * Reference: https://gist.github.com/JoshieGemFinder/982830b6d66fccec04c1d1912ca76246
     * 
     * ModDataFix version should target the mod's data version where data should be fixed.
     * Fixes will run on data processed by an earlier mod data version of the DataFixer,
     * but not by a later mod data version of the DataFixer. 
     * After being processed, the file will be updated to the mod's current data version.
     * 
     * Ex:
     * level.dat/Data/ForgeDataVersion/modernbetaforge value
     * will be set to what the mod data version provided to the DataFixer is. 
     * 
     */
    
    public static final ModDataFix BIOME_MAP_FIX = createModDataFix(
        DATA_VERSION_V1_1_0_0,
        DataFixTags.DESERT_BIOMES,
        DataFixTags.FOREST_BIOMES,
        DataFixTags.ICE_DESERT_BIOMES,
        DataFixTags.PLAINS_BIOMES,
        DataFixTags.RAINFOREST_BIOMES,
        DataFixTags.SAVANNA_BIOMES,
        DataFixTags.SHRUBLAND_BIOMES,
        DataFixTags.SEASONAL_FOREST_BIOMES,
        DataFixTags.SWAMPLAND_BIOMES,
        DataFixTags.TAIGA_BIOMES,
        DataFixTags.TUNDRA_BIOMES,
        
        // Added in 1.3.0.0 to replace fixedBiome
        DataFixTags.FIX_SINGLE_BIOME,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );

    public static final ModDataFix SANDSTONE_WOLVES_SURFACE_FIX = createModDataFix(
        DATA_VERSION_V1_2_0_0,
        DataFixTags.USE_SANDSTONE,
        DataFixTags.SPAWN_WOLVES,
        DataFixTags.SURFACE_BUILDER,
        DataFixTags.FIX_BIOME_DEPTH_SCALE,

        // Added in 1.3.0.0 to replace fixedBiome
        DataFixTags.FIX_SINGLE_BIOME,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix SKYLANDS_SURFACE_FIX = createModDataFix(
        DATA_VERSION_V1_2_2_2,
        DataFixTags.SURFACE_SKYLANDS,

        // Added in 1.3.0.0 to replace fixedBiome
        DataFixTags.FIX_SINGLE_BIOME,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix SINGLE_BIOME_FIX = createModDataFix(
        DATA_VERSION_V1_3_0_0,
        DataFixTags.FIX_SINGLE_BIOME,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix INDEV_HOUSE_FIX = createModDataFix(
        DATA_VERSION_V1_3_1_0,
        DataFixTags.FIX_USE_INDEV_HOUSE,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix RESOURCE_LOCATION_FIX = createModDataFix(
        DATA_VERSION_V1_4_0_0,
        DataFixTags.FIX_RESOURCE_LOCATION_CHUNK,
        DataFixTags.FIX_RESOURCE_LOCATION_BIOME,
        DataFixTags.FIX_RESOURCE_LOCATION_SURFACE,
        DataFixTags.FIX_RESOURCE_LOCATION_CARVER,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix SCALE_NOISE_FIX = createModDataFix(
        DATA_VERSION_V1_4_0_0,
        DataFixTags.FIX_SCALE_NOISE_SCALE_X,
        DataFixTags.FIX_SCALE_NOISE_SCALE_Z,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix LAYER_SIZE_FIX = createModDataFix(
        DATA_VERSION_V1_5_0_0,
        DataFixTags.FIX_LAYER_SIZE,
        
        // Added in 1.5.2.0 to replace useCaves
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );

    public static final ModDataFix CAVE_CARVER_NONE_FIX = createModDataFix(
        DATA_VERSION_V1_5_2_0,
        DataFixTags.FIX_CAVE_CARVER_NONE,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );

    public static final ModDataFix SPAWN_LOCATOR_FIX = createModDataFix(
        DATA_VERSION_V1_5_2_0,
        DataFixTags.FIX_WORLD_SPAWNER,
        
        // Added in 1.6.0.0 to replace useLavaOceans
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );

    public static final ModDataFix DEFAULT_FLUID_FIX = createModDataFix(
        DATA_VERSION_V1_6_0_0,
        DataFixTags.FIX_DEFAULT_FLUID,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix DISKS_FIX = createModDataFix(
        DATA_VERSION_V1_6_1_0,
        DataFixTags.FIX_SAND_DISKS,
        DataFixTags.FIX_GRAVEL_DISKS,
        DataFixTags.FIX_CLAY_DISKS,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix DOUBLE_PLANT_FIX = createModDataFix(
        DATA_VERSION_V1_6_1_0,
        DataFixTags.FIX_DOUBLE_PLANTS,
        
        // Added in 1.7.0.0 to replace useModdedBiomes
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix LAYER_VERSION_FIX = createModDataFix(
        DATA_VERSION_V1_7_0_0,
        DataFixTags.FIX_SNOWY_BIOME_CHANCE,
        DataFixTags.FIX_LAYER_VERSION_1600,
        DataFixTags.FIX_BOP_COMPAT
    );
    
    public static final ModDataFix RIVER_BIOMES_FIX = createModDataFix(
        DATA_VERSION_V1_7_1_0,
        DataFixTags.FIX_REPLACE_RIVER_BIOMES
    );
    
    private static ModDataFix createModDataFix(int fixVersion, ResourceLocation... dataFixTags) {
        return new ModDataFix(
            FixTypes.LEVEL,
            new IFixableData() {
                @Override
                public int getFixVersion() {
                    return fixVersion;
                }

                @Override
                public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                    return fixGeneratorSettings(compound, Arrays.asList(dataFixTags), this.getFixVersion());
                }
            }
        );
    }
    
    private static NBTTagCompound fixGeneratorSettings(NBTTagCompound compound, List<ResourceLocation> registryKeys, int fixVersion) {
        String worldName = getWorldName(compound);
        
        if (isModernBetaWorld(compound) && compound.hasKey("generatorOptions")) {
            String generatorOptions = compound.getString("generatorOptions");
            ModernBetaGeneratorSettings.Factory factory = ModernBetaGeneratorSettings.Factory.jsonToFactory(generatorOptions);
            
            JsonObject jsonObject;
            try {
                jsonObject = new JsonParser().parse(generatorOptions).getAsJsonObject();
            } catch (Exception e) {
                jsonObject = new JsonObject();      
            }
            
            for (ResourceLocation registryKey : registryKeys) {
                DataFixer.runDataFixer(registryKey, factory, jsonObject, worldName, fixVersion);
            }
            
            compound.setString("generatorOptions", factory.toString().replace("\n", ""));
        }
        
        return compound;
    }
    
    private static boolean isModernBetaWorld(NBTTagCompound compound) {
        if (compound.hasKey("generatorName")) {
            return compound.getString("generatorName").equals("modernbeta");
        }
        
        return false;
    }
    
    private static String getWorldName(NBTTagCompound compound) {
        if (compound.hasKey("LevelName")) {
            return compound.getString("LevelName");
        }
        
        return "";
    }
    
    public static class ModDataFix {
        private final IFixType fixType;
        private final IFixableData fixableData;
        
        public ModDataFix(IFixType fixType, IFixableData fixableData) {
            this.fixType = fixType;
            this.fixableData = fixableData;
        }
        
        public IFixType getFixType() {
            return this.fixType;
        }
        
        public IFixableData getFixableData() {
            return this.fixableData;
        }
    }
}
