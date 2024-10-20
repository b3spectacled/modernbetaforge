package mod.bespectacled.modernbetaforge.util.datafix;

import mod.bespectacled.modernbetaforge.ModernBeta;
import mod.bespectacled.modernbetaforge.util.datafix.ModDataFixers.ModDataFix;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ModDataFixer {
    public static final ModDataFixer INSTANCE = new ModDataFixer();
    
    private final CompoundDataFixer forgeDataFixer;
    private final ModFixs modFixer;
    
    private ModDataFixer() {
        this.forgeDataFixer = FMLCommonHandler.instance().getDataFixer();
        this.modFixer = this.forgeDataFixer.init(ModernBeta.MODID, ModernBeta.DATA_VERSION);
    }
    
    public void registerModDataFixes() {
        this.registerModDataFix(ModDataFixers.BIOME_MAP_FIX);
    }
    
    private void registerModDataFix(ModDataFix fix) {
        this.modFixer.registerFix(fix.getFixType(), fix.getFixableData());
    }
}
