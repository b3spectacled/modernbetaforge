package mod.bespectacled.modernbetaforge.api.datafix;

import mod.bespectacled.modernbetaforge.api.registry.ModernBetaRegistries;
import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.ModFixs;

public class ModDataFix {
    private final IFixType fixType;
    private final IFixableData fixableData;
    
    private ModDataFix(IFixType fixType, IFixableData fixableData) {
        this.fixType = fixType;
        this.fixableData = fixableData;
    }
    
    public IFixType getFixType() {
        return this.fixType;
    }
    
    public IFixableData getFixableData() {
        return this.fixableData;
    }
    
    /**
     * Creates a ModDataFix for the Forge {@link ModFixs}.
     * 
     * @param fixVersion The integer data version for the mod.
     * @param dataFixes An array of {@link DataFix datafixes} for the target data version.
     * 
     * @return A ModDataFix to be registered with {@link ModernBetaRegistries#MOD_DATA_FIX}. 
     */
    public static ModDataFix createModDataFix(int fixVersion, DataFix... dataFixes) {
        return new ModDataFix(
            FixTypes.LEVEL,
            new IFixableData() {
                @Override
                public int getFixVersion() {
                    return fixVersion;
                }

                @Override
                public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                    return ModDataFixer.fixGeneratorSettings(compound, dataFixes, this.getFixVersion());
                }
            }
        );
    }
}
