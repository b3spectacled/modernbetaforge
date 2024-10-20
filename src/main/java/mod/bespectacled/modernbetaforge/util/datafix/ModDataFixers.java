package mod.bespectacled.modernbetaforge.util.datafix;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mod.bespectacled.modernbetaforge.world.chunk.ModernBetaChunkGeneratorSettings;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

public class ModDataFixers {
    private static final int BIOME_MAP_FIX_ID = 1;
    
    /*
     * Reference: https://gist.github.com/JoshieGemFinder/982830b6d66fccec04c1d1912ca76246
     * 
     * Fix version appears to increment for each new fix,
     * regardless of type.
     * 
     * Fix version should not be confused/aligned with mod data version.
     * Fixes will run on data processed by an earlier mod data version of the DataFixer,
     * but not by a later mod data version of the DataFixer.
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
                return BIOME_MAP_FIX_ID;
            }

            @Override
            public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                if (compound.hasKey("generatorOptions")) {
                    String generatorOptions = compound.getString("generatorOptions");
                    ModernBetaChunkGeneratorSettings.Factory factory = ModernBetaChunkGeneratorSettings.Factory.jsonToFactory(generatorOptions);
                    
                    JsonObject jsonObject;
                    try {
                        jsonObject = new JsonParser().parse(generatorOptions).getAsJsonObject();
                    } catch (Exception e) {
                        jsonObject = new JsonObject();
                    }
                    
                    DataFixer.runDataFixer(factory, jsonObject);
                    compound.setString("generatorOptions", factory.toString().replace("\n", ""));
                }
                    
                return compound;
            }
        }
    );
    
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
