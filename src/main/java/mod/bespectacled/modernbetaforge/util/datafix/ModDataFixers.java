package mod.bespectacled.modernbetaforge.util.datafix;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.util.NbtTags;
import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public class ModDataFixers {
    private static final int DATA_VERSION_V1_1_0_0 = 1100;
    private static final int DATA_VERSION_V1_2_0_0 = 1200;
    
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
                List<String> dataFixKeys = Arrays.asList(
                    NbtTags.DESERT_BIOMES,
                    NbtTags.FOREST_BIOMES,
                    NbtTags.ICE_DESERT_BIOMES,
                    NbtTags.PLAINS_BIOMES,
                    NbtTags.RAINFOREST_BIOMES,
                    NbtTags.SAVANNA_BIOMES,
                    NbtTags.SHRUBLAND_BIOMES,
                    NbtTags.SEASONAL_FOREST_BIOMES,
                    NbtTags.SWAMPLAND_BIOMES,
                    NbtTags.TAIGA_BIOMES,
                    NbtTags.TUNDRA_BIOMES
                );
                
                return fixGeneratorSettings(compound, dataFixKeys);
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
                List<String> dataFixKeys = Arrays.asList(NbtTags.USE_SANDSTONE, NbtTags.SPAWN_WOLVES, NbtTags.SURFACE_BUILDER);
                
                return fixGeneratorSettings(compound, dataFixKeys);
            }
        }
    );
    
    private static NBTTagCompound fixGeneratorSettings(NBTTagCompound compound, List<String> dataFixKeys) {
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
            
            for (String key : dataFixKeys) {
                DataFixer.runDataFixer(key, factory, jsonObject, worldName);
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
