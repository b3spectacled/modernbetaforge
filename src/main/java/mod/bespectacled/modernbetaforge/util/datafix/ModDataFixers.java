package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public class ModDataFixers {
    private static final int DATA_VERSION_V1_1_0_0 = 1100;
    private static final int DATA_VERSION_V1_2_0_0 = 1200;
    private static final int DATA_VERSION_V1_2_2_2 = 1222;
    private static final int DATA_VERSION_V1_3_0_0 = 1300;
    private static final int DATA_VERSION_V1_3_1_0 = 1310;
    
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
    
    public static final ModDataFix BIOME_MAP_FIX = new ModDataFix(
        FixTypes.LEVEL,
        new IFixableData() {
            @Override
            public int getFixVersion() {
                return DATA_VERSION_V1_1_0_0;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                List<String> registryKeys = Arrays.asList(
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
                    DataFixTags.FIX_SINGLE_BIOME
                );
                
                return fixGeneratorSettings(compound, registryKeys);
            }
        }
    );
    
    public static final ModDataFix SANDSTONE_WOLVES_SURFACE_FIX = new ModDataFix(
        FixTypes.LEVEL,
        new IFixableData() {
            @Override
            public int getFixVersion() {
                return DATA_VERSION_V1_2_0_0;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                List<String> registryKeys = Arrays.asList(
                    DataFixTags.USE_SANDSTONE,
                    DataFixTags.SPAWN_WOLVES,
                    DataFixTags.SURFACE_BUILDER,

                    // Added in 1.3.0.0 to replace fixedBiome
                    DataFixTags.FIX_SINGLE_BIOME
                );
                
                return fixGeneratorSettings(compound, registryKeys);
            }
        }
    );
    
    public static final ModDataFix SKYLANDS_SURFACE_FIX = new ModDataFix(
        FixTypes.LEVEL,
        new IFixableData() {
            @Override
            public int getFixVersion() {
                return DATA_VERSION_V1_2_2_2;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                List<String> registryKeys = Arrays.asList(
                    DataFixTags.SURFACE_SKYLANDS,

                    // Added in 1.3.0.0 to replace fixedBiome
                    DataFixTags.FIX_SINGLE_BIOME
                );
                
                return fixGeneratorSettings(compound, registryKeys);
            }
        }
    );
    
    public static final ModDataFix SINGLE_BIOME_FIX = new ModDataFix(
        FixTypes.LEVEL,
        new IFixableData() {
            @Override
            public int getFixVersion() {
                return DATA_VERSION_V1_3_0_0;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                List<String> registryKeys = Arrays.asList(DataFixTags.FIX_SINGLE_BIOME);
                
                return fixGeneratorSettings(compound, registryKeys);
            }
        }
    );
    
    public static final ModDataFix INDEV_HOUSE_FIX = new ModDataFix(
        FixTypes.LEVEL,
        new IFixableData() {
            @Override
            public int getFixVersion() {
                return DATA_VERSION_V1_3_1_0;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                List<String> registryKeys = Arrays.asList(DataFixTags.FIX_USE_INDEV_HOUSE);
                
                return fixGeneratorSettings(compound, registryKeys);
            }
        }
    );
    
    private static NBTTagCompound fixGeneratorSettings(NBTTagCompound compound, List<String> registryKeys) {
        String worldName = getWorldName(compound);
        
        if (isModernBetaWorld(compound) && compound.hasKey("generatorOptions")) {
            String generatorOptions = compound.getString("generatorOptions");
            ModernBetaChunkGeneratorSettings.Factory factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(generatorOptions);
            
            JsonObject jsonObject;
            try {
                jsonObject = new JsonParser().parse(generatorOptions).getAsJsonObject();
            } catch (Exception e) {
                jsonObject = new JsonObject();      
            }
            
            for (String registryKey : registryKeys) {
                DataFixer.runDataFixer(registryKey, factory, jsonObject, worldName);
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
