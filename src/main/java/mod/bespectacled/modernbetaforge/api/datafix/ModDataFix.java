package mod.bespectacled.modernbetaforge.api.datafix;

import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixType;
import net.minecraft.util.datafix.IFixableData;

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
